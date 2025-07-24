package com.community_service_hub.user_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ngo_tb")
public class NGO extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String organizationName;
    private byte[] logo;
    private String state;
    private String city;
    private String website;
    private String socialLinks;
    private byte[] certificate;
    private String email;
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
    private String role;
}
