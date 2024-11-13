package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import reactor.core.publisher.Flux;

public interface PromotionService {

    /**
     * List all sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount
     */
    //Flux<PromotionSale> listAllPromotionSale();

    /**
     * List all the item that is on regular sale discount
     */
    Flux<ProductDTO> listAllPromotionSaleItem();

    /**
     * List all item that is on short term sale like clearance or special sale
     */
    Flux<ProductDTO> listAllFlashSaleItem();
}
