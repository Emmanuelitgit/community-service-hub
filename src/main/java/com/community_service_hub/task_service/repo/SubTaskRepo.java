package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubTaskRepo extends JpaRepository<SubTask, UUID> {

    @Query(value = "SELECT * FROM sub_task_tb WHERE assignee_id=:userId", nativeQuery = true)
    List<SubTask> fetchSubTasksForAssignee(@Param("userId") UUID userId);

    @Query(value = "SELECT * FROM sub_task_tb WHERE parent_task_id=:parentTaskId", nativeQuery = true)
    List<SubTask> fetchSubTasksByParentTaskId(@Param("parentTaskId") UUID parentTaskId);
}
