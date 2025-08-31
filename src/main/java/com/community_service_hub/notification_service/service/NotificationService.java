package com.community_service_hub.notification_service.service;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    public void sendOtp(OTPPayload otpPayload);
    public ResponseEntity<ResponseDTO> verifyOtp(OTPPayload otpPayload);
}
