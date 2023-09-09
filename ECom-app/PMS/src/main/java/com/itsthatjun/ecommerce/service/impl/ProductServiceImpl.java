package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuStockMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductExample;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuStock;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuStockExample;
import com.itsthatjun.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductSkuStockMapper skuStockMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductSkuStockMapper skuStockMapper) {
        this.productMapper = productMapper;
        this.skuStockMapper = skuStockMapper;
    }

    @Override
    public Product getProduct(int id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Product> listAllProduct() {
        return productMapper.selectByExample(new ProductExample());
    }

    @Override
    public List<Product> listProduct(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return productMapper.selectByExample(new ProductExample());
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
