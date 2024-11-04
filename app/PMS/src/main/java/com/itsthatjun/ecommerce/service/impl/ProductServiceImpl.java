package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ProductPictureDTO;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.repository.ProductRepository;
import com.itsthatjun.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final DTOMapper dtoMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, DTOMapper dtoMapper) {
        this.productRepository = productRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<ProductDTO> listAllProduct() {
        return null;
    }

    @Override
    public Flux<ProductDTO> listProduct(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public Mono<ProductDetail> getProductDetail(int id) {
        return null;
    }

    //
//    @Override
//    public Mono<ProductDetail> getProductDetail(int id) {
//        return Mono.fromCallable(() -> {
//            ProductDetail productDetail = dao.getProductDetail(id);
//
//            if (productDetail == null) throw new RuntimeException("product does not exist.");
//
//            List<Map<String, String>> attributeList = dao.getProductAttributes(id);
//            Map<String, String> attribute = new HashMap<>();
//            for (Map<String, String> entry : attributeList) {
//                String skuCode = entry.get("sku_codes");
//                String attributes = entry.get("attributes");
//                attribute.put(skuCode, attributes);
//            }
//            productDetail.setAttributes(attribute);
//
//            List<ProductPictureDTO> pictureList = dao.getProductPictures(id);
//            productDetail.setPicturesList(pictureList);
//
//            return productDetail;
//        }).subscribeOn(jdbcScheduler);
//    }
//
//    @Override
//    public Flux<ProductDTO> listAllProduct() {
//        return productRepository.findAllByPublishStatus(PublishStatus.PUBLISHED.getValue())
//                .flatMap(product -> Flux.just(dtoMapper.productToProductDTO(product)));
//    }
//
//    @Override
//    public Flux<ProductDTO> listProduct(int pageNum, int pageSize) {
//        return Mono.fromCallable(() -> {
//            productExample.createCriteria().andPublishStatusEqualTo(1);
//            List<Product> productList = productMapper.selectByExample(productExample);
//            List<ProductDTO> dtoList = dtoMapper.productToProductDTO(productList);
//
//            return dtoList;
//        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
//    }
}
