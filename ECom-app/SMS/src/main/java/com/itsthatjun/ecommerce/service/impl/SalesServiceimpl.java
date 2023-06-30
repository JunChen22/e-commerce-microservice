package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesServiceimpl implements SalesService {

    private final PromotionSaleMapper promotionSaleMapper;

    private final PromotionSaleProductRelationMapper productRelationMapper;

    private final ProductSkuStockMapper productSkuStockMapper;

    private final PromotionSaleLogMapper promotionSaleLogMapper;

    private final ProductMapper productMapper;

    @Autowired
    public SalesServiceimpl(PromotionSaleMapper promotionSaleMapper, PromotionSaleProductRelationMapper productRelationMapper,
                            ProductSkuStockMapper productSkuStockMapper, PromotionSaleLogMapper promotionSaleLogMapper,
                            ProductMapper productMapper) {
        this.promotionSaleMapper = promotionSaleMapper;
        this.productRelationMapper = productRelationMapper;
        this.productSkuStockMapper = productSkuStockMapper;
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

}
