package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.stitch.admin.utils.Constants.FAILED;
import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> createRole(String roleName) {
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

    @Override
    public ApiResponse<AdminUser> assignRolesToUser(String email, List<String> roles) {
        return null;
    }

    @Override
    public ApiResponse<Map<String, String>> createNewRoles(List<String> roles) {
        if (Objects.isNull(roles) || roles.isEmpty()){
            throw new ApiException("At least one role is required for creation", 400);
        }

        try {
            long newRolesCount = roles.stream()
                    .map(this::createRole)
                    .filter(Optional::isPresent)
                    .count();

            String message = newRolesCount == 0 ? "Roles already exist"
                    : (newRolesCount == 1 ? "1 new role created" : newRolesCount + " new roles created");
            return new ApiResponse<>(newRolesCount == 0 ? FAILED : SUCCESS, newRolesCount == 0 ? 400 : 201, message);

        }catch (Exception e){
            log.error("Error creating roles ==> {} :: {}",roles,e.getMessage());
            throw new ApiException("Failed to create roles ",417);
        }
    }

    @Override
    public ApiResponse<Void> revokeUserRole(String email, String role) {
        return null;
    }


}
