package com.itsthatjun.ecommerce.dto.OMS;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class OrderReturnApplyDecision {

    public enum Status {
        WAITING_TO_PROCESS,
        APPROVED, // RETURNING_ITEM_TRANSIT
        REJECTED,
        COMPLETED_RETURN
    }

    private final Status status;
    private final OrderReturnApply returnApply;
    private final String reason;

    @Autowired
    public OrderReturnApplyDecision(Status status, OrderReturnApply returnApply, String reason) {
        this.status = status;
        this.returnApply = returnApply;
        this.reason = reason;
    }
}
