package com.community_service_hub.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class NGOPayload {
    private UUID id;
    @NotBlank(message = "Organization name cannot be null or empty")
    private String organizationName;
    private MultipartFile logo;
    private String state;
    private String country;
    private String city;
    private String website;
    private List<String> socialLinks;
    private MultipartFile certificate;
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot be null or empty")
    private String email;
    @NotBlank(message = "Password cannot be null or empty")
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    private String description;
    private String role;
}
