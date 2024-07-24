package com.stitch.admin.controller;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.PasswordUpdateRequest;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserManagementService userManagementService;


    @PutMapping("/update-user")
    @PreAuthorize("hasAuthority('PERM_UPDATE_USER')")
    public ResponseEntity<ApiResponse<AdminUser>> updateUserDetails(@Valid @RequestBody UpdateUserRequest request,
                                                                    @RequestParam(required = false) String email) {
        log.info("Admin User Update request ==> {}", request);
        ApiResponse<AdminUser> response = userManagementService.updateUserDetails(request, email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasAuthority('PERM_UPDATE_PSW')")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody PasswordUpdateRequest request) {
        log.info("Admin User password update request ==> {}", request);
        ApiResponse<Void> response = userManagementService.updateUserPassword(request);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PatchMapping("/deactivate-admin")
    @PreAuthorize("hasAuthority('PERM_DEACTIVATE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateAdmin(@RequestParam String email) {
        ApiResponse<Void> response = userManagementService.deactivateAdmin(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }


    @PatchMapping("/activate-admin")
    @PreAuthorize("hasAuthority('PERM_ACTIVATE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateAdmin(@RequestParam String email) {
        ApiResponse<Void> response = userManagementService.activateAdmin(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PatchMapping("/deactivate-user")
    @PreAuthorize("hasAuthority('PERM_DEACTIVATE_USER')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@RequestParam String email) {
        ApiResponse<Void> response = userManagementService.deactivateUser(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PatchMapping("/activate-user")
    @PreAuthorize("hasAuthority('PERM_ACTIVATE_USER')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@RequestParam String email) {
        ApiResponse<Void> response = userManagementService.activateUser(email);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

}
