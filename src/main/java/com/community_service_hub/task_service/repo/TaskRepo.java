package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.dto.TaskProjection;
import com.community_service_hub.task_service.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepo extends JpaRepository<Task, UUID> {

    @Query(value = "SELECT * FROM task_tb WHERE posted_by=:NGOId", nativeQuery = true)
    List<Task> fetchTasksForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = " SELECT" +
            "        t.id AS id," +
            "        t.posted_by AS postedBy," +
            "        t.name AS name," +
            "        t.category AS category," +
            "        t.description AS description," +
            "        t.address AS address," +
            "        t.latitude AS latitude," +
            "        t.longitude AS longitude," +
            "        t.start_date AS startDate," +
            "        t.number_of_people_needed AS numberOfPeopleNeeded," +
            "        t.status AS status," +
            "        t.remaining_people_needed," +
            "        ng.organization_name," +
            "        ng.email," +
            "        ng.address AS organizationAddress," +
            "        ng.city," +
            "        ng.id AS ngoId," +
            "        ng.state," +
            "        ng.country," +
            "        ng.website," +
            "        ng.latitude AS ngoLatitude," +
            "        ng.longitude AS ngoLongitude," +
            "        ng.description AS ngoDescription," +
            "        ng.social_links" +
            "    FROM task_tb t" +
            "    JOIN ngo_tb ng ON ng.id=t.posted_by", nativeQuery = true)
    List<TaskProjection> fetchTasksWithNGOs();

    @Query(value = "SELECT COUNT(*) FROM task_tbl", nativeQuery = true)
    Integer totalTasks();

    @Query(value = "SELECT COUNT(*) FROM tasks_tbl WHERE posted_by=:userId", nativeQuery = true)
    Integer totalTasksForLoggedInUser(@Param("userId") UUID userId);
}
