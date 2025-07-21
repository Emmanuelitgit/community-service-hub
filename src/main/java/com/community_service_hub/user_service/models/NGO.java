package com.community_service_hub.user_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_tb")
public class NGO extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
}
