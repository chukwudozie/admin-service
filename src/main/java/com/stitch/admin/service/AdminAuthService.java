package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.Map;
import java.util.Optional;

public interface AdminAuthService {

    Optional<AdminUser> fetchUserByEmail(String email);

    ApiResponse<AdminUser> registerUser(RegistrationRequest request, String role, String permission);
    ApiResponse<Map<String,Object>> loginUser(LoginRequest request);

    ApiResponse<Map<String, Object>> refreshToken(String refreshToken);

    ApiResponse<Map<String, Object>> logout(String token);
}
