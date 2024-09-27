package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.AddressDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

@Data
@EqualsAndHashCode
public class OrderParam {

    @ApiModelProperty("item in shopping cart, sku code and quantity")
    private Map<String, Integer> orderProductSku;

    @ApiModelProperty("order amount calculated from cart")
    private BigDecimal amount;

    @ApiModelProperty("Member deliver address")
    private AddressDTO address;

    @ApiModelProperty("coupon for discount")
    private String coupon;

    @ApiModelProperty("coupon discount amount calculated from cart")
    private BigDecimal discountAmount;

    @ApiModelProperty("paypal, g pay, or finance. currently just paypal")
    private int payType;
}
