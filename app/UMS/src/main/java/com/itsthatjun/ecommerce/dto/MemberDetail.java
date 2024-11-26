package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import com.itsthatjun.ecommerce.dto.model.MemberIconDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class MemberDetail implements Serializable {

    private final MemberDTO member;

    /**
     * Member address
     */
    private final AddressDTO address;

    /**
     * Member icon
     */
    private final MemberIconDTO icon;
}
