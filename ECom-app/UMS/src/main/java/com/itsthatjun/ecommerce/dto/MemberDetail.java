package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.outgoing.AddressDTO;
import com.itsthatjun.ecommerce.dto.outgoing.MemberDTO;
import com.itsthatjun.ecommerce.dto.outgoing.MemberIconDTO;
import com.itsthatjun.ecommerce.dto.outgoing.MemberLoginLogDTO;
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
