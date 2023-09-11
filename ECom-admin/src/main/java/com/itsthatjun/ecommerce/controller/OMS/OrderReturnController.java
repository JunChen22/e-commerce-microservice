package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderReturnApplyDecision;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.service.OMS.implementation.ReturnOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class OrderReturnController {

    private final ReturnOrderServiceImpl returnOrderService;

    @Autowired
    public OrderReturnController(ReturnOrderServiceImpl returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    // return status,  waiting to process 0 , returning(sending) 1, complete 2, rejected(not matching reason) 3

    @GetMapping("/AllOpening")
    @ApiOperation(value = "list all return request open waiting to be approved")
    public List<OrderReturnApply> listAllOpening(){
        return returnOrderService.getAllOpening();
    }

    @GetMapping("/AllReturning")
    @ApiOperation(value = "List all returns that are on their way")
    public List<OrderReturnApply> listReturning(){
        return returnOrderService.getAllReturning();
    }

    @GetMapping("/AllCompleted")
    @ApiOperation(value = "List ALl completed returns")
    public List<OrderReturnApply> listAllCompleted(){
        return returnOrderService.getAllCompleted();
    }

    @GetMapping("/AllRejected")
    @ApiOperation(value = "List All rejected returns requests")
    public List<OrderReturnApply> listAllRejected(){
        return returnOrderService.getAllRejected();
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "return a return request detail")
    public OrderReturnApply getReturnRequest(@PathVariable String serialNumber){
        return returnOrderService.getOrderReturnDetail(serialNumber);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update the status of the return apply")
    public OrderReturnApplyDecision updateReturnOrderStatus(@RequestBody OrderReturnApplyDecision orderReturnApplyDecision, HttpSession session){
        OrderReturnApply returnApply = orderReturnApplyDecision.getReturnApply();

        String adminName = (String) session.getAttribute("adminName");

        orderReturnApplyDecision.getReturnApply().setHandleOperator(adminName);

        switch (orderReturnApplyDecision.getStatus()) {
            case APPROVED:
                returnOrderService.approveReturnRequest(returnApply);
                break;
            case REJECTED:
                String rejectionReason = orderReturnApplyDecision.getReason();
                returnOrderService.rejectReturnRequest(returnApply, rejectionReason);
                break;
            case COMPLETED_RETURN:
                returnOrderService.completeReturnRequest(returnApply);
                break;
            default:
                throw new IllegalArgumentException("Invalid status");
        }
        return orderReturnApplyDecision;
    }
}
