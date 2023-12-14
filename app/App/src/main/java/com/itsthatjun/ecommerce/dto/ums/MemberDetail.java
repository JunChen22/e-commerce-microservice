package com.itsthatjun.ecommerce.dto.ums;

import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberIconDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberLoginLogDTO;
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
