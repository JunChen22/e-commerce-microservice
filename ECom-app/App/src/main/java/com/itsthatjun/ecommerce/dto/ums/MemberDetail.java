package com.itsthatjun.ecommerce.dto.ums;

import com.itsthatjun.ecommerce.dto.ums.outgoing.AddressDTO;
import com.itsthatjun.ecommerce.dto.ums.outgoing.MemberDTO;
import com.itsthatjun.ecommerce.dto.ums.outgoing.MemberIconDTO;
import com.itsthatjun.ecommerce.dto.ums.outgoing.MemberLoginLogDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MemberDetail extends MemberDTO {

    @ApiModelProperty("")
    private AddressDTO address;

    @ApiModelProperty("")
    private MemberIconDTO icon;

    @ApiModelProperty("")
    private List<MemberLoginLogDTO> loginLogList;
}
