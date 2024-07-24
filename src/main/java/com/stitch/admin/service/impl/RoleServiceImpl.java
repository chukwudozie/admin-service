package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.Role;
import com.stitch.admin.model.enums.Roles;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.RoleRepository;
import com.stitch.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.stitch.admin.model.enums.Roles.DEFAULT_ADMIN;
import static com.stitch.admin.utils.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AdminUserRepository userRepository;

    @Override
    public Optional<Role> createRole(String roleName) {
        if (Objects.isNull(roleName) || roleName.isEmpty() || !validRoleName(roleName))
            return Optional.empty();
        String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user", 401));
        Optional<Role> optionalRole = roleRepository.findByNameIgnoreCase(roleName);
        if (optionalRole.isPresent()) {
            return optionalRole;
        } else {
            Role newRole = new Role(roleName);
            newRole.setDateCreated(Instant.now());
            newRole.setCreatedBy(loggedInUser);
            return Optional.of(roleRepository.save(newRole));
        }
    }

    private boolean validRoleName(String roleName) {
        String formattedRoleName = roleName.trim().toUpperCase();
        return Arrays.stream(Roles.values())
                .anyMatch(role -> role.name().equals(formattedRoleName));
    }


    @Override
    public ApiResponse<AdminUser> assignRolesToUser(String email, List<String> roles) {

        if (isNullOrEmpty(email)) {
            throw new ApiException("Email of user is required", 400);
        }
        if (Objects.isNull(roles) || roles.isEmpty()) {
            throw new ApiException("At least one role is required to be assigned", 400);
        }
        AdminUser user = userRepository.findAdminUserByEmailAddress(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user exists with email " + email));
        String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));


        Set<Role> userRoles = user.getRoles();
        Set<Role> newRoles = new HashSet<>();
        List<String> invalidRoles = new ArrayList<>();

        for (String roleName : roles) {
            Optional<Role> optionalRole = roleRepository.findByNameIgnoreCase(roleName);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                if (Objects.isNull(userRoles))
                    userRoles = new HashSet<>();
                if (!userRoles.contains(role)) {
                    newRoles.add(role);
                }
            } else {
                invalidRoles.add(roleName);
            }
        }

        if (!invalidRoles.isEmpty()) {
            return new ApiResponse<>(FAILED, 400, "Invalid roles: " + String.join(", ", invalidRoles));
        }
        userRoles.addAll(newRoles);
        user.setRoles(userRoles);
        user.setModifiedBy(loggedInUser);
        user.setLastUpdated(Instant.now());
        AdminUser updatedUser = userRepository.save(user);

        return new ApiResponse<>(SUCCESS, 200, "Roles assigned successfully", updatedUser);
    }


    @Override
    public ApiResponse<Map<String, String>> createNewRoles(List<String> roles) {
        if (Objects.isNull(roles) || roles.isEmpty()) {
            throw new ApiException("At least one role is required for creation", 400);
        }
        try {
            long newRolesCount = roles.stream()
                    .map(this::createRole)
                    .filter(Optional::isPresent)
                    .count();
            String message = newRolesCount == 0 ? "Roles already exist or invalid role names"
                    : (newRolesCount == 1 ? "1 new role created" : newRolesCount + " new roles created");
            return new ApiResponse<>(newRolesCount == 0 ? FAILED : SUCCESS, newRolesCount == 0 ? 400 : 201, message);
        } catch (Exception e) {
            log.error("Error creating roles ==> {} :: {}", roles, e.getMessage());
            throw new ApiException("Failed to create roles ", 417);
        }
    }

    @Override
    public ApiResponse<Void> revokeUserRole(String email, String role) {
        if (isNullOrEmpty(email))
            return new ApiResponse<>(FAILED, 400, "Email is required");
        try {
            Optional<AdminUser> userOptional = userRepository.findAdminUserByEmailAddress(email);
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user", 401));
            if (userOptional.isEmpty())
                return new ApiResponse<>(FAILED, 404, String.format("Email %s not found ", email));
            AdminUser existingUser = userOptional.get();
            Set<Role> userRoles = existingUser.getRoles();
            if (isNullOrEmpty(role)) {
                //revoke all user roles except default
                userRoles = new HashSet<>();
                createRole(DEFAULT_ADMIN.name()).ifPresent(userRoles::add);
                existingUser.setRoles(userRoles);
                existingUser.setLastUpdated(Instant.now());
                existingUser.setModifiedBy(loggedInUser);
                userRepository.save(existingUser);
                return new ApiResponse<>(SUCCESS, 200, "all user roles successfully revoked");
            }

            if (!roleRepository.existsByNameIgnoreCase(role))
                return new ApiResponse<>(FAILED, 404, String.format("Role : %s does not exist", role));
            roleRepository.findByNameIgnoreCase(role).ifPresent(userRoles::remove);
            existingUser.setRoles(userRoles);
            existingUser.setLastUpdated(Instant.now());
            userRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS, 200, String.format("role : %s successfully revoked", role));

        } catch (Exception e) {
            log.info("Error occurred while revoking user roles ==> {}", e.getMessage());
            return new ApiResponse<>(FAILED, 417, "Could not revoke user roles ");
        }

    }

    @Override
    public ApiResponse<List<Role>> getAllRoles(int page, int size, String sortDirection) {
        List<Role> allRoles = roleRepository.findAll();
        if (allRoles.isEmpty())
            return new ApiResponse<>(FAILED, 404, "NO ROLE FOUND");
        if (!Objects.equals("asc", sortDirection) && !Objects.equals("desc", sortDirection)) {
            sortDirection = "asc";
        }
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), "dateCreated");
        Page<Role> roles = roleRepository.findAll(PageRequest.of(page, size, sort));
        return new ApiResponse<>(SUCCESS, 200, "available roles", roles.getContent());

    }

    private boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }


}
