package com.community_service_hub.task_service.service;

import com.community_service_hub.task_service.models.Applications;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ApplicationsService {
    ResponseEntity<ResponseDTO> createApplication(Applications applications);
    ResponseEntity<ResponseDTO> getApplications();
    ResponseEntity<ResponseDTO> getApplicationById(UUID applicationId);
    ResponseEntity<ResponseDTO> updateApplication(Applications applications);
    ResponseEntity<ResponseDTO> removeApplication(UUID applicationId);
}
