package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.PermissionRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeedUser implements CommandLineRunner {

    private final AdminAuthService authService;
    private final AdminUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private static final String email = "stitchAdmin@gmail.com";
    private BCryptPasswordEncoder passwordEncoder;
    private static final String password = "P@sswd2024";

    @Override
    public void run(String... args) throws Exception {
        AdminUser superAdmin = new AdminUser();
        superAdmin.setPassword(passwordEncoder.encode(password));
        superAdmin.setEmailAddress(email);
        superAdmin.setLastUpdated(Instant.now());
        superAdmin.setDateCreated(Instant.now());
        superAdmin.setEnabled(true);
        superAdmin.setActivated(true);


    }

    private Set<Role> createAdminRoles (Set<String> roles){
        return new HashSet<>();
    }
}
