package com.stitch.admin.service;

import com.stitch.admin.payload.response.ApiResponse;

import java.util.Map;

public interface AdminDashboardService {
    ApiResponse<Map<String, Object>> getCount();

}
