package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.PasswordUpdateRequest;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;

public interface UserManagementService {
    ApiResponse<AdminUser> updateUserDetails(UpdateUserRequest request, String email);

    ApiResponse<Void> updateUserPassword(PasswordUpdateRequest request);

    ApiResponse<Void> deactivateAdmin(String email);

    ApiResponse<Void> activateAdmin(String email);

    ApiResponse<Void> deactivateUser(String email);

    ApiResponse<Void> activateUser(String email);
}
