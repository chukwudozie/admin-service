package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.UserExistsException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.model.entity.User;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    @Override
    public Optional<AdminUser> fetchUserByUserName(String username) {
        return Optional.empty();
    }

    @Override
    public ApiResponse<AdminUser> registerUser(RegistrationRequest request, List<String> roles) {
        validateUser(request);

        AdminUser adminUser = new AdminUser();

        try {
            BeanUtils.copyProperties(request,adminUser);
            int age = calculateAge(request.getDateOfBirth());
            adminUser.setAge(age);
            adminUser.setEnabled(true);
            adminUser.setActivated(true);
            Set<String> roleNames = new HashSet<>();
            Set<Role> userRoles = new HashSet<>();
            roles.forEach(rol -> {
                Optional<Role> role = createRole(rol);
                role.ifPresent(r ->{
                    if(roleNames.contains(r.getName())){
                        userRoles.add(r);
                        roleNames.add(r.getName());
                    }});
            });
            adminUser.setRoles(userRoles);
            adminUser.setDateCreated(Instant.now());

            AdminUser savedUser = adminUserRepository.save(adminUser);
            return new ApiResponse<>(SUCCESS,201,"User successfully created", savedUser);
        }catch (Exception e){
            log.error("Error in user registration ==> {}",e.getMessage());
            throw new ApiException("Failed to register user : "+e.getMessage(),417);
        }


    }

    private Optional<Role> createRole(String roleName) {
        if (Objects.isNull(roleName) || roleName.isEmpty())
            return Optional.empty();
        Optional<Role> optionalRole = roleRepository.findByNameIgnoreCase(roleName);
        if (optionalRole.isPresent()){
            return optionalRole;
        }else {
            Role newRole = new Role(roleName);
            return Optional.of(roleRepository.save(newRole));
        }
    }

    private int calculateAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    private void validateUser(RegistrationRequest request) {
        if(adminUserRepository.findAdminUserByEmailAddress(request.getEmail()).isPresent()){
            throw new UserExistsException("Email Already Exists");
        }
        if (adminUserRepository.findAdminUserByPhoneNumber(request.getPhoneNumber()).isPresent()){
            throw new UserExistsException("Phone Number already exists ");
        }

    }

    @Override
    public ApiResponse<Map<String, Object>> loginUser(LoginRequest request) {
        return null;
    }
}
