package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.exceptions.ArticleNotFoundException;
import com.itsthatjun.ecommerce.model.AdminArticleInfo;
import com.itsthatjun.ecommerce.dto.event.outgoing.ArticleUpdateEvent;
import com.itsthatjun.ecommerce.model.entity.*;
import com.itsthatjun.ecommerce.repository.*;
import com.itsthatjun.ecommerce.service.AdminArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final ArticleQARepository qaRepository;

    private final ArticleVideoRepository videoRepository;

    private final ArticleImageRepository imageRepository;

    private final ArticleLogRepository logRepository;

    private final StreamBridge streamBridge;

    private final String BINDING_NAME = "app-article-out-0";

    private final Random randomNumberGenerator = new Random();

    @Autowired
    public AdminArticleServiceImpl(ArticleRepository articleRepository, ArticleQARepository qaRepository, ArticleVideoRepository videoRepository,
                                   ArticleImageRepository imageRepository, ArticleLogRepository logRepository, StreamBridge streamBridge) {
        this.articleRepository = articleRepository;
        this.qaRepository = qaRepository;
        this.videoRepository = videoRepository;
        this.imageRepository = imageRepository;
        this.logRepository = logRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Flux<AdminArticleInfo> listAllArticles() {
        // optional:
        // Pageable pageable = PageRequest.of(0, 10); // Pageable with 10 items per page
        return articleRepository.findAllByPublishStatus(1)
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
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("No articles found."))); // Custom exception handling for empty result
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

//    @Override
//    public Mono<AdminArticleInfo> createArticle(AdminArticleInfo articleInfo, String operator) {
//        return Mono.fromCallable(() ->
//                // Offload the blocking operation to the specified scheduler
//                internalCreateArticle(articleInfo, operator)
//        ).subscribeOn(jdbcScheduler);
//    }
//
//    private AdminArticleInfo internalCreateArticle(AdminArticleInfo articleInfo, String operator) {
//        Article article = dtoMapper.adminArticleInfoToArticle(articleInfo);
//        Date currentDate = new Date();
//        article.setCreatedAt(currentDate);
//        article.setUpdatedAt(currentDate);
//
//        articleMapper.insert(article);
//
//        int articleId = article.getId();
//
//        for (ArticleQa qa : articleInfo.getQA()) {
//            qa.setArticleId(articleId);
//            qa.setCreatedAt(currentDate);
//            qa.setUpdatedAt(currentDate);
//            qaMapper.insert(qa);
//        }
//
//        for (ArticleImage image : articleInfo.getImages()) {
//            image.setArticleId(articleId);
//            image.setCreatedAt(currentDate);
//            image.setUpdatedAt(currentDate);
//            imageMapper.insert(image);
//        }
//
//        for (ArticleVideo video : articleInfo.getVideos()) {
//            video.setArticleId(articleId);
//            video.setCreatedAt(currentDate);
//            video.setUpdatedAt(currentDate);
//            videoMapper.insert(video);
//        }
//
//        articleLog(articleId, "new article", operator);
//        return articleInfo;
//    }
//
//    @Override    // TODO: update existing one, even with deletion
//    public Mono<AdminArticleInfo> updateArticle(AdminArticleInfo articleInfo, String operator) {
//        return Mono.fromCallable(() -> {
//            // Offload the blocking operation to the specified scheduler
//            int articleId = articleInfo.getId();
//
//            Article article = dtoMapper.adminArticleInfoToArticle(articleInfo);
//            article.setUpdatedAt(new Date());
//            articleMapper.updateByPrimaryKeySelective(article);
//
//            updateQA(articleId, articleInfo.getQA());
//            updateImage(articleId, articleInfo.getImages());
//            updateVideo(articleId, articleInfo.getVideos());
//
//            articleLog(articleId, "update article", operator);
//            return articleInfo;
//        }).subscribeOn(jdbcScheduler);
//    }
//
//    private int updateQA(int articleId, List<ArticleQa> qaList) {
//        for (ArticleQa qa : qaList) {
//            qa.setUpdatedAt(new Date());
//            qa.setArticleId(articleId);
//            qaMapper.updateByPrimaryKey(qa);
//        }
//        return qaList.size();
//    }
//
//    private int updateImage(int articleId, List<ArticleImage> imageList) {
//        for (ArticleImage image : imageList) {
//            image.setUpdatedAt(new Date());
//            image.setArticleId(articleId);
//            imageMapper.updateByPrimaryKey(image);
//        }
//        return imageList.size();
//    }
//
//    private int updateVideo(int articleId, List<ArticleVideo> videoList) {
//        for (ArticleVideo video : videoList) {
//            video.setUpdatedAt(new Date());
//            video.setArticleId(articleId);
//            videoMapper.updateByPrimaryKey(video);
//        }
//        return videoList.size();
//    }
//
//    @Override
//    public Mono<Void> deleteArticle(int articleId, String operator) {
//        return Mono.fromRunnable(() -> {
//            if (articleMapper.selectByPrimaryKey(articleId) == null) {
//                throw new ArticleException("Article not found with ID: " + articleId);
//            }
//
//            articleMapper.deleteByPrimaryKey(articleId);
//
//            ArticleQaExample articleQaExample= new ArticleQaExample();
//            articleQaExample.createCriteria().andArticleIdEqualTo(articleId);
//            List<ArticleQa> qaList = qaMapper.selectByExample(articleQaExample);
//            for (ArticleQa qa : qaList) {
//                qaMapper.deleteByPrimaryKey(qa.getId());
//            }
//
//            ArticleVideoExample articleVideoExample = new ArticleVideoExample();
//            articleVideoExample.createCriteria().andArticleIdEqualTo(articleId);
//            List<ArticleVideo> videoList = videoMapper.selectByExample(articleVideoExample);
//            for (ArticleVideo video : videoList) {
//                videoMapper.deleteByPrimaryKey(video.getId());
//            }
//
//            ArticleImageExample articleImageExample = new ArticleImageExample();
//            articleImageExample.createCriteria().andArticleIdEqualTo(articleId);
//            List<ArticleImage> imageList = imageMapper.selectByExample(articleImageExample);
//            for (ArticleImage image : imageList) {
//                imageMapper.deleteByPrimaryKey(image.getId());
//            }
//            articleLog(articleId, "delete article", operator);
//        }).subscribeOn(jdbcScheduler).then();
//    }

    private void articleLog(int articleId, String updateAction, String operator) {
        ArticleChangeLog changeLog = new ArticleChangeLog();
        changeLog.setArticleId(articleId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setOperator(operator);
        logRepository.save(changeLog);
    }

    private void sendOrderMessage(String bindingName, ArticleUpdateEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
