package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements UserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public String login(String username, String password) {
        return null;
    }

    @Override
    public Member getMemberByUsername(String username) {
        return null;
    }

    @Override
    public String addMember(Member member) {
        return null;
    }

    @Override
    public String updateInfo(Member member) {
        return null;
    }

    @Override
    public String updateStatus(Member member) {
        return null;
    }

    @Override
    public void deleteMember(int userId) {

    }
}
