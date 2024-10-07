package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static java.util.logging.Level.FINE;

@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms/sale";

    @Autowired
    public PromotionServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<PromotionSale> listAllPromotionSale() {
        String url = SMS_SERVICE_URL + "/AllPromotionSale";
        LOG.debug("Will call the getAllPromotionSale API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(PromotionSale.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> listAllPromotionSaleItem() {
        String url = SMS_SERVICE_URL + "/AllPromotionSaleItem";
        LOG.debug("Will call the getAllPromotionSaleItem API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Product> listAllFlashSaleItem() {
        String url = SMS_SERVICE_URL + "/AllFlashSaleItem";
        LOG.debug("Will call the getAllFlashSaleItem API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }
}
