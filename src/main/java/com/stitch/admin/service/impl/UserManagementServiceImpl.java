package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.security.JwtTokenUtils;
import com.stitch.admin.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final AdminUserRepository userRepository;
    private final JwtTokenUtils utils;
    private final TokenBlacklistService blacklistService;

    @Override
    public ApiResponse<AdminUser> updateUserDetails(UpdateUserRequest request, String email, HttpServletRequest servletRequest) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }

        boolean isPermittedUser = checkForUserIdentity(servletRequest, email);
        if (!isPermittedUser){
            throw new ApiException("Logged in user not permitted to edit details ",406);
        }
        try {
            AdminUser existingUser = userRepository.findAdminUserByEmailAddress(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+email));

            BeanUtils.copyProperties(request,existingUser,getNullPropertyNames(request));
            existingUser.setLastUpdated(Instant.now());
            System.err.println("Existing user updated ---- "+existingUser);
            AdminUser updatedUser = userRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, "User update successful",updatedUser);
        }catch (Exception e){
            log.error("Exception encountered updating user ---> {}",e.getMessage());
            throw new ApiException("Failed to update user details ",400);
        }


    }

    private boolean checkForUserIdentity(HttpServletRequest servletRequest, String email) {
        try {
            String auth = servletRequest.getHeader("Authorization");
            if (Objects.nonNull(auth) && auth.startsWith("Bearer ") && !blacklistService.isTokenBlacklisted(auth.substring(7))){
                String jwtToken = auth.substring(7);
                String  validEmail = utils.extractUsernameFromToken(jwtToken);
                return Objects.equals(validEmail,email);
            }else
                return false;
        }catch (Exception e){
            log.error("Exception occurred during token user validation ==> {}",e.getMessage());
            return false;
        }

    }


    private String[] getNullPropertyNames(Object source){
        final BeanWrapper wrapper = new BeanWrapperImpl(source);
        PropertyDescriptor [] descriptors = wrapper.getPropertyDescriptors();
        List<String> nullProperties = new ArrayList<>();
        for (PropertyDescriptor descriptor: descriptors) {
            System.err.println(descriptor.getName());
            System.err.println(descriptor.getPropertyType());
            System.err.println(descriptor.getValue("emailAddress"));
            if (wrapper.getPropertyValue(descriptor.getName()) == null) {
                nullProperties.add(descriptor.getName());
            }
        }
        return nullProperties.toArray(new String[0]);
    }
}
