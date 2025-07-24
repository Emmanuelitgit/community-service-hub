package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepo extends JpaRepository<Task, UUID> {
}
