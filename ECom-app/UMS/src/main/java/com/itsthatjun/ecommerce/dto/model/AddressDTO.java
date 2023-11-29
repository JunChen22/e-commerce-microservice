package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AddressDTO implements Serializable {

    private String receiverName;

    private String phoneNumber;

    private String detailAddress;

    private String city;

    private String state;

    private String zipCode;

}
