package com.community_service_hub.task_service.models;


import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private UUID applicantId;
    @NotBlank(message = "Applicant name cannot be null or empty")
    private String applicantName;
    private String reasonForApplication;
    @NotBlank(message = "Phone cannot be null or empty")
    private String phone;
    @NotBlank(message = "Email cannot be null or empty")
    private String email;
    private String status;
    @NotNull(message = "Task id cannot be null or empty")
    private UUID taskId;
}
