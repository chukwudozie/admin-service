package com.stitch.admin.controller;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;


    @PutMapping("/assign-permissions")
    public ResponseEntity<ApiResponse<Role>> assignRoles(@RequestParam(name = "role") String roleName,
                                                         @RequestParam(name = "permissions") List<String> permissions){
        ApiResponse<Role> response = permissionService.assignPermissionsToRole(roleName, permissions);
        return new ResponseEntity<>(response,status(response.getCode()));
    }
}
