package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.dto.event.incoming.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.service.impl.AdminArticleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class CMSApplicationTests extends TestContainerConfig {

    @Autowired
    @Qualifier("articleMessageProcessor")
    private Consumer<CmsAdminArticleEvent> articleMessageProcessor;

    @Autowired
    private AdminArticleServiceImpl adminArticleService;

    @Autowired
    private WebTestClient client;

//    @Test
//    void listAllArticles() {
//        client.get()
//                .uri("/article/all")
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(HttpStatus.OK)
//                .expectHeader().contentType(APPLICATION_JSON)
//                .expectBody();
//    }
//
//    @Test
//    void getArticleById() {
//        client.get()
//                .uri("/article/1")
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(HttpStatus.OK)
//                .expectHeader().contentType(APPLICATION_JSON)
//                .expectBody();
//    }

    @Test
    void contextLoads() {
//        Flux<ArticleInfo> articleInfoFlux = articleService.listAllArticles();
//
//        // Subscribe to the Flux and define how to handle emitted elements
//        articleInfoFlux.subscribe(
//                articleInfo -> {
//                    // Handle each emitted ArticleInfo element
//                    System.out.println("Received article: " + articleInfo);
//                },
//                throwable -> {
//                    // Handle errors
//                    System.err.println("Error occurred: " + throwable.getMessage());
//                },
//                () -> {
//                    // Handle completion (no more elements will be emitted)
//                    System.out.println("Flux completed");
//                }
//        );
    }
//
//    @Test
//    void createArticle() throws InterruptedException {
//        sendCreateArticleEvent();
//        sendCreateArticleEvent();
//        sendCreateArticleEvent();
//        TimeUnit.SECONDS.sleep(1);
//        assertEquals(6, adminArticleDao().size());
//    }
//
//    private void sendCreateArticleEvent() {
//        AdminArticleInfo articleInfo = new AdminArticleInfo();
//        articleInfo.setTitle("title");
//        articleInfo.setBody("body");
//        articleInfo.setPublishStatus(1);
//        String operator = "admin";
//
//        CmsAdminArticleEvent event = new CmsAdminArticleEvent(CREATE, articleInfo, operator);
//        articleMessageProcessor.accept(event);
//    }
//
//    private void sendUpdateArticleEvent() {
//        AdminArticleInfo articleInfo = new AdminArticleInfo();
//        articleInfo.setTitle("title");
//        articleInfo.setBody("body");
//        articleInfo.setPublishStatus(1);
//        String operator = "admin";
//
//        CmsAdminArticleEvent event = new CmsAdminArticleEvent(UPDATE, articleInfo, operator);
//        articleMessageProcessor.accept(event);
//    }
//
//    private void sendDeleteArticleEvent() {
//        AdminArticleInfo articleInfo = new AdminArticleInfo();
//        articleInfo.setTitle("title");
//        articleInfo.setBody("body");
//        articleInfo.setPublishStatus(1);
//        String operator = "admin";
//
//        CmsAdminArticleEvent event = new CmsAdminArticleEvent(DELETE, articleInfo, operator);
//        articleMessageProcessor.accept(event);
//    }
}
