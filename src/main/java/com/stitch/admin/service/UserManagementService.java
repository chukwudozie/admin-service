package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserManagementService {
    ApiResponse<AdminUser> updateUserDetails(UpdateUserRequest request, String email, HttpServletRequest servletRequest);
}
