package com.stitch.admin.service;

import com.stitch.admin.model.dto.TransactionDto;
import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.payload.response.ApiResponse;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {
    ApiResponse<Map<String, Object>> getUsersCount();

    ApiResponse<List<UserDto>> fetchUsers(int page, int size, String type, String enabled, String name);

    ApiResponse<UserDto> retrieveUserByEmail(String email);

    ApiResponse<Map<String, Object>> sumTransactionAmount(String status, String type, String paymentMode,String dateFilter, String startDate, String endDate, String monthYear);

    ApiResponse<List<TransactionDto>> getTransactions(int page, int size, String status, String type, String paymentMode, String dateFilter, String startDate, String endDate, String monthYear);
}
