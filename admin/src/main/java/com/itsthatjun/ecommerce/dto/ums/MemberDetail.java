package com.itsthatjun.ecommerce.dto.ums;

import com.itsthatjun.ecommerce.mbg.model.*;
import lombok.Data;

import java.util.List;

@Data
public class MemberDetail {
    Member member;
    Address address;
    List<MemberLoginLog> loginLogList;
}
