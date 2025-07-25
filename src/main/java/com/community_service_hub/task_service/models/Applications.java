package com.community_service_hub.task_service.models;


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
@Table(name = "applications_tb")
public class Applications extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull(message = "applicant id cannot be null")
    private UUID applicantId;
    @NotNull(message = "applicant name cannot be null")
    private String applicantName;
    private String reasonForApplication;
    @NotNull(message = "phone cannot be null")
    private String phone;
    @NotNull(message = "email cannot be null")
    private String email;
}
