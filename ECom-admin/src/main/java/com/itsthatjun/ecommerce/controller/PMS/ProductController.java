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

    @Value("${app.PMS-service.host}")
    String pmsServiceURL;
    @Value("${app.PMS-service.port}")
    int port;

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
        String url = "http://" + pmsServiceURL + ":" + port + "/product/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        String url = "http://" + pmsServiceURL + ":" + port + "/product/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<Product> listProduct(@PathVariable int id){
        String url = "http://" + pmsServiceURL + ":" + port + "/product/" + id;

        return webClient.get().uri(url).retrieve().bodyToMono(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "create a product with at least one sku variant")
    public void createProduct(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/addProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Add a sku to existing product.")
    public void addProductSku(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT_SKU, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/updateProductInfo")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public void updateProductInfo(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_INFO, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/updateProductStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public void updateProductStatus(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_STATUS, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/updateProductSkuStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public void updateProductSkuStatus(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT_SKU_STATUS, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/updateProductStock")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public void updateProductStock(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_STOCK, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/updateProductPrice")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public void updateProductPrice(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent( UPDATE_PRODUCT_PRICE, productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/removeProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public void removeProductSku(@RequestBody ProductDetail productDetail) {
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(REMOVE_PRODUCT_SKU ,productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public void deleteProduct(@PathVariable int productId) {
        ProductDetail productDetail = new ProductDetail();
        Product product = new Product();
        product.setId(productId);
        productDetail.setProduct(product);
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(DELETE_PRODUCT ,productDetail)))
                .subscribeOn(publishEventScheduler).subscribe();
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
