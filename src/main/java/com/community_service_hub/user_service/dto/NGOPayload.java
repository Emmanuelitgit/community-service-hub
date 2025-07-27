package com.community_service_hub.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class NGOPayload {
    private UUID id;
    @NotNull(message = "organization name cannot be null")
    private String organizationName;
    private MultipartFile logo;
    private String state;
    private String country;
    private String city;
    private String website;
    private List<String> socialLinks;
    private MultipartFile certificate;
    @Email(message = "invalid email")
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "password cannot be null")
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
    private String role;
}
