package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent;
import com.itsthatjun.ecommerce.service.OMS.impl.OrderReturnServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.servlet.http.HttpSession;

import static com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/order/return")
@Api(tags = "return related", description = "apply return and related api")
public class OrderReturnController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnController.class);

    private final OrderReturnServiceImpl orderReturnService;

    @Autowired
    public OrderReturnController(OrderReturnServiceImpl orderReturnService) {
        this.orderReturnService = orderReturnService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "list all return request open waiting to be approved")
    public Flux<ReturnRequest> listAllReturnRequest(@RequestParam("statusCode") ReturnStatusCode statusCode) {
        return orderReturnService.listAllReturnRequest(statusCode);
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "return a return request detail")
    public Mono<ReturnRequest> getReturnRequest(@PathVariable String serialNumber) {
        return orderReturnService.getReturnRequest(serialNumber);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update the status of the return apply")
    public Mono<Void> updateReturnOrderStatus(@RequestBody ReturnRequestDecision returnRequestDecision, HttpSession session) {
        String operator = (String) session.getAttribute("adminName");
        return orderReturnService.updateReturnOrderStatus(returnRequestDecision, operator);
    }
}
