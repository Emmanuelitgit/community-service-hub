package com.community_service_hub.task_service.repo;

import com.community_service_hub.task_service.models.Applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationsRepo extends JpaRepository<Applications, UUID> {
}
