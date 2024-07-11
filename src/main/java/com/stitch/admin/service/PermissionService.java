package com.stitch.admin.service;


import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PermissionService {

    ApiResponse<Role> assignPermissionsToRole(String roleName, List<String> permissions);

    ApiResponse<Map<String, String>> createNewPermissions(List<String> permissions);
    Optional<Permission> createPermission(String permissionName);

    ApiResponse<List<Permission>> getAllPermissions(int page, int size, String sort);
}
