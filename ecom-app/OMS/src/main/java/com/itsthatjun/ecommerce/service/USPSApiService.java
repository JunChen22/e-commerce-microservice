package com.itsthatjun.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class USPSApiService {

    @Value("${usps.userid}")
    String userId;

    @Value("${usps.password}")
    String password;

    public String getShippingRate() {
        System.out.println("User id : " + userId);
        System.out.println("password: " + password);
        return userId + " : " + password;
    }

    public boolean checkDeliveryStatus(String tracking) {
        // TODO: add USPS API
        return true;
    }
}
