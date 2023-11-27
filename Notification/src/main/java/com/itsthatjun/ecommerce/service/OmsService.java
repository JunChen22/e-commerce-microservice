package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OmsService {

    private static final Logger LOG = LoggerFactory.getLogger(UmsService.class);

    private final String OMS_SERVICE_URL = "http://ums/user";

    private final RestTemplate restTemplate;

    @Autowired
    public OmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO: mTls
    public List<UserInfo> getUserInfo() {
        String url = OMS_SERVICE_URL + "/getAllUserInfo";
        List<UserInfo> userInfoList = restTemplate.exchange(url, HttpMethod.GET, null,  new ParameterizedTypeReference<List<UserInfo>>() {}).getBody();
        return userInfoList;
    }
}
