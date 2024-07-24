package com.stitch.admin.controller;

import com.stitch.admin.model.entity.Permission;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;
import static com.stitch.admin.utils.Constants.status;

@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;


    @PutMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('PERM_ASSIGN_PERM')")

    public ResponseEntity<ApiResponse<Role>> assignRoles(@RequestParam(name = "role") String roleName,
                                                         @RequestParam(name = "permissions") List<String> permissions) {
        ApiResponse<Role> response = permissionService.assignPermissionsToRole(roleName, permissions);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PostMapping("create-permission")
    @PreAuthorize("hasAuthority('PERM_CREATE_PERM')")
    public ResponseEntity<ApiResponse<Map<String, String>>> createRole(@RequestBody List<String> permissions) {
        ApiResponse<Map<String, String>> response = permissionService.createNewPermissions(permissions);
        return new ResponseEntity<>(response, status(response.getCode()));
    }

    @PatchMapping("revoke-user-role/{role}")
    @PreAuthorize("hasAuthority('PERM_REVOKE_PERM')")
    public ResponseEntity<ApiResponse<Void>> revokeRolePermission(@PathVariable String role, @RequestParam(required = false, defaultValue = "") String permission) {
        ApiResponse<Void> response = permissionService.revokeRolePermission(role, permission);
        return new ResponseEntity<>(response, status(response.getCode()));

    }

    @GetMapping("get-all-permissions")
    @PreAuthorize("hasAuthority('PERM_VIEW_PERMS')")
    public ResponseEntity<ApiResponse<List<Permission>>> getAllRoles(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "asc") String sort) {
        ApiResponse<List<Permission>> response = permissionService.getAllPermissions(page, size, sort);
        return new ResponseEntity<>(response, status(response.getCode()));
    }
}
