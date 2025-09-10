package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.Applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationsRepo extends JpaRepository<Applications, UUID> {

    @Query(value = "SELECT * FROM applications_tb WHERE applicant_id=:userId ORDER BY created_at DESC", nativeQuery = true)
    List<Applications> fetchApplicationsForUser(@Param("userId") UUID userId);

    @Query(value = "SELECT ap.* FROM task_tb tk " +
            "JOIN applications_tb ap ON tk.id = ap.task_id " +
            "WHERE tk.posted_by = :NGOId ORDER BY created_at DESC", nativeQuery = true)
    List<Applications> fetchApplicationsForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM applications_tb", nativeQuery = true)
    Integer totalApplications();

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalApplicationsWithRange(@RequestParam("startDate") LocalDateTime startDate,
                                       @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM applications_tb WHERE applicant_id=:userId", nativeQuery = true)
    Integer totalApplicationsForApplicant(@Param("userId") UUID userId);

    @Query(value = "SELECT COUNT(*) FROM applications_tb WHERE applicant_id=:userId " +
                    "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalApplicationsForApplicantWithRange(@Param("userId") UUID userId,
                                                   @RequestParam("startDate") LocalDateTime startDate,
                                                   @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM task_tb tk " +
            "JOIN applications_tb ap ON tk.id = ap.task_id " +
            "WHERE tk.posted_by = :NGOId", nativeQuery = true)
    Integer totalApplicationsForNGO(@Param("NGOId") UUID NGOId);

    @Query(value = "SELECT COUNT(*) FROM task_tb tk " +
            "JOIN applications_tb ap ON tk.id = ap.task_id " +
            "WHERE tk.posted_by = :NGOId " +
            "AND ap.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalApplicationsForNGOWithRange(@Param("NGOId") UUID NGOId,
                                             @RequestParam("startDate") LocalDateTime startDate,
                                             @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
                   "WHERE status='APPROVED' AND applicant_id=:applicantId", nativeQuery = true)
    Integer totalApprovedApplicationsForApplicant(@Param("applicantId") UUID applicantId);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE status='APPROVED' AND applicant_id=:applicantId " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalApprovedApplicationsForApplicantWithRange(@Param("applicantId") UUID applicantId,
                                                           @RequestParam("startDate") LocalDateTime startDate,
                                                           @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE status='REJECTED' AND applicant_id=:applicantId", nativeQuery = true)
    Integer totalRejectedApplicationsForApplicant(@Param("applicantId") UUID applicantId);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE status='REJECTED' AND applicant_id=:applicantId " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalRejectedApplicationsForApplicantWithRange(@RequestParam("applicantId") UUID applicantId,
                                                           @RequestParam("startDate") LocalDateTime startDate,
                                                           @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE status='PENDING' AND applicant_id=:applicantId", nativeQuery = true)
    Integer totalPendingApplicationsForApplicant(@Param("applicantId") UUID applicantId);

    @Query(value = "SELECT COUNT(*) FROM applications_tb " +
            "WHERE status='PENDING' AND applicant_id=:applicantId " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalPendingApplicationsForApplicantWithRange(@RequestParam("applicantId") UUID applicantId,
                                                          @RequestParam("startDate") LocalDateTime startDate,
                                                          @RequestParam("endDate") LocalDateTime endDate);

    @Query(value ="SELECT count(*) FROM applications_tb " +
            "WHERE applicant_id=:applicantId AND task_id=:taskId", nativeQuery = true)
    Integer findApplicationsByApplicantIdAndTaskIdExists(@RequestParam("applicantId") UUID applicantId,
                                                         @RequestParam("taskId") UUID taskId);

}
