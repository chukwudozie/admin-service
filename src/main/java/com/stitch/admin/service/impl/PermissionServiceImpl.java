package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.PermissionRepository;
import com.stitch.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    @Override
    public ApiResponse<AdminUser> assignPermissionsToRole(String roleName, List<String> permissions) {
        return null;
    }
}
