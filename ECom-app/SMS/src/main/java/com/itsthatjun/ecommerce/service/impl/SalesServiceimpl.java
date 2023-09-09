package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SalesServiceimpl implements SalesService {

    private final PromotionSaleMapper promotionSaleMapper;

    private final PromotionSaleProductRelationMapper productRelationMapper;

    private final ProductSkuStockMapper skuStockMapper;

    private final PromotionSaleLogMapper promotionSaleLogMapper;

    private final ProductMapper productMapper;

    @Autowired
    public SalesServiceimpl(PromotionSaleMapper promotionSaleMapper, PromotionSaleProductRelationMapper productRelationMapper,
                            ProductSkuStockMapper skuStockMapper, PromotionSaleLogMapper promotionSaleLogMapper, ProductMapper productMapper) {
        this.promotionSaleMapper = promotionSaleMapper;
        this.productRelationMapper = productRelationMapper;
        this.skuStockMapper = skuStockMapper;
        this.promotionSaleLogMapper = promotionSaleLogMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<PromotionSale> getAllPromotionalSale() {
        PromotionSaleExample saleExample = new PromotionSaleExample();
        saleExample.createCriteria().andStatusEqualTo(1);  // default active sales
        List<PromotionSale> result = promotionSaleMapper.selectByExample(saleExample);
        return result.size() > 0 ? result : null;
    }

    // 0-> not on sale; 1-> is on sale; 2-> flash sale/special sales/clarance/used item

    @Override
    public List<Product> getAllPromotionalSaleItems() {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andOnSaleStatusEqualTo(1);
        List<Product> result = productMapper.selectByExample(productExample);
        return result.size() > 0 ? result : null;
    }

    @Override
    public List<Product> getAllFlashSaleItems() {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andOnSaleStatusEqualTo(2);
        List<Product> result = productMapper.selectByExample(productExample);
        return result.size() > 0 ? result : null;
    }

    @Override
    public void updatePurchase(Map<String, Integer> skuQuantityMap) {
        for (String skuCode: skuQuantityMap.keySet()) {
            int quantityNeeded = skuQuantityMap.get(skuCode);

            ProductSkuStockExample skuStockExample = new ProductSkuStockExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = skuStockMapper.selectByExample(skuStockExample).get(0);

            int currentLockStock = skuStock.getLockStock();

            skuStock.setLockStock(currentLockStock + quantityNeeded);

            skuStockMapper.updateByPrimaryKey(skuStock);
        }
    }

    @Override
    public void updatePurchasePayment(Map<String, Integer> skuQuantityMap) {
        for (String skuCode: skuQuantityMap.keySet()) {
            int quantityNeeded = skuQuantityMap.get(skuCode);

            ProductSkuStockExample skuStockExample = new ProductSkuStockExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = skuStockMapper.selectByExample(skuStockExample).get(0);

            int currentLockStock = skuStock.getLockStock();
            int currentSkuStock = skuStock.getStock();

            skuStock.setStock(currentSkuStock - quantityNeeded);
            skuStock.setLockStock(currentLockStock - quantityNeeded);

            int productId = skuStock.getProductId();

            // find product and update stock
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(productId);
            Product product = productMapper.selectByExample(productExample).get(0);

            int currentProductStock = product.getStock();
            product.setStock(currentProductStock - quantityNeeded);

            productMapper.updateByPrimaryKey(product);
            skuStockMapper.updateByPrimaryKey(skuStock);
        }
    }

    @Override
    public void updateReturn(Map<String, Integer> skuQuantityMap) {
        for (String skuCode: skuQuantityMap.keySet()) {
            int quantityNeeded = skuQuantityMap.get(skuCode);

            ProductSkuStockExample skuStockExample = new ProductSkuStockExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = skuStockMapper.selectByExample(skuStockExample).get(0);

            int currentSkuStock = skuStock.getStock();

            skuStock.setStock(currentSkuStock + quantityNeeded);

            int productId = skuStock.getProductId();

            // find product and update stock
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andIdEqualTo(productId);
            Product product = productMapper.selectByExample(productExample).get(0);

            int currentProductStock = product.getStock();
            product.setStock(currentProductStock + quantityNeeded);

            productMapper.updateByPrimaryKey(product);
            skuStockMapper.updateByPrimaryKey(skuStock);
        }
    }

    @Override
    public void updateFailPayment(Map<String, Integer> skuQuantityMap) {
        for (String skuCode: skuQuantityMap.keySet()) {
            int quantityNeeded = skuQuantityMap.get(skuCode);

            ProductSkuStockExample skuStockExample = new ProductSkuStockExample();
            skuStockExample.createCriteria().andSkuCodeEqualTo(skuCode);
            ProductSkuStock skuStock = skuStockMapper.selectByExample(skuStockExample).get(0);

            int currentLockStock = skuStock.getLockStock();

            skuStock.setLockStock(currentLockStock - quantityNeeded);

            skuStockMapper.updateByPrimaryKey(skuStock);
        }
    }
}
