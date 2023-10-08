package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UmsEventUpdateService {

    private final MemberMapper memberMapper;

    @Autowired
    public UmsEventUpdateService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @ApiOperation(value = "adds user to auth datase")
    public void addMember(Member member) {
        memberMapper.insert(member);
    };

    @ApiOperation(value = "update info like password and name, other info are irrelevant for auth")
    public void updateInfo(Member member) {
        memberMapper.updateByPrimaryKey(member);
    }

    @ApiOperation(value = "Update account status, status 0 can not login only 1 can.")
    public void updateStatus(Member member) {
        memberMapper.updateByPrimaryKey(member);
    }

    @ApiOperation(value = "delete member, remove from auth server, record kept in UMS")
    public void deleteMember(int userId) {
        Member existingMember = memberMapper.selectByPrimaryKey(userId);
        existingMember.setStatus(0);
        existingMember.setDeleteStatus(1);
        memberMapper.updateByPrimaryKey(existingMember);
    }
}
