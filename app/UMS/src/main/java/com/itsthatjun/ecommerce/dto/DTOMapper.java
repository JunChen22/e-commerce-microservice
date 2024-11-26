package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import com.itsthatjun.ecommerce.dto.model.MemberIconDTO;
import com.itsthatjun.ecommerce.model.entity.Address;
import com.itsthatjun.ecommerce.model.entity.Member;
import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import com.itsthatjun.ecommerce.model.entity.MemberIcon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    // entity to dto

    MemberDTO memberToMemberDTO(Member member);

    AddressDTO addressToAddressDTO(Address address);

    MemberIconDTO memberIconToMemberIconDTO(MemberIcon memberIcon);


    // dto to entity
    Address addressDTOToAddress(AddressDTO addressDTO);

    Member memberDTOToMember(MemberDTO memberDTO);

    MemberIcon memberIconDTOToMemberIcon(MemberIconDTO memberIconDTO);

    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "note", ignore = true)
    Address addressDTOToAddress(AddressDTO addressDTO, Integer id);

    @Mapping(target = "verifiedStatus", ignore = true)
    Member memberDTOToMember(MemberDTO memberDTO, Integer id);

    @Mapping(target = "id", ignore = true)
    Member memberDetailToMember(MemberDetail memberDetail);


    // other mapping
}
