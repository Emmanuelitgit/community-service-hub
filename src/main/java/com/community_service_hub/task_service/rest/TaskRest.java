package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.serviceImpl.TaskServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(
        name = "Task Management",
        description = "Main tasks are created by NGOs to request help from users. " +
                "These tasks serve as the foundation for assigning work to users. " +
                "Interested users can apply for main tasks, and upon approval, the NGO may create subtasks and assign them to those users."
)
public class TaskRest {

    private final TaskServiceImpl taskService;

    @Autowired
    public TaskRest(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "This endpoint is used to creat a new task")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createTask(@Valid Task task){
        return taskService.createTask(task);
    }

    @Operation(summary = "This endpoint is used to fetch all tasks")
    @GetMapping
    public ResponseEntity<ResponseDTO> getTasks(
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lng", required = false) Double lng,
            @RequestParam(name = "km", required = false) Integer km,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "search", required = false) String search){
        Pageable pageable = PageRequest.of(page, size);
        return taskService.getTasks(lat, lng, km, pageable, search);
    }

    @Operation(summary = "This endpoint is used to get a task record by id")
    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseDTO> getTaskById(@PathVariable UUID taskId){
        return taskService.getTaskById(taskId);
    }

    @Operation(summary = "This endpoint is used to update a task record")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDTO> updateTask(Task task){
        return taskService.updateTask(task);
    }

    @Operation(summary = "This endpoint is used to fetch all tasks for current logged in NGO")
    @GetMapping("/NGO")
    public ResponseEntity<ResponseDTO> fetchTasksForNGO(){
        return taskService.fetchTasksForNGO();
    }

    @Operation(summary = "This endpoint is used to deleted task record by id")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ResponseDTO> removeTask(@PathVariable UUID taskId){
        return taskService.removeTask(taskId);
    }
}
