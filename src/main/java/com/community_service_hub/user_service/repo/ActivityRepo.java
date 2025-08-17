package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepo extends JpaRepository<Activity, UUID> {

    @Query(value = "SELECT * FROM activity_tb " +
                   "WHERE created_by=:userId " +
                   "ORDER BY created_at DESC LIMIT 5",nativeQuery = true)
    List<Activity> getRecentActivitiesByUserId(@Param("userId") UUID userId);
}
