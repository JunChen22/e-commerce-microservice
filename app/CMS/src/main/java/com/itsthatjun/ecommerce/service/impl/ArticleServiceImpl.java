package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.dto.model.VideoDTO;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.exceptions.ArticleNotFoundException;
import com.itsthatjun.ecommerce.repository.ArticleImageRepository;
import com.itsthatjun.ecommerce.repository.ArticleQARepository;
import com.itsthatjun.ecommerce.repository.ArticleRepository;
import com.itsthatjun.ecommerce.repository.ArticleVideoRepository;
import com.itsthatjun.ecommerce.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final ArticleQARepository qaRepository;

    private final ArticleVideoRepository videoRepository;

    private final ArticleImageRepository imageRepository;

    private final DTOMapper dtoMapper;

    private final Random randomNumberGenerator = new Random();

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleQARepository qaRepository, ArticleVideoRepository videoRepository,
                              ArticleImageRepository imageRepository, DTOMapper dtoMapper) {
        this.articleRepository = articleRepository;
        this.qaRepository = qaRepository;
        this.videoRepository = videoRepository;
        this.imageRepository = imageRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<ArticleInfo> listAllArticles() {
        return articleRepository.findAllByPublishStatus(PublishStatus.PUBLISHED.getValue())
                .flatMap(article -> {
                    int articleId = article.getId();
                    ArticleInfo articleInfo = dtoMapper.articleToArticleDTO(article);
                    Mono<List<ImageDTO>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<QaDTO>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<VideoDTO>> videosMono = fetchVideosForArticle(articleId);

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
    public Flux<ArticleInfo> listArticles(int page, int size) {
        int offset = page * size;
        return articleRepository.findAllByPublishStatusWithPagination(PublishStatus.PUBLISHED.getValue(), offset, size)
                .flatMap(article -> {
                    int articleId = article.getId();
                    ArticleInfo articleInfo = dtoMapper.articleToArticleDTO(article);
                    Mono<List<ImageDTO>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<QaDTO>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<VideoDTO>> videosMono = fetchVideosForArticle(articleId);

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
    public Mono<ArticleInfo> getArticleBySlug(String slug, int delay, int faultPercent) {
        return articleRepository.findBySlugAndPublishStatus(slug, PublishStatus.PUBLISHED.getValue()) // find
                .flatMap(article -> {
                    int articleId = article.getId();
                    System.out.println("Article ID: " + article.getBody());
                    ArticleInfo articleInfo = dtoMapper.articleToArticleDTO(article);
                    Mono<List<ImageDTO>> imagesMono = fetchImagesForArticle(articleId);
                    Mono<List<QaDTO>> qaMono = fetchQaForArticle(articleId);
                    Mono<List<VideoDTO>> videosMono = fetchVideosForArticle(articleId);

                    return Mono.zip(imagesMono, qaMono, videosMono)
                            .flatMap(tuple -> {
                                articleInfo.setImages(tuple.getT1());
                                articleInfo.setQA(tuple.getT2());
                                articleInfo.setVideos(tuple.getT3());
                                return throwErrorIfBadLuck(articleInfo, faultPercent); // This now returns Mono<ArticleInfo>
                            });
                })
                .delayElement(Duration.ofMillis(delay))
                .switchIfEmpty(Mono.error(new ArticleNotFoundException("Article not found with slug: " + slug))); // Custom exception handling
    }

    /*
        use to test/simulate circuit breaker. fault percent.
    */
    private Mono<ArticleInfo> throwErrorIfBadLuck(ArticleInfo articleInfo, int faultPercent) {
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
    private Mono<List<QaDTO>> fetchQaForArticle(int articleId) {
        return qaRepository.findAllByArticleId(articleId)
                .collectList()
                .map(dtoMapper::qasToQaDTOs); // Map Qa entity to QaDTO
    }

    // Fetch images for the article
    private Mono<List<ImageDTO>> fetchImagesForArticle(int articleId) {
        return imageRepository.findAllByArticleId(articleId)
                .collectList()
                .map(dtoMapper::imagesToImageDTOs); // Map Image entity to ImageDTO
    }

    // Fetch videos for the article
    private Mono<List<VideoDTO>> fetchVideosForArticle(int articleId) {
        return videoRepository.findAllByArticleId(articleId)
                .collectList()
                .map(dtoMapper::videosToVideoDTOs); // Map Video entity to VideoDTO
    }
}
