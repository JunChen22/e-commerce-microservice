package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.mbg.model.Address;
import lombok.Data;

import java.util.List;

@Data
public class MemberDetail {
    Member member;
    Address address;
    MemberIcon icon;
    List<MemberLoginLog> loginLogList;
}
