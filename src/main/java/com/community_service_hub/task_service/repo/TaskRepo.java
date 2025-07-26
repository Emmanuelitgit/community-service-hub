package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepo extends JpaRepository<Task, UUID> {

    @Query(value = "SELECT * FROM task_tb WHERE posted_by=:NGOId", nativeQuery = true)
    List<Task> fetchTasksForNGO(@Param("NGOId") UUID NGOId);
}
