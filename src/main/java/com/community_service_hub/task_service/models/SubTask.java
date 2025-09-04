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
@Table(name = "sub_task_tb")
public class SubTask extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank(message = "Parent task id cannot be null or empty")
    private UUID parentTaskId;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    @NotBlank(message = "Description cannot be null or empty")
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    @Column(length = 1000)
    private String description;
//    @NotNull(message = "assignee id cannot be null")
    private UUID assigneeId;
    private String status;
    @NotBlank(message = "Due date cannot be null or empty")
    private String dueDate;
}
