package com.itsthatjun.ecommerce.DTO;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class OrderParam {

    @ApiModelProperty("item in shopping cart")
    private List<CartItem> cartItemList;

    @ApiModelProperty("Member deliver address")
    private Address address;

    @ApiModelProperty("coupon for discount")
    private String coupon;

    //@ApiModelProperty("paypal, g pay, or finance. currently just paypal")
    //private Integer payType;
}
