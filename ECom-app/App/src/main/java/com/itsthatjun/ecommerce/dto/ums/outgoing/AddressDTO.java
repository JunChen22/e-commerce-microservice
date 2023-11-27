package com.itsthatjun.ecommerce.dto.ums.outgoing;

import lombok.Data;

@Data
public class AddressDTO {

    private String receiverName;

    private String phoneNumber;

    private String detailAddress;

    private String city;

    private String state;

    private String zipCode;
}
