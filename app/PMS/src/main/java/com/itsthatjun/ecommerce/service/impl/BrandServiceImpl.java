package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.repository.BrandRepository;
import com.itsthatjun.ecommerce.repository.ProductRepository;
import com.itsthatjun.ecommerce.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final ProductRepository productRepository;

    private final BrandRepository brandRepository;

    private final DTOMapper dtoMapper;

    @Autowired
    public BrandServiceImpl(ProductRepository productRepository, BrandRepository brandRepository, DTOMapper dtoMapper) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<BrandDTO> listAllBrand() {
        return brandRepository.findAllByPublishStatus(PublishStatus.PUBLISHED.getValue())
                .map(dtoMapper::brandToBrandDTO);
    }

    @Override
    public Flux<BrandDTO> listBrand(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public Flux<ProductDTO> listAllBrandProduct(String slug) {
        return null;
    }

    @Override
    public Mono<BrandDTO> getBrand(String slug) {
        return brandRepository.findBySlugAndPublishStatus(slug, PublishStatus.PUBLISHED.getValue())
                .map(dtoMapper::brandToBrandDTO);
    }
}
