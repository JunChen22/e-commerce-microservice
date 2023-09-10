package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.ApiOperation;

public interface MemberService {

    @ApiOperation(value = "")
    Member getUserDetail(String username);

    @ApiOperation(value = "")
    Member deactivateMember(String username);

    @ApiOperation(value = "")
    Member activateMember(String username);

}
