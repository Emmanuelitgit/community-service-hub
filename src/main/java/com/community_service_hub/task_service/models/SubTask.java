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
@Table(name = "sub_task_tb")
public class SubTask extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull(message = "parent task id cannot be null")
    private UUID parentTaskId;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "description cannot be null")
    private String description;
//    @NotNull(message = "assignee id cannot be null")
    private UUID assigneeId;
    private String status;
}
