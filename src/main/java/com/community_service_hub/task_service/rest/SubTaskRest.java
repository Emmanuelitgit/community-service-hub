package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.serviceImpl.SubTaskServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sub-tasks")
public class SubTaskRest {

    private final SubTaskServiceImpl subTaskService;

    @Autowired
    public SubTaskRest(SubTaskServiceImpl subTaskService) {
        this.subTaskService = subTaskService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createSubTask(@Valid SubTask subTask){
        log.info("In create task controller->>>");
        return subTaskService.createSubTask(subTask);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getSubTasks(){
        log.info("In fetch all subtasks controller->>>");
        return subTaskService.getSubTasks();
    }

    @GetMapping("/{subTaskId}")
    public ResponseEntity<ResponseDTO> getSubTaskById(@PathVariable UUID subTaskId){
        log.info("In get subtask record by id controller->>>");
        return subTaskService.getSubTaskById(subTaskId);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateSubTask(SubTask subTask){
        log.info("In update subtask controller->>>");
        return subTaskService.updateSubTask(subTask);
    }

    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<ResponseDTO> removeSubTask(@PathVariable UUID subTaskId){
        log.info("In remove subtask controller->>>");
        return subTaskService.removeSubTask(subTaskId);
    }

    @PutMapping(value = "/assign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> assignTask(UUID subTaskId, UUID assigneeId){
        return subTaskService.assignTask(subTaskId, assigneeId);
    }
}