package com.stitch.admin.service.impl;

import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.ResourceNotFoundException;
import com.stitch.admin.model.dto.PasswordResetRequest;
import com.stitch.admin.model.entity.AdminUser;
import com.stitch.admin.model.entity.PasswordReset;
import com.stitch.admin.payload.response.ApiResponse;
import com.stitch.admin.repository.AdminUserRepository;
import com.stitch.admin.repository.PasswordResetRepository;
import com.stitch.admin.service.EmailService;
import com.stitch.admin.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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
                passwordResetRepository.save(passwordReset);
                return new ApiResponse<>(SUCCESS, 200,"Mail sent successfully for reset");
            }else {
                return new ApiResponse<>(FAILED, 417,"Failed to send mail");
            }

        }catch (Exception e){
            log.error("Error sending password change notification to user : {} :: {}",email,e.getMessage());
            throw new ApiException("Failed to send password reset OTP via mail ",417);
        }

    }

    @Override
    public ApiResponse<Void> resetPassword(PasswordResetRequest passwordReset) {
        return null;
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
