package com.community_service_hub.user_service.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserLocationRepo extends JpaRepository<com.community_service_hub.user_service.models.UserLocation, UUID> {
}
