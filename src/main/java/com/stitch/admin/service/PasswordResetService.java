package com.stitch.admin.service;

import com.stitch.admin.model.dto.PasswordResetRequest;
import com.stitch.admin.payload.response.ApiResponse;

public interface PasswordResetService {

    ApiResponse<Void> sendPasswordChangeNotification(String email);

    ApiResponse<Void> resetPassword(PasswordResetRequest passwordReset);
}
