package com.community_service_hub.task_service.service;

import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface SubTaskService {
    ResponseEntity<ResponseDTO> createSubTask(SubTask subTask);
    ResponseEntity<ResponseDTO> getSubTasks();
    ResponseEntity<ResponseDTO> getSubTaskById(UUID subTaskId);
    ResponseEntity<ResponseDTO> updateSubTask(SubTask subTask);
    ResponseEntity<ResponseDTO> removeSubTask(UUID subTaskId);
    ResponseEntity<ResponseDTO> assignTask(UUID subTaskId, UUID assigneeId);
}
