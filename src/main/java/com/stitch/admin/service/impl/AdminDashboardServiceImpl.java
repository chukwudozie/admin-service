package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.dto.TransactionDto;
import com.stitch.admin.model.dto.UserDto;
import com.stitch.admin.model.entity.Transaction;
import com.stitch.admin.model.entity.UserEntity;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.TransactionRepository;
import com.stitch.admin.repository.UserEntityRepository;
import com.stitch.admin.service.AdminDashboardService;
import com.stitch.admin.specifications.TransactionSpecification;
import com.stitch.admin.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private final TransactionRepository transactionRepository;

    @Override
    public ApiResponse<Map<String, Object>> getUsersCount() {
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

    @Override
    public ApiResponse<Map<String, Object>> sumTransactionAmount(String status, String type, String paymentMode, String dateFilter, String startDate, String endDate, String monthYear) {

        try {
            Specification<Transaction> spec = Specification.where(TransactionSpecification.hasStatus(status))
                    .and(TransactionSpecification.hasType(type))
                    .and(TransactionSpecification.hasPaymentMode(paymentMode))
                    .and(parseDateFilter(dateFilter, startDate, endDate, monthYear));
            BigDecimal totalAmount = transactionRepository.findAll(spec)
                    .stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            return new ApiResponse<>(SUCCESS, 200, "Sum of transaction balance",Map.of("totalAmount",totalAmount));
        }catch (Exception e){
            log.error("Failed to get the transaction sum ==> {}",e.getMessage());
            return new ApiResponse<>(FAILED, 400, "Failed to get Sum of transaction amount");
        }
    }

    @Override
    public ApiResponse<List<TransactionDto>> getTransactions(int page, int size, String status, String type, String paymentMode, String dateFilter, String startDate, String endDate, String monthYear) {
        try {
            Pageable pageable = PageRequest.of(page,size);
            Specification<Transaction> spec = Specification.where(TransactionSpecification.hasStatus(status))
                    .and(TransactionSpecification.hasType(type))
                    .and(TransactionSpecification.hasPaymentMode(paymentMode))
                    .and(parseDateFilter(dateFilter, startDate, endDate, monthYear));
            Page<Transaction> transactions = transactionRepository.findAll(spec,pageable);
            List<TransactionDto> transactionDetails = transactions.stream().map(this::convertToDto).collect(Collectors.toList());
            return new ApiResponse<>(SUCCESS, 200, "List of transactions returned", transactionDetails);
        }catch (Exception e){
            log.error("Failed to get filtered transaction details ==> {}",e.getMessage());
            return new ApiResponse<>(FAILED, 417, "Failed to get transaction details");
        }
    }

    private Specification<Transaction> parseDateFilter(String dateFilter, String startDate, String endDate, String monthYear) {
        LocalDate now = LocalDate.now();
        Instant start;
        Instant end;

        switch (dateFilter) {
            case "today":
                start = now.atStartOfDay(ZoneId.systemDefault()).toInstant();
                end = start.plusSeconds(86400); // 24 hours
                return TransactionSpecification.isCreatedBetween(start, end);
            case "current_week":
                start = now.with(java.time.DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant();
                end = start.plusSeconds(604800); // 7 days
                return TransactionSpecification.isCreatedBetween(start, end);
            case "current_month":
                start = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                end = start.plusSeconds(now.lengthOfMonth() * 86400L); // Days in month
                return TransactionSpecification.isCreatedBetween(start, end);
            case "last_7_days":
                start = now.minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant();
                end = now.atStartOfDay(ZoneId.systemDefault()).toInstant();
                return TransactionSpecification.isCreatedBetween(start, end);
            case "last_30_days":
                start = now.minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant();
                end = now.atStartOfDay(ZoneId.systemDefault()).toInstant();
                return TransactionSpecification.isCreatedBetween(start, end);
            case "month_year":
                return TransactionSpecification.isCreatedInMonthYear(monthYear);
            case "since":
                if (startDate != null) {
                    start = Instant.parse(startDate);
                    return TransactionSpecification.isCreatedSince(start);
                }
                break;
            case "custom":
                if (startDate != null && endDate != null) {
                    start = Instant.parse(startDate);
                    end = Instant.parse(endDate);
                    return TransactionSpecification.isCreatedBetween(start, end);
                }
                break;
            default:
                break;
        }

        return Specification.where(null);
    }

    private UserDto convertToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity,userDto);
        return userDto;
    }

    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        BeanUtils.copyProperties(transaction,transactionDto);
        return transactionDto;
    }

}
