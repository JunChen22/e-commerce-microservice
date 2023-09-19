package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.DiscountType;
import com.itsthatjun.ecommerce.dto.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsSaleOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsSaleOutEvent;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.OmsEventUpdateService;
import com.itsthatjun.ecommerce.service.SalesService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesServiceimpl implements SalesService {

    private static final Logger LOG = LoggerFactory.getLogger(SalesServiceimpl.class);

    private final PromotionSaleMapper promotionSaleMapper;

    private final PromotionSaleProductMapper promotionSaleProductMapper;

    private final PromotionSaleLogMapper promotionSaleLogMapper;

    private final ProductMapper productMapper;

    private final ProductSkuMapper productSkuMapper;

    private final StreamBridge streamBridge;

    @Autowired
    public SalesServiceimpl(PromotionSaleMapper promotionSaleMapper, PromotionSaleProductMapper promotionSaleProductMapper,
                            PromotionSaleLogMapper promotionSaleLogMapper, ProductMapper productMapper, ProductSkuMapper productSkuMapper,
                            StreamBridge streamBridge) {
        this.promotionSaleMapper = promotionSaleMapper;
        this.promotionSaleProductMapper = promotionSaleProductMapper;
        this.promotionSaleLogMapper = promotionSaleLogMapper;
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public Flux<PromotionSale> getAllPromotionalSale() {
        PromotionSaleExample saleExample = new PromotionSaleExample();
        saleExample.createCriteria().andStatusEqualTo(1);  // default active sales TODO: reduce returning infos
        List<PromotionSale> result = promotionSaleMapper.selectByExample(saleExample);
        return Flux.fromIterable(result);
    }

    @Override
    public Flux<Product> getAllPromotionalSaleItems() {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andOnSaleStatusEqualTo(1);
        List<Product> result = productMapper.selectByExample(productExample);
        return Flux.fromIterable(result);
    }

    @Override
    public Flux<Product> getAllFlashSaleItems() {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andOnSaleStatusEqualTo(2);
        List<Product> result = productMapper.selectByExample(productExample);
        return Flux.fromIterable(result);
    }

    @Override
    public void updateSaleLimitPrice() {
        // TODO: use it when receiving stock update from order event
        //      might remove from sale if it reached limit and send update event.
    }

    @Override
    public void updateSaleTimeFramePrice() {
        // TODO: could use @Scheduled  Spring Task to do so to check it every hour or so
    }

    private boolean checkItemOnSale(List<PromotionSaleProduct> onSaleProduct) {
        for (PromotionSaleProduct product : onSaleProduct) {
            String skuCode = product.getProductSkuCode();
            PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
            promotionSaleProductExample.createCriteria().andProductSkuCodeEqualTo(skuCode);
            List<PromotionSaleProduct> productList = promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

            if (!productList.isEmpty()) {
                LOG.info("Product sku %s on sale already, can not have multiple sale.", skuCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public Mono<PromotionSale> createListSale(OnSaleRequest request, String operator) {

        List<PromotionSaleProduct> promotionSaleProductList = request.getOnSaleProduct();
        boolean itemAlreadyOnSale = checkItemOnSale(promotionSaleProductList);

        if (itemAlreadyOnSale) return null;

        PromotionSale newPromotionSale = request.getPromotionSale();
        promotionSaleMapper.insert(newPromotionSale);

        int newSaleId = newPromotionSale.getId();
        int discountType = newPromotionSale.getDiscountType();
        int promotionType = newPromotionSale.getPromotionType();
        double discountAmount = newPromotionSale.getAmount().doubleValue();

        List<ProductSku> affectedSku = new ArrayList<>();

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : promotionSaleProductList) {

            int skuId = saleProduct.getId();

            ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
            double currentSkuSale = productSku.getPrice().doubleValue();
            if (discountType == 0) {// discount by amount
                currentSkuSale = currentSkuSale  - discountAmount;
            } else {   // discount  by percent
                currentSkuSale = currentSkuSale - (discountAmount *  currentSkuSale / 100);
            }

            productSku.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));

            saleProduct.setPromotionSaleId(newSaleId);
            saleProduct.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));

            productSkuMapper.updateByPrimaryKey(productSku);
            promotionSaleProductMapper.insert(saleProduct);

            int productId = saleProduct.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            double currentProductPrice = affectedProduct.getSalePrice().doubleValue();
            affectedProduct.setOnSaleStatus(1);
            affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentProductPrice, currentSkuSale)));
            productMapper.updateByPrimaryKey(affectedProduct);

            affectedSku.add(productSku);
        }

        createSaleLog(newSaleId, promotionType, discountType, discountAmount, operator);

        sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(newPromotionSale);
    }

    /*
    @Override
    public Mono<PromotionSale> createBrandSale(OnSaleRequest request, String operator) {

        List<PromotionSaleProduct> promotionSaleProductList = request.getOnSaleProduct();
        boolean itemAlreadyOnSale = checkItemOnSale(promotionSaleProductList);

        if (itemAlreadyOnSale) return null;

        PromotionSale newPromotionSale = request.getPromotionSale();
        promotionSaleMapper.insert(newPromotionSale);

        int newSaleId = newPromotionSale.getId();
        int discountType = newPromotionSale.getDiscountType();
        int promotionType = newPromotionSale.getPromotionType();
        double discountAmount = newPromotionSale.getAmount().doubleValue();

        List<ProductSku> affectedSku = new ArrayList<>();

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : promotionSaleProductList) {

            int skuId = saleProduct.getId();

            ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
            double currentSkuSale = productSku.getPromotionPrice().doubleValue();
            if (discountType == 0) {// discount by amount
                currentSkuSale = currentSkuSale  - discountAmount;
            } else {   // discount  by percent
                currentSkuSale = currentSkuSale - (discountAmount *  currentSkuSale / 100);
            }

            productSku.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));

            promotionSaleProductMapper.updateByPrimaryKey(saleProduct);
            productSkuMapper.updateByPrimaryKey(productSku);

            int productId = saleProduct.getProductId();
            // sale price and status to 1
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            double currentProductPrice = affectedProduct.getSalePrice().doubleValue();
            affectedProduct.setOnSaleStatus(1);
            affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentProductPrice, currentSkuSale)));
            productMapper.updateByPrimaryKey(affectedProduct);

            affectedSku.add(productSku);
        }

        createSaleLog(newSaleId, promotionType, discountType, discountAmount, operator);

        sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(newPromotionSale);
    }

    @Override
    public Mono<PromotionSale> createCategorySale(OnSaleRequest request , String operator) {
        List<PromotionSaleProduct> promotionSaleProductList = request.getOnSaleProduct();
        boolean itemAlreadyOnSale = checkItemOnSale(promotionSaleProductList);

        if (itemAlreadyOnSale) return null;

        PromotionSale newPromotionSale = request.getPromotionSale();
        promotionSaleMapper.insert(newPromotionSale);

        int newSaleId = newPromotionSale.getId();
        int discountType = newPromotionSale.getDiscountType();
        int promotionType = newPromotionSale.getPromotionType();
        double discountAmount = newPromotionSale.getAmount().doubleValue();

        List<ProductSku> affectedSku = new ArrayList<>();

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : promotionSaleProductList) {

            int skuId = saleProduct.getId();

            ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
            double currentSkuSale = productSku.getPromotionPrice().doubleValue();
            if (discountType == 0) {// discount by amount
                currentSkuSale = currentSkuSale  - discountAmount;
            } else {   // discount  by percent
                currentSkuSale = currentSkuSale - (discountAmount *  currentSkuSale / 100);
            }

            productSku.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));

            promotionSaleProductMapper.updateByPrimaryKey(saleProduct);
            productSkuMapper.updateByPrimaryKey(productSku);

            int productId = saleProduct.getProductId();
            // sale price and status to 1
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            double currentProductPrice = affectedProduct.getSalePrice().doubleValue();
            affectedProduct.setOnSaleStatus(1);
            affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentProductPrice, currentSkuSale)));
            productMapper.updateByPrimaryKey(affectedProduct);

            affectedSku.add(productSku);
        }

        createSaleLog(newSaleId, promotionType, discountType, discountAmount, operator);

        sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(newPromotionSale);
    }
     */

    private void createSaleLog(int newSaleId, int promotionType, int discountType, double discountAmount, String operator) {
        PromotionSaleLog newLog = new PromotionSaleLog();
        newLog.setPromotionSaleId(newSaleId);
        newLog.setPromotionType(promotionType);
        newLog.setDiscountType(discountType);
        newLog.setAmount(BigDecimal.valueOf(discountAmount));
        newLog.setOperateMan(operator);
        newLog.setCreatedAt(new Date());
        promotionSaleLogMapper.insert(newLog);
    }

    @Override
    public Mono<PromotionSale> updateSaleInfo(OnSaleRequest updateSaleRequest, String operator) {
        PromotionSale updatedPromotionSale = updateSaleRequest.getPromotionSale();
        updatedPromotionSale.setUpdatedAt(new Date());

        promotionSaleMapper.updateByPrimaryKey(updatedPromotionSale);

        // TODO:new logs
        return Mono.just(updatedPromotionSale);
    }

    @Override
    public Mono<PromotionSale> updateSalePrice(OnSaleRequest updateSaleRequest, String operator) {
        PromotionSale updatedSale = updateSaleRequest.getPromotionSale();
        promotionSaleMapper.insert(updatedSale);

        int saleId = updatedSale.getId();
        int discountType = updatedSale.getDiscountType();
        int promotionType = updatedSale.getPromotionType();
        double discountAmount = updatedSale.getAmount().doubleValue();

        List<ProductSku> affectedSku = new ArrayList<>();

        PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
        promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(saleId);
        List<PromotionSaleProduct> onsaleProdctList =  promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : onsaleProdctList) {

            int skuId = saleProduct.getId();

            ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
            double currentSkuSale = productSku.getPrice().doubleValue();
            if (discountType == 0) {// discount by amount
                currentSkuSale = currentSkuSale  - discountAmount;
            } else {   // discount  by percent
                currentSkuSale = currentSkuSale - (discountAmount *  currentSkuSale / 100);
            }

            productSku.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));
            saleProduct.setPromotionPrice(BigDecimal.valueOf(currentSkuSale));

            productSkuMapper.updateByPrimaryKey(productSku);
            promotionSaleProductMapper.updateByPrimaryKey(saleProduct);

            int productId = saleProduct.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            double currentProductPrice = affectedProduct.getSalePrice().doubleValue();
            affectedProduct.setOnSaleStatus(1);
            affectedProduct.setSalePrice(BigDecimal.valueOf(Math.min(currentProductPrice, currentSkuSale)));
            productMapper.updateByPrimaryKey(affectedProduct);

            affectedSku.add(productSku);
        }

        createSaleLog(saleId, promotionType, discountType, discountAmount, operator);

        sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(updatedSale);
    }

    @Override
    public Mono<PromotionSale> updateSaleStatus(OnSaleRequest updateSaleRequest , String operator) {
        int status = updateSaleRequest.getPromotionSale().getStatus();
        PromotionSale updatedSale = updateSaleRequest.getPromotionSale();

        if (status == 1) {
            updateSalePrice(updateSaleRequest, operator);
        } else {
            int updateSaleId = updatedSale.getId();
            promotionSaleMapper.insert(updatedSale);

            List<ProductSku> affectedSku = new ArrayList<>();

            PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
            promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(updateSaleId);
            List<PromotionSaleProduct> onsaleProdctList =  promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

            // find and update sku and product price and status.
            for (PromotionSaleProduct saleProduct : onsaleProdctList) {

                int skuId = saleProduct.getId();

                ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
                double originalPrice = productSku.getPrice().doubleValue();

                productSku.setPromotionPrice(BigDecimal.valueOf(originalPrice));
                saleProduct.setPromotionPrice(BigDecimal.valueOf(originalPrice));

                productSkuMapper.updateByPrimaryKey(productSku);
                promotionSaleProductMapper.updateByPrimaryKey(saleProduct);

                int productId = saleProduct.getProductId();
                Product affectedProduct = productMapper.selectByPrimaryKey(productId);
                double currentProductPrice = affectedProduct.getOriginalPrice().doubleValue();

                affectedProduct.setOnSaleStatus(0);
                affectedProduct.setSalePrice(BigDecimal.valueOf(currentProductPrice));

                productMapper.updateByPrimaryKey(affectedProduct);

                affectedSku.add(productSku);
            }

            int promotionType = updatedSale.getPromotionType();
            int discountType = updatedSale.getDiscountType();

            createSaleLog(updateSaleId, promotionType, discountType, 0, operator);

            sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
            sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
        }
        return Mono.just(updatedSale);
    }

    @Override
    public void delete(int promotionSaleId, String operator) {

        PromotionSale toBeDeletSale = promotionSaleMapper.selectByPrimaryKey(promotionSaleId);
        int promotionId = toBeDeletSale.getId();
        List<ProductSku> affectedSku = new ArrayList<>();

        PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
        promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(promotionId);
        List<PromotionSaleProduct> onsaleProdctList =  promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : onsaleProdctList) {

            int skuId = saleProduct.getId();

            ProductSku productSku = productSkuMapper.selectByPrimaryKey(skuId);
            double originalPrice = productSku.getPrice().doubleValue();

            productSku.setPromotionPrice(BigDecimal.valueOf(originalPrice));
            saleProduct.setPromotionPrice(BigDecimal.valueOf(originalPrice));

            productSkuMapper.updateByPrimaryKey(productSku);
            promotionSaleProductMapper.updateByPrimaryKey(saleProduct);

            int productId = saleProduct.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            double currentProductPrice = affectedProduct.getOriginalPrice().doubleValue();

            affectedProduct.setOnSaleStatus(0);
            affectedProduct.setSalePrice(BigDecimal.valueOf(currentProductPrice));

            productMapper.updateByPrimaryKey(affectedProduct);

            affectedSku.add(productSku);
        }

        int promotionType = toBeDeletSale.getPromotionType();
        int discountType = toBeDeletSale.getDiscountType();

        createSaleLog(promotionId, promotionType, discountType, 0, operator);

        sendOmsSaleUpdateMessage("saleupdatetoOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
        sendPmsSaleUpdateMessage("saleupdatetoPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
    }

    private void sendPmsSaleUpdateMessage(String bindingName, PmsSaleOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendOmsSaleUpdateMessage(String bindingName, OmsSaleOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
