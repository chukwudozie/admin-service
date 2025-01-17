package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.PermissionRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.stitch.admin.model.enums.Permissions.PERM_DEFAULT;
import static com.stitch.admin.utils.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public ApiResponse<Role> assignPermissionsToRole(String roleName, List<String> permissions) {
        if (isNullOrEmpty(roleName)) {
            throw new ApiException("Role name is required", 400);

        }
        if (Objects.isNull(permissions) || permissions.isEmpty()) {
            throw new ApiException("At least one permission is required to be assigned", 400);
        }
        String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user", 401));
        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("No Role found with name : " + roleName));
        Set<Permission> userPermissions = Objects.isNull(role.getPermissions()) ? new HashSet<>() : role.getPermissions();
        Set<Permission> newPermissions = new HashSet<>();
        List<String> invalidPermissions = new ArrayList<>();

        for (String perm : permissions) {
            Optional<Permission> optionalPermission = permissionRepository.findByNameIgnoreCase(perm);
            if (optionalPermission.isPresent()) {
                Permission permission = optionalPermission.get();
                if (!userPermissions.contains(permission)) {
                    newPermissions.add(permission);
                }

            } else invalidPermissions.add(perm);
        }
        if (!invalidPermissions.isEmpty()) {
            return new ApiResponse<>(FAILED, 400, "Invalid Permissions: " + String.join(", ", invalidPermissions));
        }
        userPermissions.addAll(newPermissions);
        role.setPermissions(userPermissions);
        role.setLastUpdated(Instant.now());
        role.setModifiedBy(loggedInUser);
        Role updatedRole = roleRepository.save(role);
        return new ApiResponse<>(SUCCESS, 200, "Permissions assigned successfully", updatedRole);

    }

    @Override
    public ApiResponse<Map<String, String>> createNewPermissions(List<String> permissions) {
        if (Objects.isNull(permissions) || permissions.isEmpty()) {
            throw new ApiException("At least one permission is required for creation", 417);
        }
        try {
            long newPermissionCount = permissions.stream()
                    .map(this::createPermission)
                    .filter(Optional::isPresent)
                    .count();
            String message = newPermissionCount == 0 ? "Roles already exist or invalid role names"
                    : (newPermissionCount == 1 ? "1 new role created" : newPermissionCount + " new roles created");
            return new ApiResponse<>(newPermissionCount == 0 ? FAILED : SUCCESS, newPermissionCount == 0 ? 400 : 201, message);
        } catch (Exception e) {
            log.error("Error creating permissions ==> {} :: {}", permissions, e.getMessage());
            throw new ApiException("Failed to create permissions ", 417);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    @Override
    public Optional<Permission> createPermission(String permissionName) {
        String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user", 401));
        if (Objects.isNull(permissionName) || permissionName.isEmpty() || !validPermissionName(permissionName))
            return Optional.empty();
        Optional<Permission> optionalPermission = permissionRepository.findByNameIgnoreCase(permissionName);
        if (optionalPermission.isPresent()) {
            return optionalPermission;
        } else {
            Permission newPermission = new Permission(permissionName.toUpperCase());
            newPermission.setDateCreated(Instant.now());
            newPermission.setCreatedBy(loggedInUser);
            return Optional.of(permissionRepository.save(newPermission));
        }
    }

    @Override
    public ApiResponse<List<Permission>> getAllPermissions(int page, int size, String sortDirection) {
        List<Permission> allPermissions = permissionRepository.findAll();
        if (allPermissions.isEmpty())
            return new ApiResponse<>(FAILED, 404, "NO PERMISSION FOUND");
        if (!Objects.equals("asc", sortDirection) && !Objects.equals("desc", sortDirection)) {
            sortDirection = "asc";
        }
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), "dateCreated");
        Page<Permission> roles = permissionRepository.findAll(PageRequest.of(page, size, sort));
        return new ApiResponse<>(SUCCESS, 200, "available permissions", roles.getContent());
    }

    @Override
    public ApiResponse<Void> revokeRolePermission(String role, String permission) {
        if (isNullOrEmpty(role))
            return new ApiResponse<>(FAILED, 400, "Role name is required");
        try {
            Role existingRole = roleRepository.findByNameIgnoreCase(role)
                    .orElseThrow(() -> new ApiException(String.format("Role %s does not exist ", role), 400));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user", 401));
            Set<Permission> rolePermissions = existingRole.getPermissions();
            if (isNullOrEmpty(permission)) {
                //revoke all role permissions except default
                rolePermissions = new HashSet<>();
                createPermission(PERM_DEFAULT.name()).ifPresent(rolePermissions::add);
                existingRole.setPermissions(rolePermissions);
                existingRole.setLastUpdated(Instant.now());
                existingRole.setModifiedBy(loggedInUser);
                roleRepository.save(existingRole);
                return new ApiResponse<>(SUCCESS, 200, "all user roles successfully revoked");
            }

            if (!permissionRepository.existsByNameIgnoreCase(permission))
                return new ApiResponse<>(FAILED, 404, String.format("Permission : %s does not exist", permission));
            permissionRepository.findByNameIgnoreCase(role).ifPresent(rolePermissions::remove);
            existingRole.setPermissions(rolePermissions);
            existingRole.setLastUpdated(Instant.now());
            existingRole.setModifiedBy(loggedInUser);
            roleRepository.save(existingRole);
            return new ApiResponse<>(SUCCESS, 200, String.format("role : %s successfully revoked", role));

        } catch (Exception e) {
            log.info("Error occurred while revoking role permissions ==> {}", e.getMessage());
            return new ApiResponse<>(FAILED, 417, "Could not revoke role permissions ");
        }
    }

    private boolean validPermissionName(String permissionName) {
        String formattedName = permissionName.trim().toUpperCase();

        if (!formattedName.startsWith("PERM_")) {
            log.info("Valid permission name must start with PERM_");
            return false;
        }
        if (!formattedName.contains("_")) {
            log.info("Valid permission name must have at least one underscore");
            return false;
        }
        if (formattedName.contains(" ")) {
            log.info("Permission name should not contain any white space");
            return false;
        }
        return true;
    }
}
