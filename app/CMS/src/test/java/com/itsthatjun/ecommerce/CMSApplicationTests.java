package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.config.MyBatisConfig;
import com.itsthatjun.ecommerce.dao.AdminArticleDao;
import com.itsthatjun.ecommerce.dao.ArticleDao;
import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.dto.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.mbg.mapper.ArticleMapper;
import com.itsthatjun.ecommerce.service.impl.AdminArticleServiceImpl;
import com.itsthatjun.ecommerce.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.itsthatjun.ecommerce.dto.event.CmsAdminArticleEvent.Type.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MyBatisConfig.class)
@ActiveProfiles("test")
class CMSApplicationTests extends TestContainerConfig {

    @Autowired
    @Qualifier("articleMessageProcessor")
    private Consumer<CmsAdminArticleEvent> articleMessageProcessor;

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private AdminArticleDao adminArticleDao;

    @Autowired
    private AdminArticleServiceImpl adminArticleService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private WebTestClient client;

    @Test
    void getAllArticles() {
        client.get()
                .uri("/article/all")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    @Test
    void getArticleById() {
        client.get()
                .uri("/article/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    @Test
    void contextLoads() {
        Flux<ArticleInfo> articleInfoFlux = articleService.getAllArticles();

        // Subscribe to the Flux and define how to handle emitted elements
        articleInfoFlux.subscribe(
                articleInfo -> {
                    // Handle each emitted ArticleInfo element
                    System.out.println("Received article: " + articleInfo);
                },
                throwable -> {
                    // Handle errors
                    System.err.println("Error occurred: " + throwable.getMessage());
                },
                () -> {
                    // Handle completion (no more elements will be emitted)
                    System.out.println("Flux completed");
                }
        );
    }

    @Test
    void createArticle() throws InterruptedException {
        sendCreateArticleEvent();
        sendCreateArticleEvent();
        sendCreateArticleEvent();
        TimeUnit.SECONDS.sleep(1);
        assertEquals(6, adminArticleDao.getAllArticles().size());
    }

    private void sendCreateArticleEvent() {
        AdminArticleInfo articleInfo = new AdminArticleInfo();
        articleInfo.setTitle("title");
        articleInfo.setBody("body");
        articleInfo.setPublishStatus(1);
        String operator = "admin";

        CmsAdminArticleEvent event = new CmsAdminArticleEvent(CREATE, articleInfo, operator);
        articleMessageProcessor.accept(event);
    }

    private void sendUpdateArticleEvent() {
        AdminArticleInfo articleInfo = new AdminArticleInfo();
        articleInfo.setTitle("title");
        articleInfo.setBody("body");
        articleInfo.setPublishStatus(1);
        String operator = "admin";

        CmsAdminArticleEvent event = new CmsAdminArticleEvent(UPDATE, articleInfo, operator);
        articleMessageProcessor.accept(event);
    }

    private void sendDeleteArticleEvent() {
        AdminArticleInfo articleInfo = new AdminArticleInfo();
        articleInfo.setTitle("title");
        articleInfo.setBody("body");
        articleInfo.setPublishStatus(1);
        String operator = "admin";

        CmsAdminArticleEvent event = new CmsAdminArticleEvent(DELETE, articleInfo, operator);
        articleMessageProcessor.accept(event);
    }
}
