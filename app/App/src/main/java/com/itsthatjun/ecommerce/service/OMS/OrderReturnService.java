package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnParam;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import reactor.core.publisher.Mono;

public interface OrderReturnService {

    /**
     * check status of the return request
     *
     * @param orderSn the serial number of the order
     * @return the return detail, if found
     */
    Mono<ReturnDetail> checkStatus(String orderSn);

    /**
     * Apply for return item, waiting for admin approve
     *
     * @param returnParam the return param to apply
     * @return the return param, if applied
     */
    Mono<ReturnParam> applyForReturn(ReturnParam returnParam);

    /**
     * change detail about return or return reason
     *
     * @param returnParam the return param to update
     * @return the return param, if updated
     */
    Mono<ReturnParam> updateReturn(ReturnParam returnParam);

    /**
     * change detail about return or return reason
     *
     * @param orderSn the serial number of the order
     * @return the return list, if found
     */
    Mono<Void> cancelReturn(String orderSn);
}
