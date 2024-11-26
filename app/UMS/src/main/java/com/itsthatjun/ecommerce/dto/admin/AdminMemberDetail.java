package com.itsthatjun.ecommerce.dto.admin;

import com.itsthatjun.ecommerce.model.entity.Address;
import com.itsthatjun.ecommerce.model.entity.Member;
import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import com.itsthatjun.ecommerce.model.entity.MemberIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminMemberDetail implements Serializable {

    private Member member;

    private Address address;

    private MemberIcon icon;

    private List<MemberActivityLog> loginLogList;
}
