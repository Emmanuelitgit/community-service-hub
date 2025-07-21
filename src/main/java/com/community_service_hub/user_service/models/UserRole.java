package com.community_service_hub.user_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_role_tb")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID roleId;
}
