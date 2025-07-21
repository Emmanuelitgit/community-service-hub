package com.community_service_hub.user_service.service;

import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.RoleSetup;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface RoleSetupService {

    ResponseEntity<ResponseDTO> saveRole(RoleSetup roleSetup);
    ResponseEntity<ResponseDTO> updateRole(RoleSetup roleSetup, UUID roleId);
    ResponseEntity<ResponseDTO> findRoleById(UUID roleId);
    ResponseEntity<ResponseDTO> deleteRole(UUID roleId);
    ResponseEntity<ResponseDTO> getRoles();
}
