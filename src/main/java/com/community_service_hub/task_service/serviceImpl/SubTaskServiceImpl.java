package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.task_service.dto.TaskStatus;
import com.community_service_hub.task_service.models.SubTask;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.SubTaskRepo;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.SubTaskService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.UserRepo;
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
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepo subTaskRepo;
    private final TaskRepo taskRepo;
    private final UserRepo userRepo;

    @Autowired
    public SubTaskServiceImpl(SubTaskRepo subTaskRepo, TaskRepo taskRepo, UserRepo userRepo) {
        this.subTaskRepo = subTaskRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }


    /**
     * @description This method is used to save a new task to the db
     * @param subTask the payload data of the subtask to be created
     * @return ResponseEntity containing the saved subtask record and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createSubTask(SubTask subTask) {
        try{
            log.info("In create subtask method->>>{}", subTask);

            /**
             * checkin if parent task exist
             */
            Optional<Task> taskOptional = taskRepo.findById(subTask.getParentTaskId());
            if (taskOptional.isEmpty()){
                log.info("parent task record cannot be found->>>{}", subTask.getParentTaskId());
                ResponseDTO  response = AppUtils.getResponseDto("parent task record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * checking if assignee exist
             */
           if (subTask.getAssigneeId()!=null){
               Optional<User> userOptional = userRepo.findById(subTask.getAssigneeId());
               if (userOptional.isEmpty()){
                   log.info("assignee record cannot be found->>>{}", subTask.getAssigneeId());
                   ResponseDTO  response = AppUtils.getResponseDto("assignee record cannot be found", HttpStatus.NOT_FOUND);
                   return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
               }
               subTask.setStatus(TaskStatus.ASSIGNED.toString());
           }else {
               subTask.setStatus(TaskStatus.NOT_ASSIGNED.toString());
           }

            /**
             * saving record
             */
            SubTask subTaskResponse = subTaskRepo.save(subTask);


            /**
             * returning response i success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("subtask created", HttpStatus.CREATED, subTaskResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);


        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to get all subtasks from the db
     * @return ResponseEntity containing a list of subtasks nd status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getSubTasks() {
        try{
            log.info("In fetch all subtasks method->>>");

            /**
             * loading subtasks from db
             */
            List<SubTask> subTasks = subTaskRepo.findAll();
            if (subTasks.isEmpty()){
                log.info("no subtask record found->>>");
                ResponseDTO  response = AppUtils.getResponseDto("no subtask record found-", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("subtasks list", HttpStatus.OK, subTasks);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to get subtask records given the task id.
     * @param subTaskId the id of the subtask to be retrieved
     * @return ResponseEntity containing the retrieved subtask record and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getSubTaskById(UUID subTaskId) {
        try {
            log.info("In get subtask record by id method->>>{}", subTaskId);

            /**
             * checking if subtask exist by id
             */
            Optional<SubTask> subTaskOptional = subTaskRepo.findById(subTaskId);
            if (subTaskOptional.isEmpty()){
                log.info("subtask record cannot be found->>>{}", subTaskId);
                ResponseDTO  response = AppUtils.getResponseDto("subtask record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("subtask record retrieved", HttpStatus.OK, subTaskOptional.get());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to update subtask records.
     * @param subTask the payload data of the subtask to be updated
     * @return ResponseEntity containing the saved subtask and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateSubTask(SubTask subTask) {
        try {

            log.info("In update subtask record by id method->>>{}", subTask.getId());

            /**
             * checking if subtask exist by id
             */
            Optional<SubTask> subTaskOptional = subTaskRepo.findById(subTask.getId());
            if (subTaskOptional.isEmpty()){
                log.info("subtask record cannot be found->>>{}", subTask.getId());
                ResponseDTO  response = AppUtils.getResponseDto("subtask record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * building updated data payload
             */
            SubTask existingData = subTaskOptional.get();
            existingData.setDescription(subTask.getDescription()!=null? subTask.getDescription() : existingData.getDescription());
            existingData.setStatus(subTask.getStatus()!=null? subTask.getStatus() : existingData.getStatus());
            existingData.setParentTaskId(subTask.getParentTaskId()!=null?subTask.getParentTaskId():existingData.getParentTaskId());
            existingData.setName(subTask.getName()!=null?subTask.getName(): existingData.getName());

            /**
             * saving updated record
             */
            SubTask subTaskResponse = subTaskRepo.save(existingData);

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("subtask updated", HttpStatus.OK, subTaskResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to remove subtask records from the db.
     * @param subTaskId the id of the subtask to be removed
     * @return ResponseEntity containing the id of the subtask to be removed and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> removeSubTask(UUID subTaskId) {
        try{
            log.info("In update subtask record by id method->>>{}", subTaskId);

            /**
             * checking if subtask exist by id
             */
            Optional<SubTask> subTaskOptional = subTaskRepo.findById(subTaskId);
            if (subTaskOptional.isEmpty()){
                log.info("subtask record cannot be found->>>{}", subTaskId);
                ResponseDTO  response = AppUtils.getResponseDto("subtask record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * deleting record
             */
            subTaskRepo.deleteById(subTaskId);

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("subtask deleted", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to assign a subtask to a user who application has been approved
     * @param subTaskId the id of the subtask to be assigned to assignee
     * @return ResponseEntity containing the id of the subtask to be assigned and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> assignTask(UUID subTaskId, UUID assigneeId) {
        try{
            log.info("In assign task to user method->>>{}", assigneeId);

            /**
             * checking is task exist
             */
            Optional<SubTask> subTaskOptional = subTaskRepo.findById(subTaskId);
            if (subTaskOptional.isEmpty()){
                log.info("subtask record cannot be found->>>{}", subTaskId);
                ResponseDTO  response = AppUtils.getResponseDto("subtask record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * checking if assignee exist
             */
            Optional<User> userOptional = userRepo.findById(assigneeId);
            if (userOptional.isEmpty()){
                log.info("assignee record cannot be found->>>{}", subTaskId);
                ResponseDTO  response = AppUtils.getResponseDto("assignee record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * saving updated records
             */
            SubTask subTask = subTaskOptional.get();
            subTask.setAssigneeId(assigneeId);
            subTask.setStatus(TaskStatus.ASSIGNED.toString());
            SubTask subTaskResponse = subTaskRepo.save(subTask);

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("task assigned successfully", HttpStatus.OK, subTaskResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


}
