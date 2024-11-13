package com.itsthatjun.ecommerce.dto.sms;

import com.itsthatjun.ecommerce.dto.pms.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OnSaleItem {

    private ProductDTO product;

    private BrandDTO brand;

    private BigDecimal discountAmount;

    private int numberAvailable;

    private Date timeStart;

    private Date timeEnd;
}
