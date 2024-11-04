package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.AdminDao;
import com.itsthatjun.ecommerce.dao.domainmodel.AdminDetail;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.mbg.mapper.AdminMapper;
import com.itsthatjun.ecommerce.service.AdminService;
import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@Service
public class AdminServiceImpl implements ReactiveUserDetailsService, AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AdminMapper adminMapper;

    private final AdminDao adminDao;

    private final PasswordEncoder passwordEncoder;

    private final Scheduler adminServiceScheduler;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, AdminDao adminDao, PasswordEncoder passwordEncoder,
                            @Qualifier("publishEventScheduler") Scheduler adminServiceScheduler) {
        this.adminMapper = adminMapper;
        this.adminDao = adminDao;
        this.passwordEncoder = passwordEncoder;
        this.adminServiceScheduler = adminServiceScheduler;
    }

    @Override
    public Mono<String> register(Admin newAdmin) {
        newAdmin.setStatus("active");
        AdminExample example = new AdminExample();
        example.createCriteria().andUsernameEqualTo(newAdmin.getUsername());

        // Check for existing account reactively
        return Mono.fromCallable(() -> adminMapper.selectByExample(example))
                .subscribeOn(adminServiceScheduler)
                .flatMap(existing -> {
                    if (!existing.isEmpty()) {
                        return Mono.error(new RuntimeException("Account already exists")); // Use a proper exception
                    }

                    //TODO: add role and permission
                    // role will be given by main admin
                    // permission will all be default not active and need to add by main admin

                    // Set password and insert new admin reactively
                    newAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));
                    return Mono.fromRunnable(() -> adminMapper.insert(newAdmin))
                            .then(Mono.just("Admin successfully added")); // Return success message after insert
                });
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException{
        AdminExample example = new AdminExample();
        example.createCriteria().andUsernameEqualTo(username);

        // Fetch the admin details reactively
        return Mono.fromCallable(() -> adminMapper.selectByExample(example))
                .subscribeOn(adminServiceScheduler)
                .flatMap(adminList -> {
                    if (adminList.isEmpty()) {
                        return Mono.error(new UsernameNotFoundException("Username not found"));
                    }

                    Admin admin = adminList.get(0);
                    Mono<List<Permission>> permissionsMono = Mono.fromCallable(() -> adminDao.getPermissionList(admin.getId()))
                            .subscribeOn(adminServiceScheduler);;
                    Mono<List<Roles>> rolesMono = Mono.fromCallable(() -> adminDao.getRolesList(admin.getId()))
                            .subscribeOn(adminServiceScheduler);;

                    // Combine permissions and roles into a CustomUserDetail object
                    return Mono.zip(permissionsMono, rolesMono)
                            .map(tuple -> new CustomUserDetail(admin, tuple.getT2(), tuple.getT1()));
                });
    }

    @Override
    public Mono<AdminDetail> getAdminDetail(int id) {
        AdminExample example = new AdminExample();
        example.createCriteria().andIdEqualTo(id);

        // Retrieve the admin information reactively
        return Mono.fromCallable(() -> adminMapper.selectByExample(example))
                .subscribeOn(adminServiceScheduler)
                .flatMap(adminList -> {
                    if (adminList.isEmpty()) {
                        return Mono.error(new NotFoundException("Id not found"));
                    }

                    Admin admin = adminList.get(0);

                    // Fetch roles, permissions, and logs reactively
                    Mono<List<Permission>> permissionsMono = Mono.fromCallable(() -> adminDao.getPermissionList(admin.getId()))
                            .subscribeOn(adminServiceScheduler);;
                    Mono<List<Roles>> rolesMono = Mono.fromCallable(() -> adminDao.getRolesList(admin.getId()))
                            .subscribeOn(adminServiceScheduler);;
                    Mono<List<AdminLoginLog>> loginLogsMono = Mono.fromCallable(() -> adminDao.getLoginList(admin.getId()))
                            .subscribeOn(adminServiceScheduler);;

                    // Combine all the results into an AdminDetail object
                    return Mono.zip(permissionsMono, rolesMono, loginLogsMono)
                            .map(tuple -> {
                                AdminDetail adminDetail = new AdminDetail();
                                adminDetail.setAdmin(admin);
                                adminDetail.setPermissions(tuple.getT1());
                                adminDetail.setRoles(tuple.getT2());
                                adminDetail.setLoginLogs(tuple.getT3());
                                return adminDetail;
                            });
                });
    }

    @Override
    public String getAdminName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            return userDetail.getAdmin().getName();
        }
        return null; // or handle the case where the user is not authenticated
    }
}
