package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent;
import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.BrandService;
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

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final AdminServiceImpl adminService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms/brand";

    @Autowired
    public BrandServiceImpl(AdminServiceImpl adminService, WebClient.Builder webClient, StreamBridge streamBridge,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.adminService = adminService;
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<Brand> getAllBrand() {
        String url = PMS_SERVICE_URL + "/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Brand> getAllBrand(int pageNum, int pageSize) {
        String url = PMS_SERVICE_URL + "/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> getBrandProduct(int brandId) {
        String url = PMS_SERVICE_URL + "/product/" + brandId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Brand> getBrand(int brandId) {
        String url = PMS_SERVICE_URL + "/" + brandId;

        return webClient.get().uri(url).retrieve().bodyToMono(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<Brand> createBrand(Brand brand) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("brand-out-0", new PmsAdminBrandEvent(CREATE, brand, operator));
            return brand;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Brand> updateBrand(Brand brand) {
        return Mono.fromCallable(() -> {
            String operator = adminService.getAdminName();
            sendMessage("brand-out-0", new PmsAdminBrandEvent(UPDATE, brand, operator));
            return brand;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteBrand(int brandId) {
        return Mono.fromRunnable(() -> {
            String operator = adminService.getAdminName();
            Brand brand = new Brand();
            brand.setId(brandId);
            sendMessage("brand-out-0", new PmsAdminBrandEvent(DELETE, brand, operator));
        }).subscribeOn(publishEventScheduler).then();
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
