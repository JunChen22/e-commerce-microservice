package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<MemberDetail> getInfo();

    Mono<MemberDetail> register(MemberDetail memberDetail);

    Mono<Void> updatePassword(String newPassword);

    Mono<MemberDetail> updateInfo(MemberDetail memberDetail);

    Mono<AddressDTO> updateAddress(AddressDTO newAddress);

    Mono<Void> deleteAccount();
}
