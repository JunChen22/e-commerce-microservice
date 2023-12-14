package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.config.MyBatisConfig;
import com.itsthatjun.ecommerce.dto.event.*;
import com.itsthatjun.ecommerce.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.Consumer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MyBatisConfig.class)
@ActiveProfiles("test")
public class NotificationApplicationTests extends TestContainerConfig {

    @Autowired
    @Qualifier("orderMessageProcessor")
    private Consumer<OmsOrderEvent> orderMessageProcessor;

    @Autowired
    @Qualifier("orderItemMessageProcessor")
    private Consumer<OmsOrderAnnouncementEvent> orderItemMessageProcessor;

    @Autowired
    @Qualifier("orderReturnMessageProcessor")
    private Consumer<OmsReturnEvent> orderReturnMessageProcessor;

    @Autowired
    @Qualifier("saleMessageProcessor")
    private Consumer<SmsEvent> saleMessageProcessor;

    @Autowired
    @Qualifier("userMessageProcessor")
    private Consumer<UmsEmailEvent> userMessageProcessor;

    @Autowired
    @Qualifier("adminMessageProcessor")
    private Consumer<UmsEmailEvent> adminMessageProcessor;

    @Autowired
    private EmailService emailService;

    @Test
    void contextLoads() {

    }
}
