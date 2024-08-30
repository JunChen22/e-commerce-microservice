package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final WebClient webClient;

    private final String PMS_SERVICE_URL = "http://pms/brand";

    @Autowired
    public BrandServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Brand> getAllBrand() {
        String url = PMS_SERVICE_URL + "/listAll/";
        LOG.debug("Will call the getAllBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Brand> getAllBrand(int pageNum, int pageSize) {
        String url = PMS_SERVICE_URL + "/list/" + "?pageNum=" + pageNum + "&pageSize=" + pageSize;
        LOG.debug("Will call the getAllBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> getBrandProduct(int brandId) {
        String url = PMS_SERVICE_URL + "/product/" + brandId;
        LOG.debug("Will call the getBrandProduct API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Brand> getBrand(int brandId) {
        String url = PMS_SERVICE_URL + "/" + brandId;
        LOG.debug("Will call the getBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
