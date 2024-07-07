package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;

public interface PermissionService {

    ApiResponse<AdminUser> assignPermissionsToRole(String roleName, List<String> permissions);
}
