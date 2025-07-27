package com.community_service_hub.user_service.service;

import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.RoleSetup;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface NGOService {
    ResponseEntity<ResponseDTO> saveNGO(NGO ngo);
    ResponseEntity<ResponseDTO> updateNGO(NGO ngo, UUID ngoId);
    ResponseEntity<ResponseDTO> findNGOById(UUID ngoId);
    ResponseEntity<ResponseDTO> deleteNGO(UUID ngoId);
    ResponseEntity<ResponseDTO> getNGOs();
    ResponseEntity<ResponseDTO> approveOrRejectNGO(UUID NGOId, Boolean status);
}
