package com.itsthatjun.ecommerce.dto.admin;

import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberIcon;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import lombok.Data;

import java.util.List;

@Data
public class AdminMemberDetail {
    private Member member;
    private Address address;
    private MemberIcon icon;
    private List<MemberLoginLog> loginLogList;
}
