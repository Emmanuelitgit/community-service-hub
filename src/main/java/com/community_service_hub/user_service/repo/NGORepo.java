package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.models.NGO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NGORepo extends JpaRepository<NGO, UUID> {
    NGO findByEmail(String email);

    Optional<NGO> findNGOByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM ngo_tb", nativeQuery = true)
    Integer totalNGOs();

    @Query(value = "SELECT COUNT(*) FROM ngo_tb " +
            "WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalNGOsWithRange(@RequestParam("startDate") LocalDateTime startDate,
                               @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE is_approved IS FALSE", nativeQuery = true)
    Integer totalNGOSPendingApproval();

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE is_approved IS FALSE " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalNGOSPendingApprovalWithRange(@RequestParam("startDate") LocalDateTime startDate,
                                              @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE is_approved IS TRUE", nativeQuery = true)
    Integer totalApprovedNGOS();

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE is_approved IS TRUE " +
            "AND created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer totalApprovedNGOSWithRange(@RequestParam("startDate") LocalDateTime startDate,
                                       @RequestParam("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE created_at >= date_trunc('month', CURRENT_DATE)", nativeQuery = true)
    Integer totalCreatedNGOSForTheMonth();

    @Query(value = "SELECT COUNT(*) " +
            "FROM ngo_tb " +
            "WHERE created_at >= date_trunc('month', CAST(COALESCE(:dateParam, CURRENT_DATE) AS DATE)) " +
            "AND created_at < date_trunc('month', CAST(COALESCE(:dateParam, CURRENT_DATE) AS DATE)) + interval '1 month'",
            nativeQuery = true)
    Integer totalCreatedNGOSForTheMonth(@Param("dateParam") LocalDate dateParam);
}
