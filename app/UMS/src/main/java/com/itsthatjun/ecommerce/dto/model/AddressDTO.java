package com.itsthatjun.ecommerce.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddressDTO {

    private final UUID memberId;

    private final String receiverName;

    private final String phoneNumber;

    private final String detailAddress;

    private final String city;

    private final String state;

    private final String zipCode;

    private final String note;
}
