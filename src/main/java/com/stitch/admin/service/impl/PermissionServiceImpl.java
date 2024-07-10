package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.PermissionRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.stitch.admin.utils.Constants.FAILED;
import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    @Override
    public ApiResponse<Role> assignPermissionsToRole(String roleName, List<String> permissions) {
        if (isNullOrEmpty(roleName)){
            throw new ApiException("Role name is required",400);
        }
        if (Objects.isNull(permissions) || permissions.isEmpty()){
            throw new ApiException("At least one permission is required to be assigned",400);
        }
        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("No Role found with name : "+roleName));
        Set<Permission> userPermissions = Objects.isNull(role.getPermissions()) ? new HashSet<>() : role.getPermissions();
        Set<Permission> newPermissions = new HashSet<>();
        List<String> invalidPermissions = new ArrayList<>();

        for (String perm : permissions){
           Optional<Permission>optionalPermission =  permissionRepository.findByNameIgnoreCase(perm);
           if (optionalPermission.isPresent()){
               Permission permission = optionalPermission.get();
               if(!userPermissions.contains(permission)){
                   newPermissions.add(permission);
               }

           }else invalidPermissions.add(perm);
        }
        if (!invalidPermissions.isEmpty()) {
            return new ApiResponse<>(FAILED, 400,"Invalid Permissions: " + String.join(", ", invalidPermissions));
        }
        userPermissions.addAll(newPermissions);
        role.setPermissions(userPermissions);
        Role updatedRole = roleRepository.save(role);
        return new ApiResponse<>(SUCCESS,200,"Permissions assigned successfully",updatedRole);

    }

    private boolean isNullOrEmpty(String value){
        return Objects.isNull(value) || value.trim().isEmpty();
    }
}
