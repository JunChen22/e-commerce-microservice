package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.mbg.mapper.MemberMapper;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberExample;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MemberServiceImpl implements UserDetailsService, MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberMapper memberMapper;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, JwtTokenUtil jwtTokenUtil) {
        this.memberMapper = memberMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = getMemberByUsername(username);
        if(member != null){
            return new CustomUserDetail(member);
        }
        //TODO: wasn't invoked when entering wrong username
        throw new UsernameNotFoundException("Username not found");
    }

    @Override
    public String login(String username, String password) {

        String token = "";
        try{
            CustomUserDetail userDetails = loadUserByUsername(username);
            System.out.println(userDetails.getMember());
            // decode password to compare
            if(!passwordEncoder().matches(password, userDetails.getPassword())){
                throw new BadCredentialsException("incorrect password");
            }

            // Authorities shouldn't be giving during validation
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, Collections.emptyList());   // provide empty list and will user userDetail's getAuthorities
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            // TODO: add a login error exception
            System.out.println("login error");
        }
        return token;
    }

    @Override
    public Member getMemberByUsername(String username) {
        MemberExample example = new MemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<Member> memberList = memberMapper.selectByExample(example);

        if(memberList != null && !memberList.isEmpty()){
            return memberList.get(0);
        }
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

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
