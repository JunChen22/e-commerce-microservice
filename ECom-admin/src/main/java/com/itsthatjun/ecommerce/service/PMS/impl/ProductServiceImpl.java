package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.ProductService;
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

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms:8080/product";

    @Autowired
    public ProductServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                              @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
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
    public Mono<ProductDetail> listProduct(int productId) {
        String url = PMS_SERVICE_URL + "/" + productId;

        return webClient.get().uri(url).retrieve().bodyToMono(ProductDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<ProductDetail> createProduct(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> addProductSku(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT_SKU, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> updateProductInfo(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_INFO, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> updateProductStatus(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_STATUS, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> updateProductSkuStatus(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_SKU_STATUS, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> updateProductStock(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_STOCK, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> updateProductPrice(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent( UPDATE_PRODUCT_PRICE, productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ProductDetail> removeProductSku(ProductDetail productDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("product-out-0", new PmsAdminProductEvent(REMOVE_PRODUCT_SKU ,productDetail));
            return productDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        return Mono.fromRunnable(() -> {
            ProductDetail productDetail = new ProductDetail();
            Product product = new Product();
            product.setId(productId);
            productDetail.setProduct(product);
            sendMessage("product-out-0", new PmsAdminProductEvent(DELETE_PRODUCT, productDetail));
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
