package com.community_service_hub.notification_service.rest;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.util.AppUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/otp")
@Tag(name = "Notification Management", description = "This controller is responsible for handling all kinds of notification")
public class OTPRest {

    private final OTPServiceImpl otpService;

    @Autowired
    public OTPRest(OTPServiceImpl otpService) {
        this.otpService = otpService;
    }

    @Operation(summary = "This endpoint is used to send an OTP code to user")
    @PostMapping("/send")
    public ResponseEntity<ResponseDTO> sendOTP(@RequestBody OTPPayload otpPayload){
        otpService.sendOtp(otpPayload);
        ResponseDTO response = AppUtils.getResponseDto("OTP sent", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "This endpoint is used to verify user OTP code")
    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO> verifyOTP(@RequestBody @Valid OTPPayload otpPayload){
        return otpService.verifyOtp(otpPayload);
    }

    @Operation(summary = "This endpoint is used to send a reset password link")
    @PostMapping("/send/password-reset-link")
    public ResponseEntity<ResponseDTO> sendResetPasswordLink(@RequestParam String email){
        log.info("In send password reset link controller->>>{}", email);
        otpService.sendResetPasswordLink(email);
        ResponseDTO responseDTO = AppUtils.getResponseDto("password link sent", HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}