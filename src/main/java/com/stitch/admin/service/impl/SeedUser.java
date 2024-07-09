package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.model.enums.Permissions;
import com.stitch.admin.model.enums.Roles;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.PermissionRepository;
import com.stitch.admin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeedUser implements CommandLineRunner {

    private final AdminUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private static final String email = "stitchAdmin@gmail.com";
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String password = "P@sswd2024";

    @Override
    public void run(String... args) {

        createSuperAdmin();


    }

    @Transactional
    public void createSuperAdmin(){

        try {
            String superAdminName = Roles.SUPER_ADMIN.name();
            if(userRepository.findAdminUserByEmailAddress(email).isPresent()){
                log.info("SUPER ADMIN ALREADY EXISTS");
                return;
            }
            if(roleRepository.existsByNameIgnoreCase(superAdminName)
                    && userRepository.existsByRolesContains(roleRepository.findByNameIgnoreCase(superAdminName).orElseThrow())){
                    log.info("SUPER ADMIN ALREADY EXISTS");
                    return;

            }


            AdminUser superAdmin = new AdminUser();
            superAdmin.setPassword(passwordEncoder.encode(password));
            superAdmin.setEmailAddress(email);
            superAdmin.setLastUpdated(Instant.now());
            superAdmin.setDateCreated(Instant.now());
            superAdmin.setEnabled(true);
            superAdmin.setActivated(true);
            Set<String> adminRoles = Set.of(superAdminName);
            log.info("Set of roles ==> {}",adminRoles);
            Set<String> adminPermissions = getAllPermissionNames();
            log.info("set of permissions ==> {}",adminPermissions);
            Set<Role> roles = createAdminRoles(adminRoles,adminPermissions);
            superAdmin.setRoles(roles);
            superAdmin.setFirstName("");
            superAdmin.setLastName("");
            superAdmin.setPhoneNumber("0811111111");
            AdminUser savedUser = userRepository.save(superAdmin);
            log.info("Saved User == > {}",savedUser);

        }catch (Exception e){
            log.error("Failed to create and save admin User => {}",e.getMessage());
        }


    }

    private Set<Role> createAdminRoles (Set<String> roles, Set<String> permissions){
        Set<Role> userRoles = new HashSet<>();
        Set<String> roleNames = new HashSet<>();
        roles.forEach(role -> {
            try {
                Role newRole;
                if(roleNames.contains(role)){
                    return;
                }
                Set<Permission> createdPermissions = createPermissions(permissions);
                if(roleRepository.existsByNameIgnoreCase(role)){
                    newRole = roleRepository.findByNameIgnoreCase(role).get();
                    Set<Permission> existingPermissions = newRole.getPermissions();
                    if (Objects.isNull(existingPermissions) || existingPermissions.isEmpty()){
                        newRole.setPermissions(createdPermissions);
                    }else{
                        createdPermissions.forEach(permission -> {
                            if(!existingPermissions.contains(permission)){
                                existingPermissions.add(permission);
                            }
                        });
                        newRole.setPermissions(existingPermissions);
                    }
                    newRole.setLastUpdated(Instant.now());
                }else{
                    newRole = new Role(role);
                    newRole.setDateCreated(Instant.now());
                    newRole.setPermissions(createdPermissions);

                }
                newRole = roleRepository.save(newRole);
                userRoles.add(newRole);
                roleNames.add(role);
            }catch (Exception e){
                log.error("Exception creating role ==> {}::{}",role,e.getMessage());
            }

        });
        return userRoles;
    }

    private Set<Permission> createPermissions(Set<String> permissions){
        Set<Permission> userPermissions = new HashSet<>();
        Set<String> permissionNames = new HashSet<>();
        permissions.forEach(permission -> {
            Permission perm;
            if(permissionNames.contains(permission))
                return;
            if(permissionRepository.existsByNameIgnoreCase(permission)){
                perm = permissionRepository.findByNameIgnoreCase(permission).get();
            }else {
                var createPerm = new Permission(permission);
                createPerm.setDateCreated(Instant.now());
                perm = permissionRepository.save(createPerm);
            }
            userPermissions.add(perm);
            permissionNames.add(permission);
        });

        return userPermissions;
    }

    private Set<String> getAllPermissionNames() {
        return Stream.of(Permissions.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
