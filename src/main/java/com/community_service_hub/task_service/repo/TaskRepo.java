package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.dto.TaskProjection;
import com.community_service_hub.task_service.models.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    List<TaskProjection> fetchTasksWithNGOs(Pageable pageable);


    @Query(value = "SELECT" +
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
            "    JOIN ngo_tb ng ON ng.id=t.posted_by" +
            "    WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(t.latitude)) * " +
            "    cos(radians(t.longitude) - radians(:lng))+ sin(radians(:lat)) * " +
            "    sin(radians(t.latitude)))) <= :km", nativeQuery = true)
    List<TaskProjection> fetchNearByTasksWithNGOs(Double lat, Double lng, Integer km, Pageable pageable);


    @Query(value = "SELECT COUNT(*) FROM task_tb",
            nativeQuery = true)
    Integer totalTasks();

    @Query(value = "SELECT COUNT(*) FROM task_tb " +
            "WHERE created_at BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    Integer totalTasksWithRange(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE posted_by=:NGOId " +
                    "WHERE (:startDate=null OR created_at >= CAST(:startDate AS TIMESTAMP))" +
                    "(:endDate=null OR created_at <= CAST(:endDate AS TIMESTAMP))", nativeQuery = true)
    Integer totalTasksForNGO(@RequestParam("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE posted_by=:NGOId " +
                    "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalTasksForNGOWithRange(@RequestParam("NGOId") UUID NGOId,
                             @RequestParam("startDate") LocalDateTime startDate,
                             @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE created_at >= date_trunc('month', CURRENT_DATE)", nativeQuery = true)
    Integer totalCreatedTasksForTheMonth();

    @Query(value = "SELECT COUNT(*) FROM task_tb " +
                   "WHERE created_at >= date_trunc('month', CURRENT_DATE)" +
                   "AND posted_by=:NGOId", nativeQuery = true)
    Integer totalCreatedTasksForTheMonthForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='OPEN'", nativeQuery = true)
    Integer totalActiveTasks();

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='OPEN' " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalActiveTasksWithRange(@RequestParam("startDate") LocalDateTime startDate,
                                      @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='CLOSED'", nativeQuery = true)
    Integer totalCompletedTasks();

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='CLOSED' " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalCompletedTasksWithRange(@RequestParam("startDate") LocalDateTime startDate,
                                         @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='OPEN' AND posted_by=:NGOId", nativeQuery = true)
    Integer totalActiveTasksForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='OPEN' AND posted_by=:NGOId " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalActiveTasksForNGOWithRange(@Param("NGOId") UUID NGOId,
                                            @RequestParam("startDate") LocalDateTime startDate,
                                            @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='CLOSED' AND posted_by=:NGOId", nativeQuery = true)
    Integer totalCompletedTasksForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM task_tb WHERE status='CLOSED' AND posted_by=:NGOId " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalCompletedTasksForNGOWithRange(@Param("NGOId") UUID NGOId,
                                               @RequestParam("startDate") LocalDateTime startDate,
                                               @RequestParam("endDate") LocalDateTime endDate);
}
