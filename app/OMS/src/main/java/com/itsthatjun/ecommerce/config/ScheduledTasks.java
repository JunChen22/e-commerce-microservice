package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.USPSApiService;
import com.itsthatjun.ecommerce.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

    private final OrderServiceImpl orderService;

    private final USPSApiService uspsApiService;

    private static final int BATCH_SIZE = 5;

    @Autowired
    public ScheduledTasks(OrderServiceImpl orderService, USPSApiService uspsApiService) {
        this.orderService = orderService;
        this.uspsApiService = uspsApiService;
    }

    @Scheduled(initialDelay = 2 * 60 * 1000, fixedRate = 15 * 60 * 1000) // Run every 15 minutes TODO: change it to Spring Batch or Quartz
    public void checkDeliveryStatus() throws Exception {
        System.out.println("Checking orders with UPS API...");

        List<Orders> ordersToCheck = orderService.getAllSendingOrder();

        // Process orders in batches
        for (int i = 0; i < ordersToCheck.size(); i += BATCH_SIZE) {
            List<Orders> batchOrders = ordersToCheck.subList(i, Math.min(i + BATCH_SIZE, ordersToCheck.size()));
            processBatch(batchOrders);
            // Add a delay between batches to avoid rate limits
            TimeUnit.SECONDS.sleep(2);  // Adjust the delay based on your requirements
        }
    }

    private void processBatch(List<Orders> orders) throws Exception {
        for (Orders order : orders) {
            // Call UPS API and update order status
            boolean isDelivered = uspsApiService.checkDeliveryStatus(order.getDeliveryTrackingNumber());
            if (isDelivered) {
                orderService.confirmReceiveOrder(order.getOrderSn());
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
