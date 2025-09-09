package com.community_service_hub.user_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ngo_tb")
public class NGO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank(message = "Organization name cannot be null")
    private String organizationName;
    private byte[] logo;
    private String state;
    private String country;
    private String city;
    private String website;
    private String socialLinks;
    private byte[] certificate;
    private String email;
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    @Column(length = 1000)
    private String description;
    private String role;
    private Boolean isApproved;
    private String phone;
    private UUID updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
