package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import reactor.core.publisher.Mono;

public interface UserService {

    /**
     * get user id from security context after authentication
     * @return int
     */
    int getUserId();

    Mono<MemberDetail> getInfo();

    Mono<MemberDetail> register(MemberDetail memberDetail);

    Mono<String> updatePassword(String newPassword);

    Mono<MemberDetail> updateInfo(MemberDetail memberDetail);

    Mono<AddressDTO> updateAddress(AddressDTO newAddress);

    Mono<Void> deleteAccount();
}
