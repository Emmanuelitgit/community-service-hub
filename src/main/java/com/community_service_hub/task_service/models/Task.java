package com.community_service_hub.task_service.models;

import com.community_service_hub.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotNull(message = "Posted by cannot be null or empty")
    private UUID postedBy;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    private String category;
    @NotBlank(message = "Description cannot be null or empty")
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    @Column(length = 1000)
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String startDate;
    @NotNull(message = "Number of people needed cannot be null or empty")
    private Integer numberOfPeopleNeeded;
    private Integer remainingPeopleNeeded;
    private String status;
}
