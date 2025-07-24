package com.community_service_hub.task_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    private UUID id;
    @NotNull(message = "posted by cannot be null")
    private UUID postedBy;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "description cannot be null")
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String duration;
    @NotNull(message = "number of people needed cannot be null")
    private Integer numberOfPeopleNeeded;
    private String status;
}
