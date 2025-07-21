package com.community_service_hub.user_service.service;


import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {

    ResponseEntity<ResponseDTO> createUser(UserPayloadDTO userPayloadDTO);
    ResponseEntity<ResponseDTO> getUsers();
    ResponseEntity<ResponseDTO> getUserById(UUID userId);
    ResponseEntity<ResponseDTO> updateUser(UserPayloadDTO user, UUID userId);
    ResponseEntity<ResponseDTO> removeUser(UUID userId);
}
