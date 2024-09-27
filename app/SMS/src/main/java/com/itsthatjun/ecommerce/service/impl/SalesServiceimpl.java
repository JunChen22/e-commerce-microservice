package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.PromotionSaleDao;
import com.itsthatjun.ecommerce.dto.OnSale;
import com.itsthatjun.ecommerce.dto.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.event.outgoing.OmsSaleOutEvent;
import com.itsthatjun.ecommerce.dto.event.outgoing.PmsSaleOutEvent;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.SalesService;
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
public class SalesServiceimpl implements SalesService {

    private static final Logger LOG = LoggerFactory.getLogger(SalesServiceimpl.class);

    private final PromotionSaleMapper promotionSaleMapper;

    private final PromotionSaleProductMapper promotionSaleProductMapper;

    private final PromotionSaleLogMapper promotionSaleLogMapper;

    private final ProductMapper productMapper;

    private final ProductSkuMapper productSkuMapper;

    private final StreamBridge streamBridge;

    private final Scheduler jdbcScheduler;

    private final PromotionSaleDao saleDao;

    @Autowired
    public SalesServiceimpl(PromotionSaleMapper promotionSaleMapper, PromotionSaleProductMapper promotionSaleProductMapper,
                            PromotionSaleLogMapper promotionSaleLogMapper, ProductMapper productMapper, ProductSkuMapper productSkuMapper,
                            StreamBridge streamBridge, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler, PromotionSaleDao saleDao) {
        this.promotionSaleMapper = promotionSaleMapper;
        this.promotionSaleProductMapper = promotionSaleProductMapper;
        this.promotionSaleLogMapper = promotionSaleLogMapper;
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
        this.streamBridge = streamBridge;
        this.jdbcScheduler = jdbcScheduler;
        this.saleDao = saleDao;
    }

    @Override
    public Flux<OnSale> getAllPromotionalSale() {
        return Mono.fromCallable(() -> {
            PromotionSaleExample saleExample = new PromotionSaleExample();
            saleExample.createCriteria().andStatusEqualTo(1);  // default active sales TODO: reduce returning infos
            List<PromotionSale> promotionSaleList = promotionSaleMapper.selectByExample(saleExample);

            List<OnSale> result = new ArrayList<>();
            for (PromotionSale promotionSale : promotionSaleList) {
                OnSale onSale = new OnSale();
                onSale.setName(promotionSale.getName());
                onSale.setStartTime(promotionSale.getStartTime());
                onSale.setEndTime(promotionSale.getEndTime());
                result.add(onSale);
            }
            return result;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> getAllPromotionalSaleItems() {
        return Mono.fromCallable(() -> {
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andOnSaleStatusEqualTo(1);
            List<Product> result = productMapper.selectByExample(productExample);
            return result;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<Product> getAllFlashSaleItems() {
        return Mono.fromCallable(() -> {
            ProductExample productExample = new ProductExample();
            productExample.createCriteria().andOnSaleStatusEqualTo(2);
            List<Product> result = productMapper.selectByExample(productExample);
            return result;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
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
        return Mono.fromCallable(() -> {
            PromotionSale newSale = internalCreateListSale(request, operator);
            return newSale;
        }).subscribeOn(jdbcScheduler);
    }

    private PromotionSale internalCreateListSale(OnSaleRequest request, String operator) {
        List<PromotionSaleProduct> promotionSaleProductList = request.getOnSaleProduct();
        boolean itemAlreadyOnSale = checkItemOnSale(promotionSaleProductList);

        if (itemAlreadyOnSale) return null;

        PromotionSale newPromotionSale = request.getPromotionSale();
        promotionSaleMapper.insert(newPromotionSale);

        int newSaleId = newPromotionSale.getId();
        int discountType = newPromotionSale.getDiscountType();
        BigDecimal discountAmount = newPromotionSale.getAmount();
        List<ProductSku> affectedSku = new ArrayList<>();

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : promotionSaleProductList) {
            String skuCode = saleProduct.getProductSkuCode();
            ProductSkuExample skuExample= new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(skuCode);

            List<ProductSku> productSkuList = productSkuMapper.selectByExample(skuExample);
            if (productSkuList.isEmpty()) {
                throw new RuntimeException("Can not find sku code : " + skuCode);
            }

            ProductSku productSku = productSkuList.get(0);
            BigDecimal currentSkuSale = productSku.getPrice();

            currentSkuSale = calculateSkuSale(currentSkuSale, discountType, discountAmount);

            productSku.setPromotionPrice(currentSkuSale);

            saleProduct.setPromotionSaleId(newSaleId);
            saleProduct.setPromotionPrice(currentSkuSale);

            productSkuMapper.updateByPrimaryKeySelective(productSku);
            promotionSaleProductMapper.insert(saleProduct);

            // update product sale status
            int productId = saleProduct.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);

            BigDecimal currentProductPrice = affectedProduct.getSalePrice();
            affectedProduct.setOnSaleStatus(1);

            // Update product sale price if the new SKU price is lower
            affectedProduct.setSalePrice(currentProductPrice.min(currentSkuSale));
            productMapper.updateByPrimaryKeySelective(affectedProduct);

            affectedSku.add(productSku);
        }

        createUpdateLog(newPromotionSale, "create sale", operator);

        sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return newPromotionSale;
    }

    private BigDecimal calculateSkuSale(BigDecimal currentSkuSale, int discountType, BigDecimal discountAmount) {
        if (discountType == 0) {    // Discount by amount
            return currentSkuSale.subtract(discountAmount);
        } else {   // Discount by percentage
            BigDecimal percentage = discountAmount.divide(BigDecimal.valueOf(100));  // Convert percentage to decimal
            BigDecimal discount = currentSkuSale.multiply(percentage);  // Multiply current price by percentage
            return currentSkuSale.subtract(discount);  // Subtract discount from the current price
        }
    }

    @Override
    public Mono<PromotionSale> createBrandSale(OnSaleRequest request, String operator) {
        /* TODO: create brand sale
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

        sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(newPromotionSale);
         */
        return Mono.empty();
    }

    @Override
    public Mono<PromotionSale> createCategorySale(OnSaleRequest request , String operator) {
        /* TODO: create category sale
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

        sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return Mono.just(newPromotionSale);
        */
        return Mono.empty();
    }

    @Override
    public Mono<PromotionSale> updateSaleInfo(OnSaleRequest updateSaleRequest, String operator) {
        return Mono.fromCallable(() -> {
            PromotionSale updatedPromotionSale = updateSaleRequest.getPromotionSale();
            updatedPromotionSale.setUpdatedAt(new Date());
            promotionSaleMapper.updateByPrimaryKeySelective(updatedPromotionSale);

            List<PromotionSaleProduct> productList = updateSaleRequest.getOnSaleProduct();
            for (PromotionSaleProduct promotionSaleProduct : productList) {
                promotionSaleProductMapper.updateByPrimaryKeySelective(promotionSaleProduct);
            }

            createUpdateLog(updatedPromotionSale, "update sale information", operator);
            return updatedPromotionSale;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<PromotionSale> updateSalePrice(OnSaleRequest updateSaleRequest, String operator) {
        return Mono.fromCallable(() ->
            internalUpdateSalePrice(updateSaleRequest, operator)
        ).subscribeOn(jdbcScheduler);
    }

    private PromotionSale internalUpdateSalePrice(OnSaleRequest updateSaleRequest, String operator) {
        PromotionSale updatedSale = updateSaleRequest.getPromotionSale();
        updatedSale.setUpdatedAt(new Date());
        promotionSaleMapper.updateByPrimaryKeySelective(updatedSale);

        int saleId = updatedSale.getId();
        int discountType = updatedSale.getDiscountType();
        BigDecimal discountAmount = updatedSale.getAmount();

        List<ProductSku> affectedSku = new ArrayList<>();

        PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
        promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(saleId);
        List<PromotionSaleProduct> onsaleProdctList = promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

        // find and update sku and product price and status.
        for (PromotionSaleProduct saleProduct : onsaleProdctList) {
            String skuCode = saleProduct.getProductSkuCode();
            ProductSkuExample skuExample= new ProductSkuExample();
            skuExample.createCriteria().andSkuCodeEqualTo(skuCode);

            List<ProductSku> productSkuList = productSkuMapper.selectByExample(skuExample);
            if (productSkuList.isEmpty()) throw new RuntimeException("Can not find sku code : " + skuCode);

            ProductSku productSku = productSkuList.get(0);
            BigDecimal currentSkuSale = productSku.getPrice();

            currentSkuSale = calculateSkuSale(currentSkuSale, discountType, discountAmount);

            productSku.setPromotionPrice(currentSkuSale);
            saleProduct.setPromotionPrice(currentSkuSale);

            productSkuMapper.updateByPrimaryKeySelective(productSku);
            promotionSaleProductMapper.updateByPrimaryKeySelective(saleProduct);

            int productId = saleProduct.getProductId();
            Product affectedProduct = productMapper.selectByPrimaryKey(productId);
            affectedProduct.setSalePrice(currentSkuSale);
            productMapper.updateByPrimaryKeySelective(affectedProduct);

            affectedSku.add(productSku);
        }

        createUpdateLog(updatedSale, "update sale price", operator);

        sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.UPDATE_SALE_PRICE, affectedSku));
        return updatedSale;
    }

    @Override
    public Mono<PromotionSale> updateSaleStatus(OnSaleRequest updateSaleRequest , String operator) {
        return Mono.fromCallable(() -> {
            int status = updateSaleRequest.getPromotionSale().getStatus();
            PromotionSale updatedSale = updateSaleRequest.getPromotionSale();

            if (status == 1) {
                internalUpdateSalePrice(updateSaleRequest, operator);
            } else {
                int updateSaleId = updatedSale.getId();
                promotionSaleMapper.updateByPrimaryKeySelective(updatedSale);

                List<ProductSku> affectedSku = new ArrayList<>();

                PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
                promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(updateSaleId);
                List<PromotionSaleProduct> onsaleProdctList =  promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

                // find and update sku and product price and status.
                for (PromotionSaleProduct saleProduct : onsaleProdctList) {
                    String skuCode = saleProduct.getProductSkuCode();
                    ProductSkuExample skuExample= new ProductSkuExample();
                    skuExample.createCriteria().andSkuCodeEqualTo(skuCode);

                    List<ProductSku> productSkuList = productSkuMapper.selectByExample(skuExample);
                    if (productSkuList.isEmpty()) throw new RuntimeException("Can not find sku code : " + skuCode);

                    ProductSku productSku = productSkuList.get(0);
                    BigDecimal originalPrice = productSku.getPrice();

                    productSku.setPromotionPrice(originalPrice);
                    saleProduct.setPromotionPrice(originalPrice);

                    productSkuMapper.updateByPrimaryKeySelective(productSku);
                    promotionSaleProductMapper.updateByPrimaryKeySelective(saleProduct);

                    int productId = saleProduct.getProductId();
                    Product affectedProduct = productMapper.selectByPrimaryKey(productId);
                    BigDecimal currentProductPrice = affectedProduct.getOriginalPrice();

                    affectedProduct.setOnSaleStatus(0);
                    affectedProduct.setSalePrice(currentProductPrice);

                    productMapper.updateByPrimaryKey(affectedProduct);

                    affectedSku.add(productSku);
                }

                createUpdateLog(updatedSale,"update sale status", operator);

                sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
                sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
            }
            return updatedSale;
        }).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> delete(int promotionSaleId, String operator) {
        return Mono.fromRunnable(() -> {
            PromotionSale toBeDeleteSale = promotionSaleMapper.selectByPrimaryKey(promotionSaleId);
            promotionSaleMapper.deleteByPrimaryKey(promotionSaleId);

            List<ProductSku> affectedSku = new ArrayList<>();

            PromotionSaleProductExample promotionSaleProductExample = new PromotionSaleProductExample();
            promotionSaleProductExample.createCriteria().andPromotionSaleIdEqualTo(promotionSaleId);
            List<PromotionSaleProduct> onsaleProdctList =  promotionSaleProductMapper.selectByExample(promotionSaleProductExample);

            // find and update sku and product price and status.
            for (PromotionSaleProduct saleProduct : onsaleProdctList) {
                String skuCode = saleProduct.getProductSkuCode();
                ProductSkuExample skuExample= new ProductSkuExample();
                skuExample.createCriteria().andSkuCodeEqualTo(skuCode);

                List<ProductSku> productSkuList = productSkuMapper.selectByExample(skuExample);
                if (productSkuList.isEmpty()) throw new RuntimeException("Can not find sku code : " + skuCode);

                ProductSku productSku = productSkuList.get(0);
                BigDecimal originalPrice = productSku.getPrice();

                productSku.setPromotionPrice(originalPrice);

                int productId = saleProduct.getProductId();

                productSkuMapper.updateByPrimaryKeySelective(productSku);
                promotionSaleProductMapper.deleteByPrimaryKey(saleProduct.getId());

                Product affectedProduct = productMapper.selectByPrimaryKey(productId);
                affectedProduct.setOnSaleStatus(0);
                BigDecimal currentProductPrice = affectedProduct.getOriginalPrice();

                affectedProduct.setSalePrice(currentProductPrice);

                productMapper.updateByPrimaryKey(affectedProduct);

                affectedSku.add(productSku);
            }
            createUpdateLog(toBeDeleteSale, "delete sale", operator);
            sendOmsSaleUpdateMessage("saleUpdateToOMS-out-0", new OmsSaleOutEvent(OmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
            sendPmsSaleUpdateMessage("saleUpdateToPms-out-0", new PmsSaleOutEvent(PmsSaleOutEvent.Type.DELETE_SALE, affectedSku));
        }).subscribeOn(jdbcScheduler).then();
    }

    private void createUpdateLog(PromotionSale sale, String updateAction, String operator) {
        PromotionSaleLog updateLog = new PromotionSaleLog();
        updateLog.setPromotionSaleId(sale.getId());
        updateLog.setSaleAction(updateAction);
        updateLog.setPromotionType(sale.getPromotionType());
        updateLog.setDiscountType(sale.getDiscountType());
        updateLog.setAmount(sale.getAmount());
        updateLog.setOperator(operator);
        promotionSaleLogMapper.insert(updateLog);
    }

    private void sendPmsSaleUpdateMessage(String bindingName, PmsSaleOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendOmsSaleUpdateMessage(String bindingName, OmsSaleOutEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType() , bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
