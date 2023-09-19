package com.itsthatjun.ecommerce.dto.ums;

import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.mbg.model.Review;
import lombok.Data;

import java.util.List;

@Data
public class MemberDetail {
    private Member member;
    private Address address;
    private List<Orders> ordersList;
    private List<Review> reviewList;
}
