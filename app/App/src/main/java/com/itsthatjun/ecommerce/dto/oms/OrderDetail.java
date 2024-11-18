package com.itsthatjun.ecommerce.dto.oms;

import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import com.itsthatjun.ecommerce.dto.oms.model.OrderItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class OrderDetail implements Serializable {

    private OrderDTO order;

    /**
     * order item list
     */
    private List<OrderItemDTO> orderItemList;
}
