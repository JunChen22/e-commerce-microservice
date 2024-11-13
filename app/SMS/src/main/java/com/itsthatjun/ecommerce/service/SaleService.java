package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.OnSale;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import reactor.core.publisher.Flux;

public interface SalesService {

    // 0-> not on sale; 1-> is on sale; 2-> flash sale/special sales/clarance/used item
    // promotional sale and flash sale is similar
    // promotion sale is the "normal" discount. flash sale for clearance/used
    @ApiOperation(value = "get all promotion list that is on sale")

    /**
     * get all sale that is happening.
     * could be regular sale or special sale
     * @return a Flux stream of OnSale objects
     */
    Flux<OnSale> getAllSale(); // TODO: add date interval, active status

    /**
     * get all promotion items on sale
     * promotional items are items that are on regular sale
     * @return a Flux stream of ProductDTO objects
     */
    Flux<ProductDTO> getAllPromotionalSaleItems();

    /**
     * get all flash sales items
     * flash sales are items that are on special sale like clearance, used items, etc.
     * @return a Flux stream of ProductDTO objects
     */
    Flux<ProductDTO> getAllFlashSaleItems();
}
