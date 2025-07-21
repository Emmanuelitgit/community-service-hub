package com.community_service_hub.user_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_location_tb")
public class UserLocation extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;
    private UUID userId;
    @NotNull(message = "user address cannot be null")
    private String address;
    @NotNull(message = "latitude cannot be null")
    private Double latitude;
    @NotNull(message = "longitude cannot be null")
    private Double longitude;
}
