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
import java.util.concurrent.TimeUnit;

@Service
public class UmsService {

    private static final Logger LOG = LoggerFactory.getLogger(UmsService.class);

    private final String UMS_SERVICE_URL = "http://ums/user";

    //private final WebClient webClient; // TODO: might change it to reactive

    private final RestTemplate restTemplate;

    private final EmailService emailService;

    @Autowired
    public UmsService(RestTemplate restTemplate, EmailService emailService) {
        this.restTemplate = restTemplate;
        this.emailService = emailService;
    }

    public String sendUserMessage(UserInfo userInfo, String message) {
        emailService.sendEmail(userInfo.getEmail(), userInfo.getName(), message);
        return "message send";
    }

    public String sendAllUserMessage(String message) {
        List<UserInfo> userInfoList = getUserInfo();

        try {
            for (UserInfo userInfo : userInfoList) {
                emailService.sendEmail(userInfo.getEmail(), userInfo.getName(), message);
                TimeUnit.SECONDS.sleep(2);  // Adjust the delay based on your requirements
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "message send";
    }

    // TODO: mTls
    public List<UserInfo> getUserInfo() {
        String url = UMS_SERVICE_URL + "/getAllUserInfo";
        List<UserInfo> userInfoList = restTemplate.exchange(url, HttpMethod.GET, null,  new ParameterizedTypeReference<List<UserInfo>>() {}).getBody();
        return userInfoList;
    }
}
