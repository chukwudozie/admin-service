package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.model.entity.UserEntity;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.UserEntityRepository;
import com.stitch.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.stitch.admin.utils.Constants.FAILED;
import static com.stitch.admin.utils.Constants.SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserEntityRepository userEntityRepository;

    @Override
    public ApiResponse<Map<String, Object>> getCount() {
        try {
            long totalCount  = userEntityRepository.count();
            long countActive = userEntityRepository.countUserEntitiesByEnabledTrue();
            long countInactive = totalCount - countActive;
            long customerCount = userEntityRepository.countUserEntitiesByRole_NameIgnoreCase("customer");
            long activeCustomerCount = userEntityRepository.countUserEntitiesByEnabledTrueAndRole_NameIgnoreCase("customer");
            long inactiveCustomerCount = customerCount - activeCustomerCount;

            long vendorCount = userEntityRepository.countUserEntitiesByRole_NameIgnoreCase("vendor");
            long activeVendorCount = userEntityRepository.countUserEntitiesByEnabledTrueAndRole_NameIgnoreCase("vendor");
            long inactiveVendorCount = vendorCount - activeVendorCount;

            Map<String,Object> response = Map.of(
                    "totalUsers",totalCount,
                    "activeUsers",countActive,
                    "inactiveUsers",countInactive,
                    "totalCustomers",customerCount,
                    "activeCustomers",activeCustomerCount,
                    "inactiveCustomers",inactiveCustomerCount,
                    "totalVendors",vendorCount,
                    "activeVendors", activeVendorCount,
                    "inactiveVendors",inactiveVendorCount);
            return new ApiResponse<>(SUCCESS, 200, "success",response);

        }catch (Exception e){
            log.error("Exception encountered getting dashboard counts --> {}",e.getMessage());
            return new ApiResponse<>(FAILED, 400, "Failed to get dashboard counts");
        }

    }

    @Override
    public ApiResponse<List<UserDto>> fetchUsers(int page, int size, String roleName, String enabled, String name) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Specification<UserEntity> spec = Specification.where(UserSpecification.hasRoleName(roleName))
                    .and(UserSpecification.isEnabled(enabled))
                    .and(UserSpecification.hasName(name));
            Page<UserEntity> userPage = userEntityRepository.findAll(spec, pageable);
            List<UserDto> userDtos = userPage.stream().map(this::convertToDto).collect(Collectors.toList());

            return new ApiResponse<>(SUCCESS,200,"list of users",userDtos);
        }catch (Exception e){
            log.error("Failed to fetch list of users --> {}",e.getMessage());
            return new ApiResponse<>(FAILED,400,"Failed to fetch list of users");
        }
    }

    @Override
    public ApiResponse<UserDto> retrieveUserByEmail(String email) {
        if (Objects.isNull(email) || email.trim().isEmpty()){
            throw new ApiException("User email cannot be empty or null ",400);
        }
        UserEntity existingUser = userEntityRepository.findByEmailAddressIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("No user exists with email %s ",email)));
        UserDto user = convertToDto(existingUser);
        return new ApiResponse<>(SUCCESS,200,"User retrieved successfully", user);
    }

    private UserDto convertToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity,userDto);
        return userDto;
    }

}
