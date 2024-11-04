package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    /**
     * list all brands
     * @return list all brands
     */
    Flux<BrandDTO> listAllBrand();

    /**
     * list all brands with pagination
     * @param pageNum page number
     * @param pageSize page size
     * @return list of brands
     */
    Flux<BrandDTO> listBrand(int pageNum, int pageSize);

    /**
     * list all products of a brand
     * @param brandId brand id
     * @return list of products
     */
    Flux<ProductDTO> getBrandProduct(int brandId);

    /**
     * get brand by id
     * @param brandId brand id
     * @return brand
     */
    Mono<BrandDTO> getBrand(int brandId);
}
