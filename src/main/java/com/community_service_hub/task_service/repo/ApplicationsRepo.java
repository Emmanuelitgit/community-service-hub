package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.Applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationsRepo extends JpaRepository<Applications, UUID> {

    @Query(value = "SELECT * FROM applications_tb WHERE applicant_id=:userId", nativeQuery = true)
    List<Applications> fetchUserApplications(@Param("userId") UUID userId);

    @Query(value = "SELECT ap.* FROM task_tb tk " +
            "JOIN applications_tb ap ON tk.id = ap.task_id " +
            "WHERE tk.posted_by = :NGOId", nativeQuery = true)
    List<Applications> fetchNGOApplications(@Param("NGOId") UUID NGOId);

}
