package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.dao.ProductDao;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ProductPictureDTO;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductExample;
import com.itsthatjun.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;

    private final ProductDao dao;

    private final Scheduler jdbcScheduler;

    private final DTOMapper dtoMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductDao dao,  @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
                              DTOMapper dtoMapper) {
        this.productMapper = productMapper;
        this.dao = dao;
        this.jdbcScheduler = jdbcScheduler;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Mono<ProductDetail> getProductDetail(int id) {
        return Mono.fromCallable(() -> {
            ProductDetail productDetail = dao.getProductDetail(id);

            if (productDetail == null) throw new RuntimeException("product does not exist.");

            List<Map<String, String>> attributeList = dao.getProductAttributes(id);
            Map<String, String> attribute = new HashMap<>();
            for (Map<String, String> entry : attributeList) {
                String skuCode = entry.get("sku_codes");
                String attributes = entry.get("attributes");
                attribute.put(skuCode, attributes);
            }
            productDetail.setAttributes(attribute);

            List<ProductPictureDTO> pictureList = dao.getProductPictures(id);
            productDetail.setPicturesList(pictureList);

            return productDetail;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<ProductDTO> listAllProduct() {
        return Mono.fromCallable(() -> {
            List<ProductDTO> productList = dao.getAllProduct();
            return productList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<ProductDTO> listProduct(int pageNum, int pageSize) {
        return Mono.fromCallable(() -> {
            PageHelper.startPage(pageNum, pageSize);
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andPublishStatusEqualTo(1);
            List<Product> productList = productMapper.selectByExample(productExample);
            List<ProductDTO> dtoList = dtoMapper.productToProductDTO(productList);

            return dtoList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }
}
