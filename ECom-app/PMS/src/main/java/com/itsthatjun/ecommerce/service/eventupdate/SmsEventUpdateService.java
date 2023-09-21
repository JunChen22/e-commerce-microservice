package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class SmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    @Autowired
    public SmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
    }

    @ApiOperation("Update sale price, logic is done already in SMS")
    public void updateSale(List<ProductSku> productSkuList) {
        for (ProductSku sku : productSkuList) {
            skuMapper.updateByPrimaryKey(sku);

            int productId = sku.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);

            double currentSalePrice = affectedProduct.getSalePrice().doubleValue();
            double skuSalePrice = sku.getPromotionPrice().doubleValue();

            affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentSalePrice, skuSalePrice)));
            affectedProduct.setOnSaleStatus(1);
            productMapper.updateByPrimaryKey(affectedProduct);
        }
        LOG.info("Updated price for %d products", productSkuList.size());
    }

    @ApiOperation("Remove sale price and update product")
    public void removeSale(List<ProductSku> productSkuList) {
        for (ProductSku sku : productSkuList) {
            skuMapper.updateByPrimaryKey(sku);

            int productId = sku.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            affectedProduct.setSalePrice(affectedProduct.getOriginalPrice());

            affectedProduct.setOnSaleStatus(0);
            productMapper.updateByPrimaryKey(affectedProduct);
        }
        LOG.info("Updated price for %d products", productSkuList.size());
    }
}
