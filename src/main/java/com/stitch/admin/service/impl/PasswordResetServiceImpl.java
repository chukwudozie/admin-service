package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.payload.request.PasswordResetRequest;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.PasswordReset;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.PasswordResetRepository;
import com.stitch.admin.service.EmailService;
import com.stitch.admin.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.stitch.admin.utils.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AdminUserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Set<String> generatedOTPs = new HashSet<>();
    private static final long CLEAR_INTERVAL = 60;

    static {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(generatedOTPs::clear, CLEAR_INTERVAL, CLEAR_INTERVAL, TimeUnit.SECONDS);
    }
    @Override
    public ApiResponse<Void> sendPasswordChangeNotification(String email) {
        AdminUser adminUser = userRepository.findAdminUserByEmailAddress(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with email %s not found",email)));
        try {
            PasswordReset passwordReset = new PasswordReset();
            String otp = generate(6);
            passwordReset.setResetCode(otp);
            passwordReset.setEmailAddress(adminUser.getEmailAddress());
            passwordReset.setDateCreated(Instant.now());
            passwordReset.setLastUpdated(Instant.now());
            passwordReset.setPhoneNumber(adminUser.getPhoneNumber());
            String mailBody = RESET_PWD_OTP_EMAIL.replace("[LINK]",PASSWORD_RESET_LINK)
                    .replace("[OTP]",otp);
            boolean emailSent = emailService.sendPasswordResetOTP(mailBody, email, PASSWORD_RESET_TITLE);
            if(emailSent){
                updateRecord(passwordReset);
                return new ApiResponse<>(SUCCESS, 200,"Mail sent successfully for reset");
            }else {
                return new ApiResponse<>(FAILED, 417,"Failed to send mail");
            }

        }catch (Exception e){
            log.error("Error sending password change notification to user : {} :: {}",email,e.getMessage());
            throw new ApiException("Failed to send password reset OTP via mail ",417);
        }

    }

    private void updateRecord(PasswordReset passwordReset) {
        Optional<PasswordReset> userExists  = passwordResetRepository.findByEmailAddress(passwordReset.getEmailAddress());

        if(userExists.isPresent()){
            PasswordReset reset = userExists.get();
            reset.setResetCode(passwordReset.getResetCode());
            reset.setDateCreated(Instant.now());
            reset.setLastUpdated(Instant.now());
            reset.setModifiedBy(passwordReset.getEmailAddress());
            passwordResetRepository.save(reset);
        }else {
            passwordReset.setCreatedBy(passwordReset.getEmailAddress());
            passwordResetRepository.save(passwordReset);
        }
    }

    @Override
    public ApiResponse<Void> resetPassword(PasswordResetRequest passwordReset) {
        String email = passwordReset.getEmailAddress().trim();
        String otp = passwordReset.getResetCode().trim();
        String newPassword = passwordReset.getNewPassword().trim();
        AdminUser user = userRepository.findAdminUserByEmailAddress(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with email %s not found",email)));

        PasswordReset existingCode = passwordResetRepository.findByEmailAddressAndResetCode(email,otp)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("OTP '%s' does not exist for the user -> %s",
                        otp,email)));
        Instant createdTime = existingCode.getDateCreated();
        boolean isExpired = checkOTPExpiration(createdTime);

        if (isExpired){
            passwordResetRepository.delete(existingCode);
            return new ApiResponse<>(FAILED, HttpStatus.NOT_ACCEPTABLE.value(), "OTP has expired");
        }else{
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setLastUpdated(Instant.now());
            user.setModifiedBy(email);
            userRepository.save(user);
            passwordResetRepository.delete(existingCode);
            return new ApiResponse<>(SUCCESS,200, "Password successfully updated!");
        }
    }

    private boolean checkOTPExpiration(Instant createdTime) {
        if(Objects.isNull(createdTime))
            return true;
        Instant now = Instant.now();
        Duration duration = Duration.between(createdTime, now);
        return duration.toMinutes() >= 3;

    }

    private static String generateOTP(int length) {
        int max = (int) Math.pow(10, length);
        int min = (int) Math.pow(10, length - 1);
        Random random = new Random();
        int number = random.nextInt(max - min) + min;
        return String.valueOf(number);
    }

    private static String generate(int length){
        String otp;
        synchronized (generatedOTPs) {
            do {
                otp = generateOTP(6);
            } while (generatedOTPs.contains(otp));
            generatedOTPs.add(otp);
        }
        return otp;
    }

}
