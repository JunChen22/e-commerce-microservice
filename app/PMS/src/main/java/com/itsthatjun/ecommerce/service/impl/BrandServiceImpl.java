package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.exceptions.BrandException;
import com.itsthatjun.ecommerce.mbg.mapper.BrandMapper;
import com.itsthatjun.ecommerce.mbg.mapper.BrandUpdateLogMapper;
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

    private final BrandUpdateLogMapper brandUpdateLogMapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public BrandServiceImpl(ProductMapper productMapper, BrandMapper brandMapper, BrandUpdateLogMapper brandUpdateLogMapper,
                            @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
        this.brandUpdateLogMapper = brandUpdateLogMapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<Brand> listAllBrand(int pageNum, int pageSize) {
        return Mono.fromCallable(() -> {
            PageHelper.startPage(pageNum, pageSize);
            List<Brand> brandList = brandMapper.selectByExample(new BrandExample());
            return brandList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listAllBrandProduct(int brandId) {
        return Mono.fromCallable(() -> {
            ProductExample example = new ProductExample();
            example.createCriteria().andBrandIdEqualTo(brandId).andPublishStatusEqualTo(1);
            List<Product> productList = productMapper.selectByExample(example);
            return productList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> getBrand(int id) {
        return Mono.fromCallable(() -> {
            Brand brand = brandMapper.selectByPrimaryKey(id);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> adminCreateBrand(Brand brand, String operator) {
        return Mono.fromCallable(() -> {
            brandMapper.insert(brand);
            createUpdateLog(brand.getId(), "update brand", operator);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Brand> adminUpdateBrand(Brand brand, String operator) {
        return Mono.fromCallable(() -> {
            brandMapper.updateByPrimaryKeySelective(brand);
            createUpdateLog(brand.getId(), "update brand", operator);
            return brand; // Use Mono.justOrEmpty to handle potential null values
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> adminDeleteBrand(int brandId, String operator) {
        return Mono.fromRunnable(() -> {
            Brand brand = brandMapper.selectByPrimaryKey(brandId);
            if (brand == null) throw new BrandException("brand does not exist: " + brandId);

            createUpdateLog(brandId, "delete brand: " +  brand.getName(), operator);
            brandMapper.deleteByPrimaryKey(brandId);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void createUpdateLog(int brandId, String updateAction, String operator) {
        BrandUpdateLog updateLog = new BrandUpdateLog();
        updateLog.setBrandId(brandId);
        updateLog.setUpdateAction(updateAction);
        updateLog.setOperator(operator);
        brandUpdateLogMapper.insert(updateLog);
    }
}
