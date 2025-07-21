package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, UUID> {
    UserRole findByUserId(UUID userId);
}
