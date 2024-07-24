package com.stitch.admin.controller;

import com.stitch.admin.model.dto.TransactionDto;
import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.stitch.admin.utils.Constants.DASHBOARD_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(DASHBOARD_URL)
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/count-users")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardUsersCount() {
        ApiResponse<Map<String, Object>> response = dashboardService.getUsersCount();
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @GetMapping("/fetch-users")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<List<UserDto>>> fetchUsers(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false, defaultValue = "all") String roleName,
            @RequestParam(required = false, defaultValue = "true") String enabled,
            @RequestParam(required = false, defaultValue = "") String username) {
        ApiResponse<List<UserDto>> response = dashboardService.fetchUsers(page, size, roleName, enabled, username);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @GetMapping("/get-user-by-email")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<UserDto>> retrieveUserByEmail(@RequestParam String email) {
        ApiResponse<UserDto> response = dashboardService.retrieveUserByEmail(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @GetMapping("/sum-transactions")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sumTransactionAmount(@RequestParam(required = false) String status,
                                                                                 @RequestParam(required = false) String type,
                                                                                 @RequestParam(required = false) String paymentMode,
                                                                                 @RequestParam(required = false, defaultValue = "current_week") String dateFilter,
                                                                                 @RequestParam(required = false) String startDate,
                                                                                 @RequestParam(required = false) String endDate,
                                                                                 @RequestParam(required = false) String monthYear) {
        ApiResponse<Map<String, Object>> response = dashboardService.sumTransactionAmount(status, type, paymentMode, dateFilter, startDate, endDate, monthYear);
        return new ResponseEntity<>(response, status(response.getCode()));
    }


    @GetMapping("/get-transactions")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false, defaultValue = "current_week") String dateFilter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String monthYear) {
        ApiResponse<List<TransactionDto>> response = dashboardService.getTransactions(page, size, status, type, paymentMode, dateFilter, startDate, endDate, monthYear);
        return new ResponseEntity<>(response, status(response.getCode()));
    }


}
