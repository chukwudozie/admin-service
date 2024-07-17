package com.stitch.admin.controller;

import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.stitch.admin.utils.Constants.DASHBOARD_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(DASHBOARD_URL)
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardCount(){
        ApiResponse<Map<String,Object>> response = dashboardService.getCount();
        return new ResponseEntity<>(response,status(response.getCode()));
    }
}
