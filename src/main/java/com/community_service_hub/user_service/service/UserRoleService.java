package com.community_service_hub.user_service.service;

import com.community_service_hub.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserRoleService {
    ResponseEntity<ResponseDTO> saveUserRole(UUID userId, UUID roleId);
}
