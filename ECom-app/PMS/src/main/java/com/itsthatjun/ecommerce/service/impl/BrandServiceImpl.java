package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.exceptions.BrandException;
import com.itsthatjun.ecommerce.mbg.mapper.BrandMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final ProductMapper productMapper;

    private final BrandMapper brandMapper;

    // TODO: add update log
    private final BrandUpdateLog updateLog;

    private final Scheduler jdbcScheduler;

    @Autowired
    public BrandServiceImpl(ProductMapper productMapper, BrandMapper brandMapper, BrandUpdateLog updateLog,
                            @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
        this.updateLog = updateLog;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<Brand> listAllBrand() {
        return Mono.fromCallable(() -> {
                    List<Brand> brandList = brandMapper.selectByExample(new BrandExample());
                    return brandList;
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Brand> listBrand(int pageNum, int pageSize) {
        return Mono.fromCallable(() -> {
                    PageHelper.startPage(pageNum, pageSize);
                    List<Brand> brandList = brandMapper.selectByExample(new BrandExample());
                    return brandList;
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listAllBrandProduct(int brandId) {
        return Mono.fromCallable(() -> {
                    ProductExample example = new ProductExample();
                    example.createCriteria().andBrandIdEqualTo(brandId);
                    List<Product> productList = productMapper.selectByExample(example);
                    return productList;
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> getBrand(int id) {
        return Mono.fromCallable(() -> {
            Brand brand = brandMapper.selectByPrimaryKey(id);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> adminCreateBrand(Brand brand) {
        return Mono.fromCallable(() -> {
            brandMapper.insert(brand);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> adminUpdateBrand(Brand brand) {
        return Mono.fromCallable(() -> {
            brandMapper.updateByPrimaryKeySelective(brand);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> adminDeleteBrand(int id) {
        return Mono.fromRunnable(() ->
                brandMapper.deleteByPrimaryKey(id)
        ).subscribeOn(jdbcScheduler).then();
    }
}
