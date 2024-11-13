package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.AddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class OrderParam implements Serializable {

    /**
     * item in shopping cart, sku code and quantity
     */
    private Map<String, Integer> orderProductSku;

    /**
     * order amount calculated from cart
     */
    private BigDecimal amount;

    /**
     * member deliver address
     */
    private AddressDTO address;

    /**
     * coupon for discount
     */
    private String coupon;

    /**
     * coupon discount amount calculated from cart
     */
    private BigDecimal discountAmount;

    /**
     * pay type g pay, or finance. currently just paypal
     */
    private int payType;
}
