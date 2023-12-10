package com.itsthatjun.ecommerce.service.admin;

import com.github.pagehelper.PageHelper;
import com.itsthatjun.ecommerce.dao.AdminProductDao;
import com.itsthatjun.ecommerce.dto.AdminProductDetail;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.SmsProductOutEvent;
import com.itsthatjun.ecommerce.dto.model.Attribute;
import com.itsthatjun.ecommerce.exceptions.ProductException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.AdminProductService;
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
import java.util.*;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminProductServiceImpl.class);

    private final ProductMapper productMapper;

    private final ProductSkuMapper skuMapper;

    private final ProductUpdateLogMapper logMapper;

    private final AdminProductDao productDao;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final ProductAttributeMapper attributeMapper;

    private final ProductPicturesMapper picturesMapper;

    private final ProductAlbumMapper albumMapper;

    @Autowired
    public AdminProductServiceImpl(ProductMapper productMapper, ProductSkuMapper skuMapper, ProductUpdateLogMapper logMapper,
                                   AdminProductDao dao, StreamBridge streamBridge, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
                                   ProductAttributeMapper attributeMapper, ProductPicturesMapper picturesMapper, ProductAlbumMapper albumMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.logMapper = logMapper;
        this.productDao = dao;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
        this.attributeMapper = attributeMapper;
        this.picturesMapper = picturesMapper;
        this.albumMapper = albumMapper;
    }

    @Override
    public Mono<AdminProductDetail> getProductDetail(int id) {
        return Mono.fromCallable(() -> {
            AdminProductDetail productDetail = new AdminProductDetail();
            productDetail.setProduct(productDao.getProduct(id));
            productDetail.setPicturesList(productDao.getProductPictures(id));

            // TODO: add on dao to get detail
            // productDetail.setSkuVariants(productDao.getAllSku(id));

            List<Map<String, String>> attributeList = productDao.getProductAttributes(id);
            Map<String, String> attribute = new HashMap<>();
            for (Map<String, String> entry : attributeList) {
                String skuCode = entry.get("sku_codes");
                String attributes = entry.get("attributes");
                attribute.put(skuCode, attributes);
            }
            //productDetail.setAttributes(attribute);

            return productDetail;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listAllProduct() {
        return Mono.fromCallable(() -> {
            List<Product> productList = productDao.getAllProduct();
            return productList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> listProduct(int pageNum, int pageSize) {
        return Mono.fromCallable(() -> {
            PageHelper.startPage(pageNum, pageSize);
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andPublishStatusEqualTo(1);
            List<Product> productList = productMapper.selectByExample(productExample);

            return productList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> createProduct(AdminProductDetail productDetail, String operator) {
        return Mono.fromCallable(() -> {
            Date currentTime = new Date();

            Product newProduct = productDetail.getProduct();
            ProductSku newSku = productDetail.getSkuVariants();
            List<ProductPictures> picturesList = productDetail.getPicturesList();
            Map<String, Attribute> attributes = productDetail.getAttributes();
            String categoryName = newProduct.getCategoryName();

            newProduct.setCreatedAt(currentTime);
            productMapper.insert(newProduct);

            int newProductId = newProduct.getId();

            newSku.setProductId(newProductId);
            skuMapper.insert(newSku);
            String skuCode = newSku.getSkuCode();

            addAttribute(newProductId, skuCode, attributes, categoryName);
            addPicture(newProductId, picturesList);

            createUpdateLog(newProduct, newProduct, "create product", operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.NEW_PRODUCT, newProduct, newSku));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT, newProduct, newSku));

            return newProduct;
        }).subscribeOn(jdbcScheduler);
    }

    private void addAttribute(int productId, String skuCode, Map<String, Attribute> attributes, String categoryName) {
        // get attribute types by category name, MyBatis won't map directly it behave a little different.
        // each row is a map and multiple row is a list. alias or colum name became key and colum became value.
        List<Map<String, Object>> attributeListMap = productDao.getAttributeType(categoryName);
        Map<String, Integer> attributeTypes = new HashMap<>();

        for (Map<String, Object> map : attributeListMap) {
            attributeTypes.put((String) map.get("attribute_type_name"), (Integer) map.get("attribute_type_id"));
        }

        ProductAttribute attribute = new ProductAttribute();
        attribute.setProductId(productId);
        attribute.setSkuCode(skuCode);

        // insert attributeList
        for (String type : attributeTypes.keySet()) {
            String value = attributes.get(type).getValue();
            String unit = attributes.get(type).getUnit();
            int attributeTypeId = attributeTypes.get(type);

            attribute.setAttributeValue(value);
            attribute.setAttributeUnit(unit);
            attribute.setAttributeTypeId(attributeTypeId);

            attributeMapper.insert(attribute);
        }
    }

    private void addPicture(int productId, List<ProductPictures> picturesList) {
        // find if album exist, if not create one
        Date currentTime = new Date();

        ProductAlbumExample albumExample = new ProductAlbumExample();
        albumExample.createCriteria().andProductIdEqualTo(productId);
        List<ProductAlbum> albumList = albumMapper.selectByExample(albumExample);

        ProductAlbum album = !albumList.isEmpty() ? albumList.get(0) : null;

        if (albumList.isEmpty()) {
            ProductAlbum newAlbum = new ProductAlbum();
            newAlbum.setProductId(productId);
            newAlbum.setCreatedAt(currentTime);
            newAlbum.setPicCount(picturesList.size());
            newAlbum.setName("new album");
            newAlbum.setDescription("new desc");
            albumMapper.insert(newAlbum);

            album = newAlbum;
        }

        int albumId = album.getId();

        for (ProductPictures picture : picturesList) {
            picture.setProductAlbumId(albumId);
            picture.setCreatedAt(currentTime);

            picturesMapper.insert(picture);
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        product.setPictureAlbum(albumId);
        productMapper.updateByPrimaryKey(product);
    }

    @Override
    public Mono<Product> addProductSku(AdminProductDetail productDetail, String operator) {
        return Mono.fromCallable(() -> {
            int productId = productDetail.getProduct().getId();
            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            if (foundProduct == null) throw new ProductException("Product does not exist, product id: " + productId);

            ProductSku newSKu = productDetail.getSkuVariants();
            newSKu.setProductId(productId);
            skuMapper.insert(newSKu);
            String skuCode = newSKu.getSkuCode();

            List<ProductPictures> picturesList = productDetail.getPicturesList();
            if (!picturesList.isEmpty()) addPicture(productId, picturesList);

            Map<String, Attribute> attributes = productDetail.getAttributes();
            addAttribute(productId, skuCode, attributes, foundProduct.getCategoryName());

            // update product price
            Product updateProduct = productSkuPriceUpdate(productId);

            createUpdateLog(foundProduct, updateProduct, "add new sku", operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.NEW_PRODUCT_SKU, foundProduct, newSKu));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.NEW_PRODUCT_SKU, foundProduct, newSKu));
            return foundProduct;
        }).subscribeOn(jdbcScheduler);
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

            createUpdateLog(foundProduct, foundProduct, "update product stock: " + foundProduct.getStock(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, sku));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, foundProduct, sku));

            return sku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Product> updateProductPrice(ProductSku sku, String operator) {
        return Mono.fromCallable(() -> {
            skuMapper.updateByPrimaryKeySelective(sku);

            // update product price
            int productId = sku.getProductId();
            Product oldProduct = productMapper.selectByPrimaryKey(productId);
            Product updatedProduct = productSkuPriceUpdate(productId);

            createUpdateLog(oldProduct, updatedProduct, "update product price : " + updatedProduct.getSalePrice(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, sku));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, sku));

            return updatedProduct;
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
            if (currentPublishStatus == updateProductStatus) return updatedProduct;  // same status no changes.

            // Turning product offline and all of its sku.
            // Turning online won't make the sku go on, need to use updateProductSkuStatus one by one.
            if (updateProductStatus == 0) {
                // changing to not active
                ProductSkuExample productSkuExample = new ProductSkuExample();
                productSkuExample.createCriteria().andProductIdEqualTo(productId).andStatusEqualTo(1);
                List<ProductSku> affectedSkuList = skuMapper.selectByExample(productSkuExample);

                for (ProductSku affectedSku : affectedSkuList) {
                    affectedSku.setStatus(0);
                    skuMapper.updateByPrimaryKey(affectedSku);
                }
            }

            productMapper.updateByPrimaryKey(updatedProduct);
            createUpdateLog(foundProduct, foundProduct, "update product status : " + updatedProduct.getPublishStatus(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, null));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT, updatedProduct, null));

            return updatedProduct;
        }).subscribeOn(jdbcScheduler);
    }

    // update product and sku high and low price if there's any change
    private Product productSkuPriceUpdate(int productId) {
        Product product = productMapper.selectByPrimaryKey(productId);

        ProductSkuExample skuExample = new ProductSkuExample();
        skuExample.createCriteria().andProductIdEqualTo(productId).andStatusEqualTo(1);
        List<ProductSku> skuList = skuMapper.selectByExample(skuExample);

        double newHighestPrice = product.getOriginalPrice().doubleValue();
        double newLowPrice = product.getSalePrice().doubleValue();

        // find low and high price of sku
        for (ProductSku productSku : skuList) {
            double skuSalePrice = productSku.getPromotionPrice().doubleValue();
            double skuPrice = productSku.getPrice().doubleValue();
            newHighestPrice = Math.max(newHighestPrice, skuPrice);
            newLowPrice = Math.min(newLowPrice, skuSalePrice);
        }

        // update price to highest, sku is just a variant of product.
        double skuPrice = product.getOriginalPrice().doubleValue();
        if (newHighestPrice > product.getOriginalPrice().doubleValue()) {
            for (ProductSku productSku : skuList) {
                productSku.setPrice(BigDecimal.valueOf(skuPrice));
                skuMapper.updateByPrimaryKey(productSku);
            }
        }

        // update product price
        product.setSalePrice(BigDecimal.valueOf(newLowPrice));
        product.setOriginalPrice(BigDecimal.valueOf(newHighestPrice));
        productMapper.updateByPrimaryKeySelective(product);

        return product;
    }

    @Override
    public Mono<ProductSku> updateProductSkuStatus(ProductSku updateSku, String operator) {
        return Mono.fromCallable(() -> {
            // find out if the product is online first
            int productId = updateSku.getId();
            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            if (foundProduct == null) throw new RuntimeException("Product does not exist, product id: " + productId);

            int skuId = updateSku.getId();
            ProductSku foundSku = skuMapper.selectByPrimaryKey(skuId);
            if (foundSku == null) throw new RuntimeException("Product sku does not exist, sku code: " + foundSku.getSkuCode());

            skuMapper.updateByPrimaryKeySelective(updateSku);

            createUpdateLog(foundProduct, foundProduct, "update product sku status: " + updateSku.getStatus(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.UPDATE_PRODUCT_STATUS, foundProduct, updateSku));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.UPDATE_PRODUCT_STATUS, foundProduct, updateSku));

            return updateSku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ProductSku> removeProductSku(ProductSku removeSku, String operator) {
        return Mono.fromCallable(() -> {
            int productId = removeSku.getProductId();
            Product foundProduct = productMapper.selectByPrimaryKey(productId);

            ProductSkuExample skuExample = new ProductSkuExample();
            skuExample.createCriteria().andProductIdEqualTo(productId);
            List<ProductSku> skuList = skuMapper.selectByExample(skuExample);
            if (!skuList.contains(removeSku)) throw new ProductException("Sku does not exist or belong to product id: " + productId);

            skuMapper.updateByPrimaryKeySelective(removeSku);   // soft delete

            // update product price.
            Product updatedProduct = productSkuPriceUpdate(productId);

            createUpdateLog(foundProduct, updatedProduct,"Remove product sku: " + removeSku.getSkuCode(), operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removeSku));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT_SKU, foundProduct, removeSku));
            return removeSku;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> removeProduct(int productId, String operator) {
        return Mono.fromRunnable(() -> {
            Product foundProduct = productMapper.selectByPrimaryKey(productId);
            foundProduct.setPublishStatus(0);
            foundProduct.setDeleteStatus(1);
            productMapper.updateByPrimaryKeySelective(foundProduct);

            ProductSkuExample example = new ProductSkuExample();
            example.createCriteria().andProductIdEqualTo(foundProduct.getId());
            List<ProductSku> skuList = skuMapper.selectByExample(example);

            for (ProductSku sku : skuList) {
                sku.setStatus(0);
                skuMapper.updateByPrimaryKeySelective(sku);
            }

            createUpdateLog(foundProduct, foundProduct, "delete product", operator);

            sendSalesStockUpdateMessage("smsProductUpdate-out-0", new SmsProductOutEvent(SmsProductOutEvent.Type.REMOVE_PRODUCT, foundProduct, null));
            sendOmsStockUpdateMessage("omsProductUpdate-out-0", new OmsProductOutEvent(OmsProductOutEvent.Type.REMOVE_PRODUCT, foundProduct, null));
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