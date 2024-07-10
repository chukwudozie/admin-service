package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.UserExistsException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.security.JwtTokenUtils;
import com.stitch.admin.service.AdminAuthService;
import com.stitch.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static com.stitch.admin.utils.Constants.FAILED;
import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final AdminUserRepository adminUserRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RoleRepository roleRepository;
    @Override
    public Optional<AdminUser> fetchUserByEmail(String email) {
        return adminUserRepository.findAdminUserByEmailAddress(email);
    }

    @Override
    public ApiResponse<AdminUser> registerUser(RegistrationRequest request, String role, String permission) {
        validateUser(request);

        AdminUser adminUser = new AdminUser();

        try {
            BeanUtils.copyProperties(request,adminUser);
            int age = calculateAge(request.getDateOfBirth());
            adminUser.setAge(age);
            adminUser.setEnabled(true);
            adminUser.setActivated(true);
            adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
            Set<Role> userRoles = new HashSet<>();
            if(Objects.nonNull(role) && !role.isEmpty()){
                Optional<Role> newRole = roleService.createRole(role);
                newRole.ifPresent(r ->{
                    Optional<Permission> optionalPermission = roleService.createDefaultPermission(permission);
                    optionalPermission.ifPresent(p ->{
                        if(!roleRepository.existsByPermissionsContains(p)){
                            Set<Permission> userPerm = r.getPermissions();
                            if(Objects.isNull(userPerm))
                                userPerm = new HashSet<>();
                            userPerm.add(p);
                            roleRepository.save(r);
                        }
                    });

                    userRoles.add(r);
                });
            }
            adminUser.setRoles(userRoles);
            adminUser.setDateCreated(Instant.now());

            AdminUser savedUser = adminUserRepository.save(adminUser);
            return new ApiResponse<>(SUCCESS,201,"User successfully created", savedUser);
        }catch (Exception e){
            log.error("Error in user registration ==> {}",e.getMessage());
            throw new ApiException("Failed to register user : "+e.getMessage(),417);
        }


    }


    private int calculateAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    private void validateUser(RegistrationRequest request) {
        if(adminUserRepository.findAdminUserByEmailAddress(request.getEmailAddress()).isPresent()){
            throw new UserExistsException("Email Already Exists");
        }
        if (adminUserRepository.findAdminUserByPhoneNumber(request.getPhoneNumber()).isPresent()){
            throw new UserExistsException("Phone Number already exists ");
        }

    }

    @Override
    public ApiResponse<Map<String, Object>> loginUser(LoginRequest request) {
        UsernamePasswordAuthenticationToken auth;
        try {
            auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(auth);
            Map<String, Object> tokens = new HashMap<>();
            if(authentication.isAuthenticated()){
                User user = (User) authentication.getPrincipal();

                List<String> roles = user.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority
                ).toList();
                System.err.println("initial roes" +roles);
                AdminUser adminUser = adminUserRepository.findAdminUserByEmailAddress(request.getEmail())
                        .orElseThrow(() -> new ApiException("User not found with email",400));
                List<String> roleList = adminUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                List<String> permissionList = adminUser.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .collect(Collectors.toList());
                System.err.println("role list ="+roleList);
                System.err.println("permission list =="+permissionList);
                String accessToken = jwtTokenUtils.generateJwtToken(request.getEmail(),roleList,permissionList);
                String refreshToken = jwtTokenUtils.generateJwtRefreshToken(request.getEmail(),roleList, permissionList);

                tokens.put("access_token",accessToken);
                tokens.put("refresh_token",refreshToken);
                tokens.put("roles",roleList);
                tokens.put("permissions",permissionList);
                return new ApiResponse<>(SUCCESS,200,"login successful",tokens);
            }else {
                return new ApiResponse<>(FAILED,401,"Not authenticated, check credentials");
            }

        }catch (Exception e){
            log.error("Exception occurred during user login ==> {}",e.getMessage());
            return new ApiResponse<>(FAILED,401,e.getMessage());

        }
    }
}
