package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.TaskService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;

    @Autowired
    public TaskServiceImpl(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }


    /**
     * @description This method is used to save a new task to the db
     * @param task the payload data of the task to be created
     * @return ResponseEntity containing the saved task record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createTask(Task task) {
        try{
            log.info("In create task method->>>{}", task);
            /**
             * saving task record
             */
            Task taskResponse = taskRepo.save(task);

            /**
             * returning response if successfully
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("Task created successfully", HttpStatus.CREATED, taskResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getTasks() {
        try {
            log.info("In fetch all tasks method->>>");

            /**
             * loading tasks from db
             */
            List<Task> tasks = taskRepo.findAll();
            if (tasks.isEmpty()){
                log.info("no task record found->>>");
                ResponseDTO  response = AppUtils.getResponseDto("no task record found-", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("tasks list", HttpStatus.OK, tasks);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getTaskById(UUID taskId) {
        try {
            log.info("In get task by id method->>>");

            /**
             * loading task from db by id
             */
            Optional<Task> task = taskRepo.findById(taskId);
            if (task.isEmpty()){
                log.info("no task record found->>>{}", taskId);
                ResponseDTO  response = AppUtils.getResponseDto("no task record found-", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("task record retrieved", HttpStatus.OK, task.get());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updateTask(Task task, UUID taskId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> removeTask(UUID taskId) {
        try {
            log.info("In delete task by id method->>>");

            /**
             * loading task from db by id
             */
            Optional<Task> task = taskRepo.findById(taskId);
            if (task.isEmpty()){
                log.info("no task record found->>>{}", taskId);
                ResponseDTO  response = AppUtils.getResponseDto("no task record found-", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("task record retrieved", HttpStatus.OK, task.get());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
