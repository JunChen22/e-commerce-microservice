package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.OrderReturnApplyMapper;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApplyExample;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReason;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import com.itsthatjun.ecommerce.service.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private final OrderReturnApplyMapper returnApplyMapper;

    private final OrderReturnReason reasonMapper;

    private final OrderReturnReasonPictures picturesMapper;

    @Autowired
    public ReturnOrderServiceImpl(OrderReturnApplyMapper returnApplyMapper, OrderReturnReason reasonMapper, OrderReturnReasonPictures picturesMapper) {
        this.returnApplyMapper = returnApplyMapper;
        this.reasonMapper = reasonMapper;
        this.picturesMapper = picturesMapper;
    }

    @Override
    public OrderReturnApply getStatus(String orderSn, int userId) {

        OrderReturnApplyExample returnApplyExample = new OrderReturnApplyExample();
        returnApplyExample.createCriteria().andOrderSnEqualTo(orderSn).andMemberIdEqualTo(userId);
        OrderReturnApply returnApply = returnApplyMapper.selectByExample(returnApplyExample).get(0);

        return returnApply;
    }

    @Override
    public OrderReturnApply applyForReturn(OrderReturnApply apply, OrderReturnReason returnReason, List<OrderReturnReasonPictures> pictures,
                                           String orderSn, int userId) {

        apply.setOrderSn(orderSn);
        apply.setMemberId(userId);
        apply.setReason(returnReason.getReason());
        returnApplyMapper.insert(apply);
        return null;
    }

    @Override
    public OrderReturnApply updateReturn(OrderReturnApply apply, OrderReturnReason returnReason, List<OrderReturnReasonPictures> pictures,
                                         String orderSn, int userId) {
        return null;
    }

    @Override
    public OrderReturnApply cancelReturn(String orderSn, int userId) {
        return null;
    }
}
