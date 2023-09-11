package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.OrderReturnApplyMapper;
import com.itsthatjun.ecommerce.mbg.mapper.OrderReturnReasonPicturesMapper;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApplyExample;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.ReturnOrderService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

    private final OrderReturnApplyMapper returnApplyMapper;
    private final OrderReturnReasonPicturesMapper picturesMapper;

    private final StreamBridge streamBridge;

    @Autowired
    public ReturnOrderServiceImpl(OrderReturnApplyMapper returnApplyMapper, OrderReturnReasonPicturesMapper picturesMapper, StreamBridge streamBridge) {
        this.returnApplyMapper = returnApplyMapper;
        this.picturesMapper = picturesMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public OrderReturnApply getStatus(String orderSn, int userId) {

        OrderReturnApplyExample returnApplyExample = new OrderReturnApplyExample();
        returnApplyExample.createCriteria().andOrderSnEqualTo(orderSn).andMemberIdEqualTo(userId);
        OrderReturnApply returnApply = returnApplyMapper.selectByExample(returnApplyExample).get(0);

        return returnApply;
    }

    @Override
    public OrderReturnApply applyForReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures,
                                           String orderSn, int userId) {
        apply.setOrderSn(orderSn);
        apply.setCreatedAt(new Date());
        apply.setMemberId(userId);
        returnApplyMapper.insert(apply);
        return null;
    }

    @Override
    public OrderReturnApply updateReturn(OrderReturnApply apply, List<OrderReturnReasonPictures> pictures, String orderSn, int userId) {

        return null;
    }

    @Override
    public OrderReturnApply cancelReturn(String orderSn, int userId) {


        return null;
    }
}
