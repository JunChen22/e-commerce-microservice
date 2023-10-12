package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Address;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface UserService {

    @ApiOperation(value = "")
    Mono<MemberDetail> getInfo(int userId);

    @ApiOperation(value = "Register")
    Mono<MemberDetail> register(MemberDetail memberDetail);

    @ApiOperation(value = "")
    Mono<String> updatePassword(String newPassword, int userId);

    @ApiOperation(value = "password, name, icon")
    Mono<MemberDetail> updateInfo(MemberDetail memberDetail, int userId);

    @ApiOperation(value = "")
    Mono<Address> updateAddress(Address newAddress, int userId);

    @ApiOperation(value = "")
    Mono<Void> deleteAccount(int userId);
}
