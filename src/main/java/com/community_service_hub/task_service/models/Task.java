package com.community_service_hub.task_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_tb")
public class Task extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull(message = "posted by cannot be null")
    private UUID postedBy;
    @NotNull(message = "name cannot be null")
    private String name;
    private String category;
    @NotNull(message = "description cannot be null")
    @Size(max = 255, message = "Description must be at most 255 characters long")
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String startDate;
    @NotNull(message = "number of people needed cannot be null")
    private Integer numberOfPeopleNeeded;
    private String status;
}
