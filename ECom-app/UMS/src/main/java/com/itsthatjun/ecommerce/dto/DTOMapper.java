package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    AddressDTO addressToAddressDTO(Address address);

    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "note", ignore = true)
    Address addressDTOToAddress(AddressDTO addressDTO, Integer id);

    MemberDTO memberToMemberDTO(Member member);

    @Mapping(target = "verifiedStatus", ignore = true)
    @Mapping(target = "deleteStatus", ignore = true)
    @Mapping(target = "sourceType", ignore = true)
    Member memberDTOToMember(MemberDTO memberDTO, Integer id);

    Member memberDetailToMember(MemberDetail memberDetail);
}
