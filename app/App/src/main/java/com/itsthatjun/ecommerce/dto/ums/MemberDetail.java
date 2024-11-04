package com.itsthatjun.ecommerce.dto.ums;

import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberIconDTO;
import com.itsthatjun.ecommerce.dto.ums.model.MemberLoginLogDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MemberDetail extends MemberDTO implements Serializable {

    /**
     * Member address
     */
    private AddressDTO address;

    /**
     * Member icon
     */
    private MemberIconDTO icon;

    /**
     * Login log list
     */
    private List<MemberLoginLogDTO> loginLogList;
}
