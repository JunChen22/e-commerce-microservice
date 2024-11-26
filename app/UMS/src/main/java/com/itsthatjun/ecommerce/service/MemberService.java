package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.dto.model.MemberDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MemberService {

    /**
     * check if the email/username is already taken
     * @param email
     * @return
     */
    Mono<Boolean> checkEmail(String email);

    Mono<MemberDetail> getInfo();

    Mono<MemberDetail> register(MemberDetail memberDetail);

    Mono<MemberDTO> updatePassword(MemberDTO member, UUID memberId);

    Mono<MemberDTO> updateInfo(MemberDTO member, UUID memberId);

    Mono<AddressDTO> updateAddress(AddressDTO newAddress, UUID memberId);

    /**
     * soft delete account
     * @param memberId
     * @return
     */
    Mono<Void> deleteAccount(UUID memberId);
}
