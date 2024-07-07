package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.AdminAuthService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminAuthServiceImpl implements AdminAuthService {
    @Override
    public Optional<AdminUser> fetchUserByUserName(String username) {
        return Optional.empty();
    }

    @Override
    public ApiResponse<AdminUser> registerUser(RegistrationRequest request, List<String> roles) {
        return null;
    }

    @Override
    public ApiResponse<Map<String, Object>> loginUser(LoginRequest request) {
        return null;
    }
}
