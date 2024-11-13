package com.itsthatjun.ecommerce.dto.ums.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private String receiverName;

    private String phoneNumber;

    private String detailAddress;

    private String city;

    private String state;

    private String zipCode;
}
