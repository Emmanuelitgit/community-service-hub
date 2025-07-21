package com.community_service_hub.notification_service.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "otp_tb")
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int otpCode;
    private UUID userId;
    private ZonedDateTime expireAt;
    private boolean status;
}
