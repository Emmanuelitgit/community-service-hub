package com.community_service_hub.task_service.service;

import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface TaskService {
    ResponseEntity<ResponseDTO> createTask(Task task);
    ResponseEntity<ResponseDTO> getTasks();
    ResponseEntity<ResponseDTO> getTaskById(UUID taskId);
    ResponseEntity<ResponseDTO> updateTask(Task task);
    ResponseEntity<ResponseDTO> removeTask(UUID taskId);
    public ResponseEntity<ResponseDTO> fetchTasksForNGO();
}
