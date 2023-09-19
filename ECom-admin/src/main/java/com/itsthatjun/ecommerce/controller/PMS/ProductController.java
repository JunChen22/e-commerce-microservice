package com.itsthatjun.ecommerce.controller.PMS;

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
    @ApiOperation(value = "Create a product")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    public void createProduct(@RequestBody Product product){
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(NEW_PRODUCT, product, null)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation(value = "Update a product")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    public void updateProduct(@RequestBody Product product){
        int productId = product.getId();
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(UPDATE_PRODUCT, product, productId)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{productId}")
    @ApiOperation(value = "Delete a product")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    public void deleteProduct(@PathVariable int productId){
        Mono.fromRunnable(() -> sendMessage("product-out-0", new PmsAdminProductEvent(REMOVE_PRODUCT, null, productId)))
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
