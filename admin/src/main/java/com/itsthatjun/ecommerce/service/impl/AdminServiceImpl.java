package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.AdminDao;
import com.itsthatjun.ecommerce.dao.domainmodel.AdminDetail;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.mbg.mapper.AdminMapper;
import com.itsthatjun.ecommerce.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements ReactiveUserDetailsService, AdminService {

    private final AdminMapper adminMapper;

    private final AdminDao adminDao;

    private final PasswordEncoder passwordEncoder;

    private final Scheduler publishEventScheduler;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, AdminDao adminDao, PasswordEncoder passwordEncoder,
                            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.adminMapper = adminMapper;
        this.adminDao = adminDao;
        this.passwordEncoder = passwordEncoder;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Mono<String> register(Admin newAdmin) {
        newAdmin.setStatus(1);
        AdminExample example = new AdminExample();
        example.createCriteria().andUsernameEqualTo(newAdmin.getUsername());
        List<Admin> existing = adminMapper.selectByExample(example);

        //TODO: add role and permission, maybe in here or a separate controller/dao
        if (!existing.isEmpty()) {
            System.out.println("existing account");
            return null; // TODO: make exception for existing account
        }
        newAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));
        adminMapper.insert(newAdmin);
        return Mono.just("Admin successfully added");
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException{
        AdminExample example = new AdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<Admin> AdminList = adminMapper.selectByExample(example);

        if (AdminList.isEmpty()) {
            return Mono.error(new UsernameNotFoundException("Username not found"));
        }

        Admin admin = AdminList.get(0);
        List<Permission> permissions = adminDao.getPermissionList(admin.getId());
        List<Roles> roles = adminDao.getRolesList(admin.getId());

        return Mono.just(new CustomUserDetail(admin, roles, permissions));
    }

    @Override
    public Mono<AdminDetail> getAdminDetail(int id) {
        return Mono.fromCallable(() -> adminDao.getAdminDetail(id))
                .subscribeOn(publishEventScheduler);
    }
}
