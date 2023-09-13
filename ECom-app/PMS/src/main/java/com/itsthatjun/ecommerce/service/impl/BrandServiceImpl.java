package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.exceptions.BrandException;
import com.itsthatjun.ecommerce.mbg.mapper.BrandMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.BrandExample;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductExample;
import com.itsthatjun.ecommerce.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final ProductMapper productMapper;

    private final BrandMapper brandMapper;

    @Autowired
    public BrandServiceImpl(ProductMapper productMapper, BrandMapper brandMapper) {
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
    }

    @Override
    public Flux<Brand> listAllBrand() {
        List<Brand> brandList = brandMapper.selectByExample(new BrandExample());
        return Flux.fromIterable(brandList);
    }

    @Override
    public Flux<Brand> listBrand(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Brand> brandList = brandMapper.selectByExample(new BrandExample());
        return Flux.fromIterable(brandList);
    }

    @Override
    public Flux<Product> listAllBrandProduct(int brandId) {
        ProductExample example = new ProductExample();
        example.createCriteria().andBrandIdEqualTo(brandId);
        List<Product> productList = productMapper.selectByExample(example);
        return Flux.fromIterable(productList);
    }

    @Override
    public Mono<Brand> getBrand(int id) {
        Brand brand =  brandMapper.selectByPrimaryKey(id);
        if (brand != null) {
            return Mono.just(brand);
        } else {
            return Mono.error(new BrandException("Brand not found"));
        }
    }

    @Override
    public boolean createBrand(Brand brand) {
        brandMapper.insert(brand);
        return true;
    }

    @Override
    public boolean updateBrand(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
        return true;
    }

    @Override
    public boolean deleteBrand(int id) {
        brandMapper.deleteByPrimaryKey(id);
        return true;
    }
}
