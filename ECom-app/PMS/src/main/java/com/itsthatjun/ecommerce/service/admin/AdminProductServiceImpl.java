package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.dao.ProductDao;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsProductOutEvent;
import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.ProductMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductSkuMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ProductUpdateLogMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuExample;
import com.itsthatjun.ecommerce.mbg.model.ProductUpdateLog;
import com.itsthatjun.ecommerce.service.AdminProductService;
import com.itsthatjun.ecommerce.service.impl.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final ProductUpdateLogMapper logMapper;

    private final ProductDao dao;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final DTOMapper dtoMapper;

    @Autowired
    public AdminProductServiceImpl(ProductMapper productMapper, ProductSkuMapper skuMapper, ProductUpdateLogMapper logMapper,
                              ProductDao dao, StreamBridge streamBridge, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
                              DTOMapper dtoMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.logMapper = logMapper;
        this.dao = dao;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Mono<Product> createProduct(Product newProduct, List<ProductSku> skuList, String operator) {
        return Mono.fromCallable(() -> {
            newProduct.setCreatedAt(new Date());
            productMapper.insert(newProduct);

            int newProductId = newProduct.getId();

            for (ProductSku sku : skuList) {
                sku.setProductId(newProductId);
                skuMapper.insert(sku);
            }

            createUpdateLog(newProduct, newProduct, "create product", operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT, newProduct, skuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT, newProduct, skuList));

            return newProduct;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> addProductSku(Product currentProduct, ProductSku newSKu, String operator) {
        return Mono.fromCallable(() ->
                internalAddProductSku(currentProduct, newSKu, operator)
        ).subscribeOn(jdbcScheduler);
    }

    private Product internalAddProductSku(Product currentProduct, ProductSku newSKu, String operator) {
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

        createUpdateLog(foundProduct, foundProduct, "add new sku", operator);

        sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT_SKU, currentProduct, skuList));
        sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT_SKU, currentProduct, skuList));
        return currentProduct;
    }

    @Override
    public Mono<Product> updateProductInfo(Product updatedProduct, String operator) {
        return Mono.fromCallable(() -> {
            // TODO: update sku too
            int productId = updatedProduct.getId();
            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            if (foundProduct == null) throw new ProductException("Product id does not exist : " + productId);
            productMapper.updateByPrimaryKeySelective(updatedProduct);

            createUpdateLog(foundProduct, updatedProduct, "update product info", operator);

            return updatedProduct;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ProductSku> updateProductStock(ProductSku sku, int addedStock, String operator) {
        return Mono.fromCallable(() -> {
            ProductSku foundSku = skuMapper.selectByPrimaryKey(sku.getId());
            if (foundSku == null) throw new ProductException("Sku does not exist: " + sku.getSkuCode());

            int productId = foundSku.getProductId();
            int skuStock = foundSku.getStock();
            foundSku.setStock(skuStock + addedStock);
            skuMapper.updateByPrimaryKeySelective(foundSku);

            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            if (foundProduct == null) throw new ProductException("product id doesn't exist: " + productId);

            int currentStock = foundProduct.getStock();
            foundProduct.setStock(currentStock + addedStock);

            productMapper.updateByPrimaryKey(foundProduct);

            List<ProductSku> skuList = new ArrayList<>();
            skuList.add(foundSku);

            createUpdateLog(foundProduct, foundProduct, "update product stock: " + foundProduct.getStock(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));

            return sku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> updateProductPrice(List<ProductSku> productSkuList, String operator) {
        return Mono.fromCallable(() -> {
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
            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            foundProduct.setSalePrice(BigDecimal.valueOf(newLowPrice));
            foundProduct.setOriginalPrice(BigDecimal.valueOf(newHighestPrice));

            productMapper.updateByPrimaryKeySelective(foundProduct);

            createUpdateLog(foundProduct, foundProduct, "update product price : " + foundProduct.getSalePrice(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, productSkuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, productSkuList));

            return foundProduct;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> updateProductStatus(Product updatedProduct, String operator) {
        return Mono.fromCallable(() -> {
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

            createUpdateLog(foundProduct, foundProduct, "update product status : " + updatedProduct.getPublishStatus(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, affectedSkuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, affectedSkuList));

            return updatedProduct;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ProductSku> updateProductSkuStatus(ProductSku updateSku, String operator) {
        return Mono.fromCallable(() -> {
            // find out if the product is online first
            int productId = updateSku.getId();
            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            if (foundProduct == null) throw new RuntimeException("Product does not exist, product id: " + productId);

            if (foundProduct.getPublishStatus() == 0) throw new ProductException("Product is not active, sku can not be set until product id: " + productId + "is active");

            List<ProductSku> skuList = new ArrayList<>();
            skuList.add(updateSku);
            // TODO: when there's no more sku, might want to notify admin or just take the product off line too
            skuMapper.updateByPrimaryKeySelective(updateSku);

            createUpdateLog(foundProduct, foundProduct, "update product sku status: " + updateSku.getStatus(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, skuList));

            return updateSku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ProductSku> removeProductSku(ProductSku removeSku, String operator) {
        return Mono.fromCallable(() -> {
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
                updateProductPrice(skuList, operator);
            }

            List<ProductSku> removedSkuList = new ArrayList<>();
            removedSkuList.add(removeSku);

            createUpdateLog(foundProduct, foundProduct,"Remove product sku: " + removeSku.getSkuCode(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removedSkuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removedSkuList));
            return removeSku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteProduct(int id, String operator) {
        return Mono.fromRunnable(() -> {
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

            createUpdateLog(foundProduct, foundProduct, "delete product", operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(OmsProductOutEvent.Type.DELETE_PRODUCT, foundProduct, skuList));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.DELETE_PRODUCT, foundProduct, skuList));
        }).subscribeOn(jdbcScheduler).then();
    }

    private void createUpdateLog(Product oldProductInfo, Product newProductInfo, String updateAction, String operator) {
        ProductUpdateLog updateLog = new ProductUpdateLog();
        updateLog.setProductId(oldProductInfo.getId());

        // price related
        updateLog.setPriceOld(oldProductInfo.getOriginalPrice());
        updateLog.setPriceNew(newProductInfo.getOriginalPrice());
        updateLog.setSalePriceOld(oldProductInfo.getSalePrice());
        updateLog.setSalePriceNew(newProductInfo.getSalePrice());

        // stock related
        updateLog.setOldStock(oldProductInfo.getStock());
        updateLog.setAddedStock(newProductInfo.getStock() - oldProductInfo.getStock());
        updateLog.setTotalStock(newProductInfo.getStock());

        // update information
        updateLog.setUpdateAction(updateAction);
        updateLog.setOperator(operator);
        updateLog.setCreatedAt(new Date());
        logMapper.insert(updateLog);
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
