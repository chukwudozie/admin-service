package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RoleService {

    Optional<Role> createRole(String roleName);

    ApiResponse<AdminUser> assignRolesToUser(String email, List<String> roles);

    ApiResponse<Map<String, String>> createNewRoles(List<String> roles);

    ApiResponse<Void> revokeUserRole(String email, String role);
}
