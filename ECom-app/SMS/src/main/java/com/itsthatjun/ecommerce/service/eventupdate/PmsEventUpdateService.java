package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(PmsEventUpdateService.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    @Autowired
    public PmsEventUpdateService(ProductMapper productMapper, ProductSkuMapper skuMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
    }

    @ApiOperation("Add product to local database, only need price, sale price, stock, lock stock, sku and id.")
    public void addProduct(Product newProduct, List<ProductSku> newSkuList) {

        productMapper.insert(newProduct);

        for (ProductSku sku : newSkuList){
            skuMapper.insert(sku);
        }
        int addedProductId = newProduct.getId();
        int skuSize = newSkuList.size();
        LOG.info("Added product with id: %d, and %d product sku", addedProductId, skuSize);
    }

    @ApiOperation("Add a sku to product")
    public void addProductSku(ProductSku newSku) {
        skuMapper.insert(newSku);

        int productId = newSku.getProductId();
        int skuId = newSku.getId();
        String skuCode = newSku.getSkuCode();
        LOG.info("Added sku to product id: %d, with sku id : %d and sku code: %s", productId, skuId, skuCode);
    }

    @ApiOperation("Update product info and sku like stock, original price, online status. Logic is done in PMS already.")
    public void updateProduct(Product affectedProduct, List<ProductSku> skuList) {
        productMapper.updateByPrimaryKey(affectedProduct);
        for (ProductSku sku : skuList){
            skuMapper.updateByPrimaryKey(sku);
        }

        int addedProductId = affectedProduct.getId();
        int skuSize = skuList.size();
        LOG.info("Updated product with id: %d, and %d product sku", addedProductId, skuSize);
    }

    @ApiOperation("Remove a sku from product")
    public void removeProductSku(ProductSku skuToRemove) {
        int productId = skuToRemove.getProductId();
        int deleteSkuId = skuToRemove.getId();
        skuMapper.deleteByPrimaryKey(deleteSkuId);

        LOG.info("Delete product sku with id: %d, and %d product id", deleteSkuId, productId);
    }

    @ApiOperation("Remove product with all of its SKU")
    public void removeProduct(Product product, List<ProductSku> skuList) {
        int productId = product.getId();
        int skuListSize = skuList.size();

        for (ProductSku sku : skuList){
            int skuId = sku.getId();
            skuMapper.deleteByPrimaryKey(skuId);
        }
        productMapper.deleteByPrimaryKey(productId);
        LOG.info("Delete product with id: %d, and %d product sku.", productId, skuListSize);
    }
}
