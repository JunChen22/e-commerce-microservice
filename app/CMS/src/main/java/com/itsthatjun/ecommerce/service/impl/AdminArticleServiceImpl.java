package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.exceptions.ArticleNotFoundException;
import com.itsthatjun.ecommerce.exceptions.ArticleSlugAlreadyExistException;
import com.itsthatjun.ecommerce.model.AdminArticleInfo;
import com.itsthatjun.ecommerce.dto.event.outgoing.ArticleUpdateEvent;
import com.itsthatjun.ecommerce.model.entity.*;
import com.itsthatjun.ecommerce.repository.*;
import com.itsthatjun.ecommerce.service.AdminArticleService;
import com.itsthatjun.ecommerce.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.itsthatjun.ecommerce.dto.event.outgoing.ArticleUpdateEvent.Type.*;

@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final ArticleQARepository qaRepository;

    private final ArticleVideoRepository videoRepository;

    private final ArticleImageRepository imageRepository;

    private final ArticleLogRepository logRepository;

    private final StreamBridge streamBridge;

    private final DTOMapper dtoMapper;

    private final String BINDING_NAME = "app-article-out-0";

    private final Random randomNumberGenerator = new Random();

    @Autowired
    public AdminArticleServiceImpl(ArticleRepository articleRepository, ArticleQARepository qaRepository,
                                   ArticleVideoRepository videoRepository, ArticleImageRepository imageRepository,
                                   ArticleLogRepository logRepository, StreamBridge streamBridge, DTOMapper dtoMapper) {
        this.articleRepository = articleRepository;
        this.qaRepository = qaRepository;
        this.videoRepository = videoRepository;
        this.imageRepository = imageRepository;
        this.logRepository = logRepository;
        this.streamBridge = streamBridge;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<AdminArticleInfo> listAllArticles() {
        return articleRepository.findAllByPublishStatus(PublishStatus.PUBLISHED.getValue())
                .flatMap(article -> {
                    int articleId = article.getId();
                    AdminArticleInfo articleInfo = new AdminArticleInfo(article);
                    Mono<List<ArticleImage>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<ArticleQa>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<ArticleVideo>> videosMono = fetchVideosForArticle(articleId);

                    return Mono.zip(imagesMono, qaMono, videosMono)
                            .map(tuple -> {
                                articleInfo.setImages(tuple.getT1());
                                articleInfo.setQA(tuple.getT2());
                                articleInfo.setVideos(tuple.getT3());
                                return articleInfo;  // Return the populated articleInfo
                            });
                })
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("No articles found.")));
    }

    @Override
    public Flux<AdminArticleInfo> listArticles(int page, int size) {
        int offset = page * size;
        return articleRepository.findAllByPublishStatusWithPagination(PublishStatus.PUBLISHED.getValue(), offset, size)
                .flatMap(article -> {
                    int articleId = article.getId();
                    AdminArticleInfo articleInfo = new AdminArticleInfo(article);
                    Mono<List<ArticleImage>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<ArticleQa>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<ArticleVideo>> videosMono = fetchVideosForArticle(articleId);

                    return Mono.zip(imagesMono, qaMono, videosMono)
                            .map(tuple -> {
                                articleInfo.setImages(tuple.getT1());
                                articleInfo.setQA(tuple.getT2());
                                articleInfo.setVideos(tuple.getT3());
                                return articleInfo;  // Return the populated articleInfo
                            });
                })
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("No articles found.")));
    }

    @Override
    public Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent) {
        return articleRepository.findById(articleId) // find
                .flatMap(article -> {
                    AdminArticleInfo articleInfo = new AdminArticleInfo(article);
                    Mono<List<ArticleImage>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<ArticleQa>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<ArticleVideo>> videosMono = fetchVideosForArticle(articleId);

                    return Mono.zip(imagesMono, qaMono, videosMono)
                            .flatMap(tuple -> {
                                articleInfo.setImages(tuple.getT1());
                                articleInfo.setQA(tuple.getT2());
                                articleInfo.setVideos(tuple.getT3());
                                return throwErrorIfBadLuck(articleInfo, faultPercent); // This now returns Mono<ArticleInfo>
                            });
                })
                .delayElement(Duration.ofMillis(delay))
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with id: " + articleId))); // Custom exception handling
    }

    /*
        use to test/simulate circuit breaker. fault percent.
    */
    private Mono<AdminArticleInfo> throwErrorIfBadLuck(AdminArticleInfo articleInfo, int faultPercent) {
        if (faultPercent == 0) {
            return Mono.just(articleInfo);
        }

        int randomThreshold = randomNumberGenerator.nextInt(100);

        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
            return Mono.just(articleInfo);
        } else {
            LOG.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            return Mono.error(new RuntimeException("Something went wrong..."));
        }
    }

    // Fetch Q&A for the article
    private Mono<List<ArticleQa>> fetchQaForArticle(int articleId) {
        return qaRepository.findAllByArticleId(articleId)
                .collectList();
    }

    // Fetch images for the article
    private Mono<List<ArticleImage>> fetchImagesForArticle(int articleId) {
        return imageRepository.findAllByArticleId(articleId)
                .collectList();
    }

    // Fetch videos for the article
    private Mono<List<ArticleVideo>> fetchVideosForArticle(int articleId) {
        return videoRepository.findAllByArticleId(articleId)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<AdminArticleInfo> createArticle(AdminArticleInfo articleInfo, String operator) {
        String slug = StringUtil.slugify(articleInfo.getTitle());

        return articleRepository.findAllBySlug(slug)
                .hasElements() // Check if slug already exists
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ArticleSlugAlreadyExistException("Article with slug already exists: " + slug));
                    }

                    Article articleEntity = dtoMapper.adminArticleInfoToArticle(articleInfo);
                    articleEntity.setSlug(slug);

                    return articleRepository.save(articleEntity)
                            .flatMap(savedArticle -> {
                                int articleId = savedArticle.getId();

                                articleInfo.setId(articleId);
                                articleInfo.getImages().forEach(image -> image.setArticleId(articleId));
                                articleInfo.getQA().forEach(qa -> qa.setArticleId(articleId));
                                articleInfo.getVideos().forEach(video -> video.setArticleId(articleId));

                                Mono<Void> saveImages = imageRepository.saveAll(articleInfo.getImages()).then();
                                Mono<Void> saveQa = qaRepository.saveAll(articleInfo.getQA()).then();
                                Mono<Void> saveVideos = videoRepository.saveAll(articleInfo.getVideos()).then();

                                ArticleInfo articleInfoDTO = dtoMapper.adminArticleInfoToArticleDTO(articleInfo);

                                return Mono.zip(saveImages, saveQa, saveVideos)
                                        .then(articleLog(articleId, "create article", operator)) // Log the operation
                                        .then(Mono.fromRunnable(() ->
                                                sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(CREATE, articleInfoDTO))
                                        ))
                                        .thenReturn(articleInfo); // Return the created articleInfo
                            });
                })
                .doOnError(error -> LOG.error("Failed to create article, error: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<AdminArticleInfo> updateArticle(AdminArticleInfo article, String operator) {
        return articleRepository.findById(article.getId())
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + article.getId())))
                .flatMap(foundArticle -> {
                    // Map the input article DTO to the entity to be updated
                    Article updatedArticle = dtoMapper.adminArticleInfoToArticle(article);
                    updatedArticle.setUpdatedAt(LocalDateTime.now());

                    // Save the updated article
                    return articleRepository.save(updatedArticle)
                            .flatMap(savedArticle -> {
                                int articleId = savedArticle.getId();

                                // Map images, QA, and videos to be saved
                                Mono<Void> handleImages = handleImages(article.getImages(), articleId);
                                Mono<Void> handleQA = handleQA(article.getQA(), articleId);
                                Mono<Void> handleVideos = handleVideos(article.getVideos(), articleId);

                                // Prepare ArticleInfo DTO for update event
                                ArticleInfo articleInfoDTO = dtoMapper.adminArticleInfoToArticleDTO(article);

                                // Combine all the operations and proceed with logging and sending the update event
                                return Mono.zip(handleImages, handleQA, handleVideos)
                                        .then(articleLog(articleId, "update article", operator)) // Log the update operation
                                        .then(Mono.fromRunnable(() ->
                                                sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(UPDATE, articleInfoDTO))
                                        ))
                                        .thenReturn(article); // Return the updated articleDTO
                            });
                })
                .doOnError(error -> LOG.error("Failed to update article with ID: {}, error: {}", article.getId(), error.getMessage()))
                .log();
    }

    private Mono<Void> handleImages(List<ArticleImage> updatedImages, int articleId) {
        // Handle deletions: Find images to remove that are not in the updated list by comparing IDs
        Mono<Void> deleteImages = imageRepository.findAllByArticleId(articleId)
                .filter(image -> updatedImages.stream()
                        .noneMatch(updatedImage -> updatedImage.getId().equals(image.getId()))) // Check if image ID doesn't exist in updated list
                .flatMap(imageRepository::delete) // Delete the images
                .then();

        // Handle additions/updates: Save new or modified images
        Mono<Void> saveImages = imageRepository.saveAll(updatedImages)
                .then();

        return Mono.zip(deleteImages, saveImages).then();
    }

    private Mono<Void> handleQA(List<ArticleQa> updatedQA, int articleId) {
        // Handle deletions: Find QAs to remove that are not in the updated list by comparing IDs
        Mono<Void> deleteQA = qaRepository.findAllByArticleId(articleId)
                .filter(qa -> updatedQA.stream()
                        .noneMatch(updatedQa -> updatedQa.getId().equals(qa.getId()))) // Check if QA ID doesn't exist in updated list
                .flatMap(qaRepository::delete) // Delete the QAs
                .then();

        // Handle additions/updates: Save new or modified QAs
        Mono<Void> saveQA = qaRepository.saveAll(updatedQA)
                .then();

        return Mono.zip(deleteQA, saveQA).then();
    }

    private Mono<Void> handleVideos(List<ArticleVideo> updatedVideos, int articleId) {
        // Handle deletions: Find videos to remove that are not in the updated list by comparing IDs
        Mono<Void> deleteVideos = videoRepository.findAllByArticleId(articleId)
                .filter(video -> updatedVideos.stream()
                        .noneMatch(updatedVideo -> updatedVideo.getId().equals(video.getId()))) // Check if video ID doesn't exist in updated list
                .flatMap(videoRepository::delete) // Delete the videos
                .then();

        // Handle additions/updates: Save new or modified videos
        Mono<Void> saveVideos = videoRepository.saveAll(updatedVideos)
                .then();

        return Mono.zip(deleteVideos, saveVideos).then();
    }

    @Override
    @Transactional
    public Mono<Void> deleteArticle(int articleId, String operator) {
        return articleRepository.findById(articleId)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleId)))
                .flatMap(article ->
                        // Delete child entities first
                        qaRepository.deleteByArticleId(article.getId())
                                .then(imageRepository.deleteByArticleId(article.getId()))
                                .then(videoRepository.deleteByArticleId(article.getId()))
                                .then(articleLog(articleId, "delete article", operator))
                                // Delete the parent article last
                                .then(articleRepository.delete(article))
                                // Send the update event after successful deletion
                                .then(Mono.defer(() -> {
                                    ArticleInfo articleInfoDTO = dtoMapper.articleToArticleDTO(article);
                                    return sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(DELETE, articleInfoDTO));
                                }))
                )
                .doOnSuccess(unused -> LOG.debug("Article deleted with ID: {}, by operator: {}", articleId, operator))
                .doOnError(error -> LOG.error("Failed to delete article with ID: {}, error: {}", articleId, error.getMessage()));
    }

    private Mono<Void> articleLog(int articleId, String updateAction, String operator) {
        ArticleChangeLog changeLog = new ArticleChangeLog();
        changeLog.setArticleId(articleId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setOperator(operator);
        return logRepository.save(changeLog).then();
    }

    // sending message to message queue then to App for cache update
    private Mono<Void> sendArticleUpdateMessage(String bindingName, ArticleUpdateEvent event) {
        return Mono.fromRunnable(() -> {
            LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
            Message message = MessageBuilder.withPayload(event)
                    .setHeader("event-type", event.getEventType())
                    .build();
            if (!streamBridge.send(bindingName, message)) {
                throw new RuntimeException("Failed to send message to " + bindingName);
            }
        });
    }
}