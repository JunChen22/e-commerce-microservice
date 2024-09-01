package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface UserService {

    @ApiOperation(value = "")
    Mono<MemberDetail> getInfo();

    @ApiOperation(value = "Register")
    Mono<MemberDetail> register(MemberDetail memberDetail);

    @ApiOperation(value = "")
    Mono<String> updatePassword(String newPassword);

    @ApiOperation(value = "password, name, icon")
    Mono<MemberDetail> updateInfo(MemberDetail memberDetail);

    @ApiOperation(value = "")
    Mono<AddressDTO> updateAddress(AddressDTO newAddress);

    @ApiOperation(value = "")
    Mono<Void> deleteAccount();
}
