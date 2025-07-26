package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubTaskRepo extends JpaRepository<SubTask, UUID> {
}
