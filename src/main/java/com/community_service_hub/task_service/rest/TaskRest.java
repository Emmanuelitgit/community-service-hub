package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.serviceImpl.TaskServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
public class TaskRest {

    private final TaskServiceImpl taskService;

    @Autowired
    public TaskRest(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createTask(@Valid Task task){
        return taskService.createTask(task);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getTasks(){
        return taskService.getTasks();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseDTO> getTaskById(@PathVariable UUID taskId){
        return taskService.getTaskById(taskId);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDTO> updateTask(Task task){
        return taskService.updateTask(task);
    }
}
