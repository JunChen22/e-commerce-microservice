package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent;
import com.itsthatjun.ecommerce.mbg.model.Brand;
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

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent.Type.*;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@RequestMapping("/brand")
@Api(tags = "brand related", description = "brand management")
public class BrandController {

    private static final Logger LOG = LoggerFactory.getLogger(BrandController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms:8080/brand";

    @Autowired
    public BrandController(WebClient.Builder webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public Flux<Brand> getAllBrand(){
        String url = PMS_SERVICE_URL + "/brand/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public Flux<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize){

        String url = PMS_SERVICE_URL + "/brand/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<Product> getBrandProduct(@PathVariable int brandId){
        String url = PMS_SERVICE_URL + "/brand/product/" + brandId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<Brand> getBrand(@PathVariable int brandId){
        String url = PMS_SERVICE_URL + "/brand/" + brandId;

        return webClient.get().uri(url).retrieve().bodyToMono(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Create a brand")
    public void createBrand(@RequestBody Brand brand){
        Mono.fromRunnable(() -> sendMessage("brand-out-0", new PmsAdminBrandEvent(CREATE, brand, null)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update a brand")
    public void updateBrand(@RequestBody Brand brand){
        int brandId = brand.getId();
        Mono.fromRunnable(() -> sendMessage("brand-out-0", new PmsAdminBrandEvent(UPDATE, brand, brandId)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete a brand")
    public void deleteBrand(@PathVariable int id){
        Mono.fromRunnable(() -> sendMessage("brand-out-0", new PmsAdminBrandEvent(DELETE, null, id)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    private void sendMessage(String bindingName, PmsAdminBrandEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
