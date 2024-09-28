package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.AdminProductDetail;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.ProductService;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final AdminServiceImpl adminService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms/product/admin";

    @Autowired
    public ProductServiceImpl(AdminServiceImpl adminService, WebClient.Builder webClient, StreamBridge streamBridge,
                              @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.adminService = adminService;
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<Product> listAllProduct() {
        String url = PMS_SERVICE_URL + "/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> listAllProduct(int pageNum, int pageSize) {
        String url = PMS_SERVICE_URL + "/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<AdminProductDetail> listProduct(int productId) {
        String url = PMS_SERVICE_URL + "/" + productId;

        return webClient.get().uri(url).retrieve().bodyToMono(AdminProductDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<AdminProductDetail> createProduct(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> addProductSku(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT_SKU, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> updateProductInfo(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_INFO, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> updateProductStatus(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_STATUS, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> updateProductSkuStatus(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_SKU_STATUS, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> updateProductStock(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_STOCK, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> updateProductPrice(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_PRICE, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<AdminProductDetail> removeProductSku(AdminProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("product-out-0", new PmsAdminProductEvent(REMOVE_PRODUCT_SKU, productDetail, operator));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        return Mono.fromRunnable(() -> {
            String operator = adminService.getAdminName();
            AdminProductDetail productDetail = new AdminProductDetail();
            Product product = new Product();
            product.setId(productId);
            productDetail.setProduct(product);
            sendMessage("product-out-0", new PmsAdminProductEvent(DELETE_PRODUCT, productDetail, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, PmsAdminProductEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
