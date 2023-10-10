package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent.Type.*;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@RequestMapping("/product")
@Api(tags = "Product related", description = "product related")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms:8080/product";

    @Autowired
    public ProductController(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct(){
        String url = PMS_SERVICE_URL + "/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        String url = PMS_SERVICE_URL + "/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<ProductDetail> listProduct(@PathVariable int id){
        String url = PMS_SERVICE_URL + "/" + id;

        return webClient.get().uri(url).retrieve().bodyToMono(ProductDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "create a product with at least one sku variant")
    public Mono<Void> createProduct(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/addProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Add a sku to existing product.")
    public Mono<Void> addProductSku(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT_SKU, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateProductInfo")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public Mono<Void> updateProductInfo(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_INFO, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateProductStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<Void> updateProductStatus(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_STATUS, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateProductSkuStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<Void> updateProductSkuStatus(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_SKU_STATUS, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateProductStock")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public Mono<Void> updateProductStock(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_STOCK, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateProductPrice")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public Mono<Void> updateProductPrice(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent( UPDATE_PRODUCT_PRICE, productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/removeProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public Mono<Void> removeProductSku(@RequestBody ProductDetail productDetail) {
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(REMOVE_PRODUCT_SKU ,productDetail)))
                .subscribeOn(publishEventScheduler).then();
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public Mono<Void> deleteProduct(@PathVariable int productId) {
        ProductDetail productDetail = new ProductDetail();
        Product product = new Product();
        product.setId(productId);
        productDetail.setProduct(product);
        return Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(DELETE_PRODUCT ,productDetail)))
                .subscribeOn(publishEventScheduler).then();
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
