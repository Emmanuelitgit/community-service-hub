package com.community_service_hub.notification_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPPayload {
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "otp code cannot be null")
    private int otpCode;
}
