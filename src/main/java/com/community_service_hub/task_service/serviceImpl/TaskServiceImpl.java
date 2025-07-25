package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.TaskService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.repo.NGORepo;
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
    private final NGORepo ngoRepo;

    @Autowired
    public TaskServiceImpl(TaskRepo taskRepo, NGORepo ngoRepo) {
        this.taskRepo = taskRepo;
        this.ngoRepo = ngoRepo;
    }


    /**
     * @description This method is used to save a new task to the db
     * @param task the payload data of the task to be created
     * @return ResponseEntity containing the saved task record and status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createTask(Task task) {
        try{
            log.info("In create task method->>>{}", task);

            /**
             * check if NGO exist
             */
            Optional<NGO> ngo = ngoRepo.findById(task.getPostedBy());
            if (ngo.isEmpty()){
                log.info("NGO record cannot be found->>>{}", task.getPostedBy());
                ResponseDTO responseDTO = AppUtils.getResponseDto("NGO record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            }

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


    /**
     * @description This method is used to get all tasks from the db
     * @return ResponseEntity containing a list of tasks nd status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
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


    /**
     * @description This method is used to get task records given the task id.
     * @param taskId the id of the task to be retrieved
     * @return ResponseEntity containing the retrieved task record and status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
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


    /**
     * @description This method is used to update task records.
     * @param task the payload data of the task to be updated
     * @return ResponseEntity containing the saved task and status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateTask(Task task) {
        try {
            log.info("In update task by id method->>>");

            /**
             * loading task from db by id
             */
            Optional<Task> taskOptional = taskRepo.findById(task.getId());
            if (taskOptional.isEmpty()){
                log.info("task record cannot be found->>>{}", task.getId());
                ResponseDTO  response = AppUtils.getResponseDto("no task record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * building update payload
             */
            Task existingData = taskOptional.get();
            existingData.setName(task.getName() != null ? task.getName() : existingData.getName());
            existingData.setPostedBy(task.getPostedBy() != null ? task.getPostedBy() : existingData.getPostedBy());
            existingData.setDescription(task.getDescription() != null ? task.getDescription() : existingData.getDescription());
            existingData.setAddress(task.getAddress() != null ? task.getAddress() : existingData.getAddress());
            existingData.setLatitude(task.getLatitude() != null ? task.getLatitude() : existingData.getLatitude());
            existingData.setLongitude(task.getLongitude() != null ? task.getLongitude() : existingData.getLongitude());
            existingData.setDuration(task.getDuration() != null ? task.getDuration() : existingData.getDuration());
            existingData.setNumberOfPeopleNeeded(task.getNumberOfPeopleNeeded() != null ? task.getNumberOfPeopleNeeded() : existingData.getNumberOfPeopleNeeded());
            existingData.setStatus(task.getStatus() != null ? task.getStatus() : existingData.getStatus());

            /**
             * saving updated record
             */
            Task taskResponse = taskRepo.save(existingData);

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("task record updated", HttpStatus.OK, taskResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }

    }


    /**
     * @description This method is used to remove task records from the db.
     * @param taskId the id of the task to be removed
     * @return ResponseEntity containing the id of the task to be removed and status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
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