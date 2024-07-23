package com.stitch.admin.service.impl;

import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.UserEntityRepository;
import com.stitch.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public ApiResponse<List<UserDto>> fetchUsers(int page, int size, String type, String enabled, String name) {
        return null;
    }
}
