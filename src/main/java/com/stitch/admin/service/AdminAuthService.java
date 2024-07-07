package com.stitch.admin.service;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AdminAuthService {

    Optional<AdminUser> fetchUserByEmail(String email);

    ApiResponse<AdminUser> registerUser(RegistrationRequest request, List<String> roles);
    ApiResponse<Map<String,Object>> loginUser(LoginRequest request);

}
