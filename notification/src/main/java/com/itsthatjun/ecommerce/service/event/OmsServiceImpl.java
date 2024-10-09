package com.itsthatjun.ecommerce.service.event;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.service.impl.EmailService;
import com.itsthatjun.ecommerce.service.impl.TemplateServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OmsServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(OmsServiceImpl.class);

    private final TemplateServiceImpl templateService;

    private final EmailService emailService;

    private final WebClient webClient;

    private final String OMS_SERVICE_URL = "http://oms/order";

    @Autowired
    public OmsServiceImpl(TemplateServiceImpl templateService, EmailService emailService, WebClient webClient) {
        this.templateService = templateService;
        this.emailService = emailService;
        this.webClient = webClient;
    }

    public String newOrderMessage(OrderDetail orderDetail) {
//        String email = orderDetail.getMemberEmail();
//        String orderSn = orderDetail.getOrderSn();
//        String name = orderDetail.getReceiverName();
//        String trackingNumber = orderDetail.getDeliveryTrackingNumber();
//        String orderAmount = String.valueOf(orderDetail.getPayAmount());
//        String status = orderDetail.getStatus().toString();  // TODO: add enum to change it back to string.
//
//        String template = templateService.getTemplate("order_service"); // TODO: use the template feature
//        template = template.replace("{order_number}", orderSn);
//        //template = template.replace("{name}", name);
//        //template = template.replace("{trackingNumber}", trackingNumber);
//        //template = template.replace("{orderAmount}", orderAmount);
//        //template = template.replace("{status}", status);
//
//        emailService.sendEmail(email, "new order", template);
        return "message send";
    }

    public String updateOrderMessage(OrderDetail orderDetail) {
//        String email = orderDetail.getMemberEmail();
//        String orderSn = orderDetail.getOrderSn();
//        String name = orderDetail.getReceiverName();
//        String trackingNumber = orderDetail.getDeliveryTrackingNumber();
//        String status = orderDetail.getStatus().toString();  // TODO: add enum to change it back to string.
//
//        String template = templateService.getTemplate("order_service"); // TODO: use the template feature
//        template = template.replace("{order_number}", orderSn);
//        //template = template.replace("{name}", name);
//        //template = template.replace("{trackingNumber}", trackingNumber);
//        template = template.replace("{status}", status);
//
//        emailService.sendEmail(email, "status update", template);
        return "message send";
    }

    // send message to all user that are affected/ordered the product. E.g defect product recall, not enough stock/inventory error and etc
    public String sendAllItemMessage(String productSku, String message) {
        List<OrderDetail> userInfoList = getAllPurchasedItemUserInfo(productSku);

        try {
            for (OrderDetail orderDetail : userInfoList) {
                emailService.sendEmail(orderDetail.getMemberEmail(), orderDetail.getReceiverName(), message);
                TimeUnit.SECONDS.sleep(2);  // Adjust the delay based on your requirements
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "message send";
    }

    public List<OrderDetail> getAllPurchasedItemUserInfo(String productSku) {
//        String url = OMS_SERVICE_URL + "/getUserPurchasedItem?productSku=" + productSku ;
//        List<OrderDetail> orderDetailList = restTemplate.exchange(url, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<OrderDetail>>() {}).getBody();
//        return orderDetailList;
        return null;
    }

    public String newReturnMessage(ReturnDetail returnDetail, UserInfo userInfo) {
//        String email = userInfo.getEmail();
//        String name = userInfo.getName();
//        String orderSn = returnDetail.getOrderSn();
//        String status = returnDetail.getStatus().toString();  // TODO: add enum to change it back to string.
//
//        String template = templateService.getTemplate("order_service_return");
//        template = template.replace("{order_number}", orderSn);
//        template = template.replace("{status}", status);
//
//        emailService.sendEmail(email, "new return request", template);
        return "message send";
    }

    public String updateReturnMessage(ReturnDetail returnDetail, UserInfo userInfo) {
//        String email = userInfo.getEmail();
//        String name = userInfo.getName();
//        String orderSn = returnDetail.getOrderSn();
//        String status = returnDetail.getStatus().toString();  // TODO: add enum to change it back to string.
//
//        String template = templateService.getTemplate("order_service_return_update");
//        template = template.replace("{order_number}", orderSn);
//        template = template.replace("{status}", status);
//
//        emailService.sendEmail(email, "return request updated", template);
        return "message send";
    }
}
