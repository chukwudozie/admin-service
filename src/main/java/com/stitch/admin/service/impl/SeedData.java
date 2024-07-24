package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.*;
import com.stitch.admin.model.enums.Permissions;
import com.stitch.admin.model.enums.Roles;
import com.stitch.admin.model.enums.TransactionStatus;
import com.stitch.admin.model.enums.TransactionType;
import com.stitch.admin.repository.*;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.stitch.admin.model.enums.TransactionStatus.P;
import static com.stitch.admin.model.enums.TransactionType.PAY_BILL;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeedData implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final UserEntityRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TransactionRepository transactionRepository;
    private static final String email = "stitchAdmin@gmail.com";
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String password = "P@sswd2024";
    private static final String CUSTOMER = "CUSTOMER";
    private static final String VENDOR = "VENDOR";
    private static final String VENDOR_PHONE = "123456";
    private static final String CUSTOMER_PHONE = "246809";

    @Override
    public void run(String... args) {

        createSuperAdmin();

        seedUsers();

        seedTransactions();


    }

    public void seedTransactions() {
        if(transactionRepository.count() < 1){
            for (int i = 0; i <4;i++){
                Transaction transaction = new Transaction();
                transaction.setTransactionId(UUID.randomUUID().toString());
                transaction.setTransactionType(PAY_BILL);
                transaction.setStatus(P);
                transaction.setDescription("test transaction");
                transaction.setDateCreated(Instant.now());
                transactionRepository.save(transaction);
            }
        }
    }

    @Transactional
    public void seedUsers() {
        createUser(CUSTOMER, CUSTOMER_PHONE);
        createUser(VENDOR, VENDOR_PHONE);
    }

    private void createUser(String name, String phone) {
        if(!userRepository.existsByUsernameAndRole_Name(name,name)){
            String firstName = name.concat("_").concat("FIRSTNAME");
            String lastName = name.concat("_").concat("LASTNAME");
            UserEntity user = new UserEntity();
            user.setUsername(name);
            Role customerRole;
            if(!roleRepository.existsByNameIgnoreCase(name)){
                customerRole = new Role(name);
                customerRole = roleRepository.save(customerRole);
            }else
                customerRole = roleRepository.findByNameIgnoreCase(name).get();
            user.setRole(customerRole);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCountry("NIGERIA");
            user.setPhoneNumber(phone);
            user.setEmailAddress(name.concat("@gmail.com"));
            user.setUserId(UUID.randomUUID().toString());
            user.setDateCreated(Instant.now());
            userRepository.save(user);
        }
    }

    @Transactional
    public void createSuperAdmin(){

        try {
            String superAdminName = Roles.SUPER_ADMIN.name();
            if(adminUserRepository.findAdminUserByEmailAddress(email).isPresent()){
                log.info("SUPER ADMIN ALREADY EXISTS");
                return;
            }
            if(roleRepository.existsByNameIgnoreCase(superAdminName)
                    && adminUserRepository.existsByRolesContains(roleRepository.findByNameIgnoreCase(superAdminName).orElseThrow())){
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
            AdminUser savedUser = adminUserRepository.save(superAdmin);
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
