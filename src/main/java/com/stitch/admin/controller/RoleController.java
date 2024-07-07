package com.stitch.admin.controller;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.stitch.admin.utils.Constants.ADMIN_BASE_URL;
import static com.stitch.admin.utils.Constants.status;


@RestController
@RequestMapping(ADMIN_BASE_URL)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PutMapping("/assign-roles")
    public ResponseEntity<ApiResponse<AdminUser>> assignRoles(@RequestParam(name = "roleName") String email,
                                                              @RequestParam("permissions")List<String> roles){
        ApiResponse<AdminUser> response = roleService.assignRolesToUser(email,roles);
        return new ResponseEntity<>(response,status(response.getCode()));
    }

    @PostMapping("create-role")
    public ResponseEntity<ApiResponse<Map<String,String>>> createRole(@RequestBody List<String> roles){
        ApiResponse<Map<String,String>> response = roleService.createNewRoles(roles);
        return new ResponseEntity<>(response,status(response.getCode()));
    }

    @PatchMapping("revoke-user-role/{email}")
    public ResponseEntity<ApiResponse<Void>> revokeUserRole(@PathVariable String email, @RequestParam(required = false, defaultValue = "") String role){
        ApiResponse<Void> response = roleService.revokeUserRole(email, role);
        return new ResponseEntity<>(response,status(response.getCode()));

    }

    @GetMapping("get-all-roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "asc") String sort){
        ApiResponse<List<Role>> response = roleService.getAllRoles(page,size,sort);
        return new ResponseEntity<>(response,status(response.getCode()));
    }

}
