package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.service.EmailService;
import com.itsthatjun.ecommerce.service.NotificationService;
import com.itsthatjun.ecommerce.service.UmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nof")
public class controller {

    private final EmailService emailService;

    private final UmsService umsService;

    private final NotificationService notificationService;

    @Autowired
    public controller(EmailService emailService, UmsService umsService, NotificationService notificationService) {
        this.emailService = emailService;
        this.umsService = umsService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<UserInfo> getUserInfo() {
        List<UserInfo> userInfoList = umsService.getUserInfo();

        for (UserInfo userInfo : userInfoList) {
            try {
                System.out.println(notificationService.generateUserNotification(userInfo.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error");
            }
        }
        return userInfoList;
    }

    @PostMapping
    public String sendEmail() {
        String to = "appacct10001@gmail.com";
        String subject = "test subject";
        String text = "hello there";
        emailService.sendEmail(to, subject, text);
        return "done";
    }
}