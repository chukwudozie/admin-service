package com.stitch.admin.controller;

import com.stitch.admin.payload.request.PasswordResetRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordService;

    @GetMapping("/send-password-notification")
    public ResponseEntity<ApiResponse<?>> sendNotification(@RequestParam String email) {
        ApiResponse<Void> response = passwordService.sendPasswordChangeNotification(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody PasswordResetRequest request) {
        ApiResponse<Void> response = passwordService.resetPassword(request);
        return new ResponseEntity<>(response, status(response.getCode()));
    }
}
