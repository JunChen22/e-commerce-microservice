package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuExample;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(OmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    @Autowired
    public OmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
    }

    @ApiOperation("Generated order, increase sku lock stock")
    public void updatePurchase(Map<String, Integer> skuQuantity) {

        Set<String> skuSet = skuQuantity.keySet();

        for (String sku : skuSet) {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) throw new RuntimeException("Unable to find product with SKU: " + sku);

            ProductSku skuProduct = skuList.get(0);

            int currentLockStock = skuProduct.getLockStock();
            int quantityPurchased = skuQuantity.get(sku);
            skuProduct.setLockStock(currentLockStock + quantityPurchased);

            skuMapper.updateByPrimaryKey(skuProduct);
        }
    }

    @ApiOperation("Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock")
    public void updatePurchasePayment(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();

        for (String sku : skuSet) {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) throw new RuntimeException("Unable to find product with SKU: " + sku);

            ProductSku skuProduct = skuList.get(0);

            int productId = skuProduct.getProductId();
            int currentSkuStock = skuProduct.getStock();
            int currentSkuLockStock = skuProduct.getLockStock();
            int quantityPurchased = skuQuantity.get(sku);

            skuProduct.setLockStock(currentSkuLockStock - quantityPurchased);
            skuProduct.setStock(currentSkuStock - quantityPurchased);

            skuMapper.updateByPrimaryKey(skuProduct);

            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            int currentStock = foundProduct.getStock();
            foundProduct.setStock(currentStock - quantityPurchased);
            productMapper.updateByPrimaryKey(foundProduct);
        }
    }

    @ApiOperation("Generated order and success payment and return, increase product stock and sku stock")
    public void updateReturn(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();

        for (String sku : skuSet) {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) throw new RuntimeException("Unable to find product with SKU: " + sku);

            ProductSku skuProduct = skuList.get(0);

            int productId = skuProduct.getProductId();
            int currentSkuStock = skuProduct.getStock();
            int quantityReturned = skuQuantity.get(sku);

            skuProduct.setStock(currentSkuStock + quantityReturned);

            skuMapper.updateByPrimaryKey(skuProduct);

            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            int currentStock = foundProduct.getStock();
            foundProduct.setStock(currentStock + quantityReturned);
            productMapper.updateByPrimaryKey(foundProduct);
        }
    }

    @ApiOperation("Generated order and failure payment, decrease sku lock stock")
    public void updateFailPayment(Map<String, Integer> skuQuantity) {
        Set<String> skuSet = skuQuantity.keySet();

        for (String sku : skuSet) {
            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(sku);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

            if (skuList.isEmpty()) throw new RuntimeException("Unable to find product with SKU: " + sku);

            ProductSku skuProduct = skuList.get(0);

            int currentLockStock = skuProduct.getLockStock();
            int quantityFreed = skuQuantity.get(sku);
            skuProduct.setLockStock(currentLockStock - quantityFreed);

            skuMapper.updateByPrimaryKey(skuProduct);
        }
    }
}
