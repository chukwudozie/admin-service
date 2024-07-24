package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.UserEntity;
import com.stitch.admin.payload.request.PasswordUpdateRequest;
import com.stitch.admin.payload.request.UpdateUserRequest;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.UserEntityRepository;
import com.stitch.admin.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.stitch.admin.utils.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final AdminUserRepository adminUserRepository;
    private final UserEntityRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public ApiResponse<AdminUser> updateUserDetails(UpdateUserRequest request, String email) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }

        boolean isPermittedUser = checkForUserIdentity(email);
        if (!isPermittedUser){
            throw new ApiException("Logged in user not permitted to edit details ",406);
        }
        try {
            AdminUser existingUser = adminUserRepository.findAdminUserByEmailAddress(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+email));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));

            BeanUtils.copyProperties(request,existingUser,getNullPropertyNames(request));
            existingUser.setLastUpdated(Instant.now());
            existingUser.setModifiedBy(loggedInUser);
            System.err.println("Existing user updated ---- "+existingUser);
            AdminUser updatedUser = adminUserRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, "User update successful",updatedUser);
        }catch (Exception e){
            log.error("Exception encountered updating user ---> {}",e.getMessage());
            throw new ApiException("Failed to update user details ",400);
        }


    }

    @Override
    public ApiResponse<Void> updateUserPassword(PasswordUpdateRequest request) {

        if(Objects.isNull(request)){
            return new ApiResponse<>(FAILED,400,"invalid password update request");
        }
        try {
            String loggedInUser = getLoggedInUser()
                    .orElseThrow(() -> new ApiException("Could not validate logged in user",401));
            AdminUser existingUser = adminUserRepository.findAdminUserByEmailAddress(loggedInUser)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+loggedInUser));

            String defaultPassword = value(existingUser.getPassword());
            String oldPassword = value(request.getOldPassword());
            String newPassword = value(request.getNewPassword());
            String confirmPassword = value(request.getConfirmPassword());

            if(!encoder.matches(oldPassword, defaultPassword)){
                throw new ApiException("old password does not match with default password for user",417);
            }

            if (!Objects.equals(newPassword, confirmPassword)){
                throw new ApiException("new password field must match confirm password field",417);
            }
            existingUser.setPassword(encoder.encode(newPassword));
            existingUser.setLastUpdated(Instant.now());
            existingUser.setLastPasswordChange(Instant.now());
            existingUser.setModifiedBy(loggedInUser);
            adminUserRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, "Password successfully updated");

        } catch (ApiException e){
            log.error("Exception occurred updating user password => {}",e.getMessage());
            return new ApiResponse<>(FAILED, e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("Error occurred updating user password => {}",e.getMessage());
            return new ApiResponse<>(FAILED, 406, "Failed to update user password");
        }

    }

    @Override
    public ApiResponse<Void> deactivateAdmin(String email) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }
        try {
            AdminUser existingUser = adminUserRepository.findAdminUserByEmailAddress(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+email));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));
            existingUser.setActivated(false);
            existingUser.setEnabled(false);
            existingUser.setLastUpdated(Instant.now());
            existingUser.setModifiedBy(loggedInUser);
            adminUserRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, String.format("User with email %s has been deactivated",email));
        }catch (Exception e){
            log.error("Exception occurred deactivating user  ---> {}",e.getMessage());
            throw new ApiException("Failed to deactivate user ",400);
        }

    }

    @Override
    public ApiResponse<Void> activateAdmin(String email) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }
        try {
            AdminUser existingUser = adminUserRepository.findAdminUserByEmailAddress(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No Admin found with email "+email));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));
            existingUser.setActivated(true);
            existingUser.setEnabled(true);
            existingUser.setModifiedBy(loggedInUser);
            existingUser.setLastUpdated(Instant.now());
            adminUserRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, String.format("Admin with email %s has been activated",email));
        }catch (Exception e){
            log.error("Exception occurred activating admin  ---> {}",e.getMessage());
            throw new ApiException("Failed to activate admin ",400);
        }

    }

    @Override
    public ApiResponse<Void> deactivateUser(String email) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }
        try {
            UserEntity existingUser = userRepository.findByEmailAddressIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+email));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));
            existingUser.setEnabled(false);
            existingUser.setLastUpdated(Instant.now());
            existingUser.setModifiedBy(loggedInUser);
            userRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, String.format("User with email %s has been deactivated",email));
        }catch (Exception e){
            log.error("Exception occurred deactivating user  ---> {}",e.getMessage());
            throw new ApiException("Failed to deactivate user ",400);
        }
    }

    @Override
    public ApiResponse<Void> activateUser(String email) {
        if(Objects.isNull(email) || email.isEmpty()){
            throw new ApiException("Email is required for update",400);
        }
        try {
            UserEntity existingUser = userRepository.findByEmailAddressIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException("No user found with email "+email));
            String loggedInUser = getLoggedInUser().orElseThrow(() -> new ApiException("Could not validate logged in user",401));
            existingUser.setEnabled(true);
            existingUser.setLastUpdated(Instant.now());
            existingUser.setModifiedBy(loggedInUser);
            userRepository.save(existingUser);
            return new ApiResponse<>(SUCCESS,200, String.format("User with email %s has been activated",email));
        }catch (Exception e){
            log.error("Exception occurred activating user  ---> {}",e.getMessage());
            throw new ApiException("Failed to activate user ",400);
        }
    }


    private String value(Object o){
        if (Objects.isNull(o))
            return EMPTY;
        return o.toString();
    }



    private boolean checkForUserIdentity( String email) {
        try {
            String validEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            if (Objects.nonNull(validEmail) && !validEmail.isEmpty()){
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
