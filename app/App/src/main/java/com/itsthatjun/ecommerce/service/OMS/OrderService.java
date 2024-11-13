package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    /**
     * Get Order Detail by serial number
     *
     * @param orderSn the serial number of the order
     * @return the order detail, if found
     */
    Mono<OrderDetail> detail(String orderSn);

    /**
     * Get member order based on status code and page size
     *
     * @param status the status code of the order
     * @param pageNum the page number of the order
     * @param pageSize the size of the page
     * @return the order list, if found
     */
    Flux<OrderDTO> list(int status, Integer pageNum, Integer pageSize);

    /**
     * get payment link to make payment in different time
     *
     * @param orderSn the size of the page
     * @return the order list, if found
     */
    Mono<String> getPaymentLink(String orderSn);

    /**
     * Generate order based on shopping cart, actual transaction
     *
     * @param orderParam the order param to generate order
     * @param requestUrl the request url to generate order
     * @return the order param, if generated
     */
    Mono<OrderParam> generateOrder(OrderParam orderParam, String requestUrl);

    /**
     * after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks
     *
     * @param paymentId the payment id to process
     * @param payerId the payer id to process
     * @return the order list, if found
     */
    Mono<String> successPay(String paymentId, String payerId);

    /**
     * Cancel order if before sending the order out.
     *
     * @param orderSn the serial number of the order
     * @return the order list, if found
     */
    Mono<Void> cancelUserOrder(String orderSn);
}
