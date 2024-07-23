package com.stitch.admin.service;

import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {
    ApiResponse<Map<String, Object>> getCount();

    ApiResponse<List<UserDto>> fetchUsers(int page, int size, String type, String enabled, String name);
}
