package com.stitch.admin.controller;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.LoginRequest;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.stitch.admin.utils.Constants.AUTH_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(AUTH_URL)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AdminAuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('PERM_CREATE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminUser>> registerUser(@Valid @RequestBody RegistrationRequest request,
                                   @RequestParam(required = false, defaultValue = "DEFAULT_ADMIN")String role,
                                   @RequestParam(required = false, defaultValue = "PERM_DEFAULT") String permission){
        log.info("Admin Registration request ==> {}",request);
        ApiResponse<AdminUser> response = authService.registerUser(request,role, permission);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,Object>>> loginUser(@RequestBody LoginRequest request){
        log.info("Login Request ==> {}",request);
        ApiResponse<Map<String,Object>> token = authService.loginUser(request);
        return new ResponseEntity<>(token,status(token.getCode()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String,Object>>> refreshAccessToken(@RequestHeader(name = "Authorization") String refreshToken){
        ApiResponse<Map<String,Object>> response = authService.refreshToken(refreshToken);
        return new ResponseEntity<>(response,status(response.getCode()));
    }



}
