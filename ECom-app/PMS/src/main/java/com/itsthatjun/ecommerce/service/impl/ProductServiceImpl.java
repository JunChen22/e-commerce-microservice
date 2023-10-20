package com.itsthatjun.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.dao.ProductDao;
import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsProductOutEvent;
import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductUpdateLogMapper;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final ProductUpdateLogMapper logMapper;

    private final ProductDao dao;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductSkuMapper skuMapper, ProductUpdateLogMapper logMapper,
                              ProductDao dao, StreamBridge streamBridge,
                              @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.logMapper = logMapper;
        this.dao = dao;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<ProductDetail> getProduct(int id) {
        return Mono.fromCallable(() -> {
                    ProductDetail productDetail = new ProductDetail();
                    Product product = productMapper.selectByPrimaryKey(id);

                    ProductSkuExample skuExample = new ProductSkuExample();
                    skuExample.createCriteria().andProductIdEqualTo(product.getId());
                    List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

                    productDetail.setProduct(product);  // TODO: set dao to get product detail and attribute and review
                    productDetail.setSkuVariants(skuList);
                    return productDetail;
                }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listAllProduct() {
        return Mono.fromCallable(() -> {
                    ProductExample productExample = new ProductExample();
                    productExample.createCriteria().andPublishStatusEqualTo(1);
                    return productMapper.selectByExample(productExample);
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listProduct(int pageNum, int pageSize) {
        return Mono.fromCallable(() -> {
                    PageHelper.startPage(pageNum, pageSize);
                    ProductExample productExample = new ProductExample();
                    productExample.createCriteria().andPublishStatusEqualTo(1);
                    return productMapper.selectByExample(productExample);
                })
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> createProduct(Product newProduct, List<ProductSku> skuList) {
        return Mono.fromCallable(() ->
                internalCreateProduct(newProduct, skuList)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalCreateProduct(Product newProduct, List<ProductSku> skuList) {

        newProduct.setCreatedAt(new Date());
        productMapper.insert(newProduct);

        int newProductId = newProduct.getId();

        for (ProductSku sku : skuList) {
            sku.setProductId(newProductId);
            skuMapper.insert(sku);
        }

        ProductUpdateLog newLog = new ProductUpdateLog();

       //   TODO: private method for general update.
        newLog.setProductId(newProductId);
        newLog.setCreatedAt(new Date());
        logMapper.insert(newLog);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT, newProduct, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT, newProduct, skuList));

        return newProduct;
    }

    @Override
    public Mono<Product> addProductSku(Product currentProduct, ProductSku newSKu) {
        return Mono.fromCallable(() ->
                internalAddProductSku(currentProduct, newSKu)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalAddProductSku(Product currentProduct, ProductSku newSKu) {

        int productId = currentProduct.getId();
        Product foundProduct = productMapper.selectByPrimaryKey(productId);

        if (foundProduct == null) throw new ProductException("Product does not exist, product id: " + productId);

        ProductSkuExample skuExample = new ProductSkuExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

        newSKu.setProductId(productId);
        skuMapper.insert(newSKu);
        skuList.add(newSKu);

        double currentPrice = currentProduct.getOriginalPrice().doubleValue();
        double currentSalePrice = currentProduct.getSalePrice().doubleValue();
        double skuPrice = newSKu.getPrice().doubleValue();

        // update price to highest, sku is just a variant of product.
        if (skuPrice > currentPrice) {
            currentProduct.setOriginalPrice(BigDecimal.valueOf(skuPrice));

            for (ProductSku productSku : skuList) {
                productSku.setPrice(BigDecimal.valueOf(skuPrice));
                skuMapper.updateByPrimaryKey(productSku);
            }
            currentProduct.setOriginalPrice(BigDecimal.valueOf(skuPrice));
            productMapper.updateByPrimaryKey(currentProduct);
        }

        // new lowest price
        if (skuPrice < currentSalePrice) {
            currentProduct.setSalePrice(BigDecimal.valueOf(skuPrice));
            productMapper.updateByPrimaryKey(currentProduct);
        }

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT_SKU, currentProduct, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT_SKU, currentProduct, skuList));
        return currentProduct;
    }

    @Override
    public Mono<Product> updateProductInfo(Product updatedProduct) {
        return Mono.fromCallable(() ->
                internalUpdateProductInfo(updatedProduct)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalUpdateProductInfo(Product updatedProduct) {
        // TODO: there might be category name, but that might as well create new product.
        // TODO: update sku too
        // todo update will over write the existing data like publish status, i didn't include it in postman
        //     but it will make it null. use update by existing data, use productMapper.updateByExampleSelective(
        int productId = updatedProduct.getId();
        Product foundProduct = productMapper.selectByPrimaryKey(productId);

        if (foundProduct == null) throw new ProductException("Product id does not exist : " + productId);
        productMapper.updateByPrimaryKey(updatedProduct);

        return updatedProduct;
    }

    @Override
    public Mono<ProductSku> updateProductStock(ProductSku sku, int addedStock) {
        return Mono.fromCallable(() ->
                internalUpdateProductStock(sku, addedStock)
        ).subscribeOn(jdbcScheduler);
    }

    private ProductSku internalUpdateProductStock(ProductSku sku, int addedStock) {
        ProductSku foundSku = skuMapper.selectByPrimaryKey(sku.getId());
        if (foundSku == null) throw new ProductException("Sku does not exist: " + sku.getSkuCode());

        int productId = foundSku.getProductId();
        int skuStock = foundSku.getStock();
        foundSku.setStock(skuStock + addedStock);
        skuMapper.updateByPrimaryKeySelective(foundSku);

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) throw new ProductException("product id doesn't exist: " + productId);

        int currentStock = product.getStock();
        product.setStock(currentStock + addedStock);

        productMapper.updateByPrimaryKey(product);

        List<ProductSku> skuList = new ArrayList<>();
        skuList.add(foundSku);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, product, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, product, skuList));

        return sku;
    }

    @Override
    public Mono<Product> updateProductPrice(List<ProductSku> productSkuList) {
        return Mono.fromCallable(
                () -> internalUpdateProductPrice(productSkuList)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalUpdateProductPrice(List<ProductSku> productSkuList) {
        double newHighestPrice = 0;
        double newLowPrice = 0;
        for (ProductSku sku : productSkuList) {
            double skuSalePrice = sku.getPromotionPrice().doubleValue();
            double skuPrice = sku.getPrice().doubleValue();
            newHighestPrice = Math.max(newHighestPrice, skuPrice);
            newLowPrice = Math.min(newLowPrice, skuSalePrice);
        }

        // update highest/original price
        for (ProductSku sku : productSkuList) {
            sku.setPrice(BigDecimal.valueOf(newHighestPrice));
            skuMapper.updateByPrimaryKeySelective(sku);
        }

        int productId = productSkuList.get(0).getProductId();
        Product product = productMapper.selectByPrimaryKey(productId);
        product.setSalePrice(BigDecimal.valueOf(newLowPrice));
        product.setOriginalPrice(BigDecimal.valueOf(newHighestPrice));

        productMapper.updateByPrimaryKeySelective(product);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, product, productSkuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, product, productSkuList));

        return product;
    }

    @Override
    public Mono<Product> updateProductStatus(Product updatedProduct) {
        return Mono.fromCallable(() ->
                internalUpdateProductStatus(updatedProduct)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalUpdateProductStatus(Product updatedProduct) {
        int productId = updatedProduct.getId();
        Product foundProduct = productMapper.selectByPrimaryKey(productId);

        if (foundProduct == null) throw new ProductException("Product does not exist, product id: " + productId);

        int currentPublishStatus = foundProduct.getPublishStatus();
        int updateProductStatus = updatedProduct.getPublishStatus();

        List<ProductSku> affectedSkuList = new ArrayList<>();
        if (currentPublishStatus == updateProductStatus) {
            return updatedProduct;
        }

        // Turning product offline and all of its sku.
        // Turning online won't make the sku go on, need to use updateProductSkuStatus one by one.
        if (updateProductStatus == 0) {
            // changing to not active
            ProductSkuExample productSkuExample = new ProductSkuExample();
            productSkuExample.createCriteria().andProductIdEqualTo(productId).andStatusEqualTo(1);
            affectedSkuList = skuMapper.selectByExample(productSkuExample);

            for (ProductSku affectedSku : affectedSkuList) {
                affectedSku.setStatus(0);
                skuMapper.updateByPrimaryKey(affectedSku);
            }
        }

        productMapper.updateByPrimaryKey(updatedProduct);
        // TODO: create log update

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, affectedSkuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, affectedSkuList));

        return updatedProduct;
    }

    @Override
    public Mono<ProductSku> updateProductSkuStatus(ProductSku updateSku) {
        return Mono.fromCallable(() ->
                internalUpdateProductSkuStatus(updateSku)
        ).subscribeOn(jdbcScheduler);
    }

    private ProductSku internalUpdateProductSkuStatus(ProductSku updateSku) {
        // find out if the product is online first
        int productId = updateSku.getId();
        Product foundProduct = productMapper.selectByPrimaryKey(productId);

        if (foundProduct == null) throw new RuntimeException("Product does not exist, product id: " + productId);

        if (foundProduct.getPublishStatus() == 0) throw new ProductException("Product is not active, sku can not be set until product id: " + productId + "is active");

        List<ProductSku> skuList = new ArrayList<>();
        skuList.add(updateSku);
        // TODO: when there's no more sku, might want to notify admin or just take the product off line too
        skuMapper.updateByPrimaryKeySelective(updateSku);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));

        return updateSku;
    }

    @Override
    public Mono<ProductSku> removeProductSku(ProductSku removeSku) {
        return Mono.fromCallable(
                () -> internalRemoveProductSku(removeSku)
        ).subscribeOn(jdbcScheduler);
    }

    private ProductSku internalRemoveProductSku(ProductSku removeSku) {

        int productId = removeSku.getProductId();

        ProductSkuExample skuExample = new ProductSkuExample();
        skuExample.createCriteria().andProductIdEqualTo(productId);
        List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

        if (!skuList.contains(removeSku)) throw new ProductException("Sku does not exist or belong to product id: " + productId);

        int skuId = removeSku.getId();
        skuList.remove(removeSku);
        skuMapper.deleteByPrimaryKey(skuId);
        Product foundProduct = productMapper.selectByPrimaryKey(productId);

        // update product price info.
        if (!skuList.isEmpty()) {
            updateProductPrice(skuList);
        }

        List<ProductSku> removedSkuList = new ArrayList<>();
        removedSkuList.add(removeSku);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removedSkuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removedSkuList));
        return removeSku;
    }

    @Override
    public Mono<Void> deleteProduct(int id) {
        return Mono.fromRunnable(() ->
            internalDeleteProduct(id)
        ).subscribeOn(jdbcScheduler).then();
    }

    public void internalDeleteProduct(int id) {
        Product foundProduct = productMapper.selectByPrimaryKey(id);
        foundProduct.setPublishStatus(0);
        foundProduct.setDeleteStatus(1);

        ProductSkuExample example = new ProductSkuExample();
        example.createCriteria().andProductIdEqualTo(foundProduct.getId());
        List<ProductSku> skuList = skuMapper.selectByExample(example);

        for (ProductSku sku : skuList) {
            sku.setStatus(0);
            skuMapper.updateByPrimaryKeySelective(sku);
        }

        productMapper.updateByPrimaryKeySelective(foundProduct);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.DELETE_PRODUCT, foundProduct, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.DELETE_PRODUCT, foundProduct, skuList));
    }

    private void sendOmsStockUpdateMessage(String bindingName, OmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendSalesStockUpdateMessage(String bindingName, SmsProductOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
