package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.enums.type.UpdateActionType;
import com.itsthatjun.ecommerce.exception.ArticleException;
import com.itsthatjun.ecommerce.exception.InvalidArticleStateException;
import com.itsthatjun.ecommerce.exception.ArticleNotFoundException;
import com.itsthatjun.ecommerce.exception.ArticleSlugAlreadyExistException;
import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
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
                .flatMap(this::buildAdminArticleInfo)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("No articles found.")));
    }

    @Override
    public Flux<AdminArticleInfo> listArticles(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return articleRepository.findAllByPublishStatusWithPagination(PublishStatus.PUBLISHED.getValue(), offset, pageSize)
                .flatMap(this::buildAdminArticleInfo)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("No articles found.")));
    }

    @Override
    public Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent) {
        return articleRepository.findById(articleId)
                .flatMap(this::buildAdminArticleInfo)
                .delayElement(Duration.ofMillis(delay))
                .flatMap(articleInfo -> throwErrorIfBadLuck(articleInfo, faultPercent)) // simulate error injection
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with id: " + articleId))); // Custom exception handling
    }

    private Mono<AdminArticleInfo> buildAdminArticleInfo(Article article) {
        int articleId = article.getId();
        AdminArticleInfo articleInfo = new AdminArticleInfo();
        articleInfo.setArticle(article);
        Mono<List<ArticleImage>> imagesMono = imageRepository.findAllByArticleId(articleId).collectList();
        Mono<List<ArticleQa>> qaMono = qaRepository.findAllByArticleId(articleId).collectList();
        Mono<List<ArticleVideo>> videosMono = videoRepository.findAllByArticleId(articleId).collectList();

        return Mono.zip(imagesMono, qaMono, videosMono)
                .map(tuple -> {
                    articleInfo.setImages(tuple.getT1());
                    articleInfo.setQA(tuple.getT2());
                    articleInfo.setVideos(tuple.getT3());
                    return articleInfo;
                });
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

    @Override
    @Transactional
    public Mono<AdminArticleInfo> createArticle(AdminArticleInfo articleInfo, String operator) {
        String slug = StringUtil.slugify(articleInfo.getArticle().getTitle());

        return articleRepository.findAllBySlug(slug)
                .hasElements() // Check if slug already exists
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ArticleSlugAlreadyExistException("Article with slug already exists: " + slug));
                    }

                    Article articleEntity = articleInfo.getArticle();
                    articleEntity.setSlug(slug);

                    return articleRepository.saveArticle(articleEntity)
                            .flatMap(savedArticle -> {
                                int articleId = savedArticle.getId();

                                articleInfo.getArticle().setId(articleId);

                                articleInfo.getImages().forEach(image -> image.setArticleId(articleId));
                                articleInfo.getQA().forEach(qa -> qa.setArticleId(articleId));
                                articleInfo.getVideos().forEach(video -> video.setArticleId(articleId));

                                Mono<Void> saveImages = imageRepository.saveAll(articleInfo.getImages()).then();
                                Mono<Void> saveQa = qaRepository.saveAll(articleInfo.getQA()).then();
                                Mono<Void> saveVideos = videoRepository.saveAll(articleInfo.getVideos()).then();

                                ArticleInfo articleInfoDTO = dtoMapper.adminArticleInfoToArticleInfo(articleInfo);

                                return Mono.zip(saveImages, saveQa, saveVideos)
                                        .then(Mono.fromRunnable(() ->
                                                sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(CREATE, articleInfoDTO))
                                        ))
                                        .then(createChangeLog(articleId, UpdateActionType.CREATE, operator)) // Log the operation
                                        .thenReturn(articleInfo); // Return the created articleInfo
                            });
                })
                .doOnError(error -> LOG.error("Failed to create article, error: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<AdminArticleInfo> updateArticle(AdminArticleInfo articleInfo, String operator) {
        return articleRepository.findById(articleInfo.getArticle().getId())
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleInfo.getArticle().getId())))
                .flatMap(foundArticle -> {
                    // Map the input article DTO to the entity to be updated
                    Article updatedArticle = articleInfo.getArticle();
                    updatedArticle.setUpdatedAt(LocalDateTime.now());

                    // Save the updated article
                    return articleRepository.updateArticle(updatedArticle)
                            .flatMap(savedArticle -> {
                                int articleId = savedArticle.getId();

                                // Map images, QA, and videos to be saved
                                Mono<Void> handleImages = handleImages(articleInfo.getImages(), articleId);
                                Mono<Void> handleQA = handleQA(articleInfo.getQA(), articleId);
                                Mono<Void> handleVideos = handleVideos(articleInfo.getVideos(), articleId);

                                // Prepare ArticleInfo DTO for update event
                                ArticleInfo articleInfoDTO = dtoMapper.adminArticleInfoToArticleInfo(articleInfo);

                                // Combine all the operations and proceed with logging and sending the update event
                                return Mono.zip(handleImages, handleQA, handleVideos)
                                        .then(createChangeLog(articleId, UpdateActionType.UPDATE, operator)) // Log the update operation
                                        .then(Mono.fromRunnable(() ->
                                                sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(UPDATE, articleInfoDTO))
                                        ))
                                        .thenReturn(articleInfo); // Return the updated articleDTO
                            });
                })
                .doOnError(error -> LOG.error("Failed to update article with ID: {}, error: {}", articleInfo.getArticle().getId(), error.getMessage()))
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
    public Mono<Void> updateStatus(Article updateArticle, String operator) {
        int articleId = updateArticle.getId();
        return articleRepository.findById(articleId)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleId)))
                .flatMap(article -> {
                    if (updateArticle.getPublishStatus().equals(PublishStatus.DELETED) ||
                            updateArticle.getLifecycleStatus().equals(LifeCycleStatus.SOFT_DELETE)) {
                        return Mono.error(new InvalidArticleStateException(
                                "Cannot update article in DELETED or SOFT_DELETE state."));
                    }
                    return articleRepository.updateLifecycleAndPublishStatus(updateArticle.getId(),
                                    updateArticle.getLifecycleStatus().getValue(),
                                    updateArticle.getPublishStatus().getValue()) // Update both statuses
                            .flatMap(updatedArticle -> {
                                ArticleInfo articleInfoDTO = new ArticleInfo();
                                articleInfoDTO.setArticle(dtoMapper.articleToArticleDTO(updatedArticle));
                                return sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(UPDATE, articleInfoDTO));
                            })
                            .then(createChangeLog(articleId, UpdateActionType.UPDATE, operator));
                })
                .doOnSuccess(unused -> LOG.info("Successfully updated article with ID: {}, lifecycle status: {}, publish status: {}, by operator: {}",
                        articleId, updateArticle.getLifecycleStatus(), updateArticle.getPublishStatus(), operator))
                .doOnError(error -> LOG.error("Failed to update article with ID: {}, attempted lifecycle status: {}, publish status: {}, by operator: {}, error: {}",
                        articleId, updateArticle.getLifecycleStatus(), updateArticle.getPublishStatus(), operator, error.getMessage()));
    }

    @Override
    public Mono<Void> restoreArticle(int articleId, String operator) {
        return articleRepository.findById(articleId)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleId)))
                .flatMap(article -> {
                    if (!article.getLifecycleStatus().equals(LifeCycleStatus.SOFT_DELETE)) {
                        return Mono.error(new InvalidArticleStateException("Article is not in a soft-deleted state."));
                    }
                    return articleRepository.updateLifecycleAndPublishStatus(articleId,   // un-delete article but keep it paused
                                    LifeCycleStatus.NORMAL.getValue(),
                                    PublishStatus.PAUSED.getValue())
                            .flatMap(updateArticle -> {
                                ArticleInfo articleInfoDTO = new ArticleInfo();
                                articleInfoDTO.setArticle(dtoMapper.articleToArticleDTO(updateArticle));
                                return sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(UPDATE, articleInfoDTO));
                            })
                            .then(createChangeLog(articleId, UpdateActionType.UPDATE, operator));
                })
                .doOnSuccess(unused -> LOG.debug("Restored article with ID: {}, new status: {}, by operator: {}", articleId, LifeCycleStatus.NORMAL, operator))
                .doOnError(error -> LOG.error("Restore failed for article ID: {}, operator: {}, error: {}", articleId, operator, error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deleteArticle(int articleId, String operator) {
        return articleRepository.findById(articleId)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleId)))
                .flatMap(article -> {
                    if (article.getLifecycleStatus().equals(LifeCycleStatus.SOFT_DELETE)) {
                        return Mono.error(new ArticleException("Article is already soft-deleted."));
                    }
                    return articleRepository.updateLifecycleAndPublishStatus(articleId,   // Mark as soft delete
                                    LifeCycleStatus.SOFT_DELETE.getValue(),
                                    PublishStatus.DELETED.getValue())
                            .flatMap(updateArticle -> {
                                ArticleInfo articleInfoDTO = new ArticleInfo();
                                articleInfoDTO.setArticle(dtoMapper.articleToArticleDTO(updateArticle));
                                return sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(UPDATE, articleInfoDTO));
                            })
                            .then(createChangeLog(articleId, UpdateActionType.UPDATE, operator));
                })
                .doOnSuccess(unused -> LOG.debug("Soft deleted article with ID: {}, new status: {}, by operator: {}", articleId, LifeCycleStatus.SOFT_DELETE, operator))
                .doOnError(error -> LOG.error("Soft delete failed for article ID: {}, operator: {}, error: {}", articleId, operator, error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> permanentDeleteArticle(int articleId, String operator) {
        return articleRepository.findById(articleId)
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with ID: " + articleId)))
                .flatMap(article ->
                        articleRepository.deleteArticle(article)
                            .then(createChangeLog(articleId, UpdateActionType.DELETE, operator))
                            .then(Mono.defer(() -> {
                                ArticleInfo articleInfoDTO = new ArticleInfo();
                                articleInfoDTO.setArticle(dtoMapper.articleToArticleDTO(article));
                                return sendArticleUpdateMessage(BINDING_NAME, new ArticleUpdateEvent(DELETE, articleInfoDTO));
                            }))
                )
                .doOnSuccess(unused -> LOG.debug("Article deleted with ID: {}, by operator: {}", articleId, operator))
                .doOnError(error -> LOG.error("Failed to delete article with ID: {}, error: {}", articleId, error.getMessage()));
    }

    private Mono<Void> createChangeLog(int articleId, UpdateActionType updateAction, String operator) {
        ArticleChangeLog changeLog = new ArticleChangeLog();
        changeLog.setArticleId(articleId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setOperator(operator);
        return logRepository.saveLog(changeLog).then();
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