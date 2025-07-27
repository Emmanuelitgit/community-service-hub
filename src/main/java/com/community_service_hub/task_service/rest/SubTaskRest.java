package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.serviceImpl.SubTaskServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "This endpoint is used to create a new subtask")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createSubTask(@Valid SubTask subTask){
        log.info("In create task controller->>>");
        return subTaskService.createSubTask(subTask);
    }

    @Operation(summary = "This endpoint is used to fetch all subtasks")
    @GetMapping
    public ResponseEntity<ResponseDTO> getSubTasks(){
        log.info("In fetch all subtasks controller->>>");
        return subTaskService.getSubTasks();
    }

    @Operation(summary = "This endpoint is used to get a subtask record by id")
    @GetMapping("/{subTaskId}")
    public ResponseEntity<ResponseDTO> getSubTaskById(@PathVariable UUID subTaskId){
        log.info("In get subtask record by id controller->>>");
        return subTaskService.getSubTaskById(subTaskId);
    }

    @Operation(summary = "This endpoint is used to update a subtask")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateSubTask(SubTask subTask){
        log.info("In update subtask controller->>>");
        return subTaskService.updateSubTask(subTask);
    }

    @Operation(summary = "This endpoint is used to remove a subtask given the task id")
    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<ResponseDTO> removeSubTask(@PathVariable UUID subTaskId){
        log.info("In remove subtask controller->>>");
        return subTaskService.removeSubTask(subTaskId);
    }

    @Operation(summary = "This endpoint is used to assign a subtask to a user")
    @PutMapping(value = "/assign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> assignTask(UUID subTaskId, UUID assigneeId){
        return subTaskService.assignTask(subTaskId, assigneeId);
    }

    @Operation(summary = "This endpoint is used to get all subtasks assigned to the current logged in user")
    @GetMapping("/assignee")
    public ResponseEntity<ResponseDTO> fetchSubTasksForAssignee(){
        return subTaskService.fetchSubTasksForAssignee();
    }

    @Operation(summary = "This endpoint is used to fetch subtasks by parent task id")
    @GetMapping("/tasks/{parentTaskId}")
    public ResponseEntity<ResponseDTO> fetchSubTasksByParentTaskId(@PathVariable UUID parentTaskId){
        return subTaskService.fetchSubTasksByParentTaskId(parentTaskId);
    }
}