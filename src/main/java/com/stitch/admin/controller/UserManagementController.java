package com.stitch.admin.controller;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.RegistrationRequest;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<AdminUser>> updateUserDetails(@RequestBody UpdateUserRequest request,
                                                                    @RequestParam(required = false)String email, HttpServletRequest servletRequest){
        log.info("Admin User Update request ==> {}",request);
        ApiResponse<AdminUser> response = userManagementService.updateUserDetails(request,email, servletRequest);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasAuthority('PERM_DEFAULT')")
    public ResponseEntity<ApiResponse<AdminUser>> updatePassword(@RequestBody UpdateUserRequest request,
                                                                    @RequestParam(required = false)String email, HttpServletRequest servletRequest){
        log.info("Admin User password update request ==> {}",request);
        ApiResponse<AdminUser> response = userManagementService.updateUserDetails(request,email, servletRequest);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    //update password
    //update details  -- in progress
    //deactivate user
    //activate user
    //get all active users
    //get all vendor users
    //get all customer users
    //get all orders pending
    // get all orders completed
    //get all transactions completed
    //get all admins
    //disable admins
    //enable admins
    //disable vendors or customers\
    //get a particuler user (get a vendor, get a customer)

}
