package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.exceptions.BrandException;
import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductExample;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuExample;
import com.itsthatjun.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuStockMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductSkuMapper skuStockMapper) {
        this.productMapper = productMapper;
        this.skuStockMapper = skuStockMapper;
    }

    @Override
    public Mono<Product> getProduct(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product != null) {
            return Mono.just(product);
        } else {
            return Mono.error(new ProductException("Product not found"));
        }
    }

    @Override
    public Flux<Product> listAllProduct() {
        List<Product> productList =  productMapper.selectByExample(new ProductExample());
        return Flux.fromIterable(productList);
    }

    @Override
    public Flux<Product> listProduct(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByExample(new ProductExample());
        return Flux.fromIterable(productList);
    }

    @Override
    public boolean createProduct(Product product) {
        productMapper.insert(product);
        return true;
    }

    @Override
    public boolean updateProduct(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
        return true;
    }

    @Override
    public boolean deleteProduct(int id) {
        productMapper.deleteByPrimaryKey(id);
        return true;
    }
}
