package com.itsthatjun.ecommerce.service.PMS.impl;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.PMS.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final WebClient webClient;

    private final String PMS_SERVICE_URL = "http://pms/product";

    @Autowired
    public ProductServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<ProductDTO> listAllProduct() {
        String url = PMS_SERVICE_URL + "/listAll";
        LOG.debug("Will call the listAllProduct API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductDTO.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<ProductDTO> listAllProduct(int pageNum, int pageSize) {
        String url = PMS_SERVICE_URL + "/list?page=" + pageNum + "&size=" + pageSize;
        LOG.debug("Will call the listAllProduct with pagination API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ProductDTO.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ProductDetail> listProduct(int id) {
        String url = PMS_SERVICE_URL + "/" + id;
        LOG.debug("Will call the listProduct API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ProductDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
