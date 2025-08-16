package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.models.NGO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NGORepo extends JpaRepository<NGO, UUID> {
    NGO findByEmail(String email);

    Optional<NGO> findNGOByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM ngo_tb", nativeQuery = true)
    Integer totalNGOs();

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE is_approved IS FALSE", nativeQuery = true)
    Integer totalNGOSPendingApproval();

    @Query(value = "SELECT COUNT(*) FROM ngo_tb WHERE created_at >= date_trunc('month', CURRENT_DATE)", nativeQuery = true)
    Integer totalCreatedNGOSForTheMonth();
}
