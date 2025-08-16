package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.task_service.dto.TaskProjection;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.TaskService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.exception.ServerException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.util.AppConstants;
import com.community_service_hub.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;
    private final NGORepo ngoRepo;
    private final AppUtils appUtils;

    @Autowired
    public TaskServiceImpl(TaskRepo taskRepo, NGORepo ngoRepo, AppUtils appUtils) {
        this.taskRepo = taskRepo;
        this.ngoRepo = ngoRepo;
        this.appUtils = appUtils;
    }


    /**
     * @description This method is used to save a new task to the db
     * @param task the payload data of the task to be created
     * @return ResponseEntity containing the saved task record and status information
     * @auther Emmanuel Yidana
     * @createdAt 24h July 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'NGO')")
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
             * checking if NGO has been approved or not
             */
            if (Boolean.FALSE.equals(ngo.get().getIsApproved())){
                log.info("NGO not approved");
                ResponseDTO responseDTO = AppUtils.getResponseDto("NGO not approved yet and cannot create task at the moment", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * saving task record
             */
            task.setRemainingPeopleNeeded(task.getNumberOfPeopleNeeded());
            task.setStatus(AppConstants.OPEN);
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
            List<TaskProjection> tasks = taskRepo.fetchTasksWithNGOs();
            if (tasks.isEmpty()){
                log.info("no task record found->>>");
                ResponseDTO  response = AppUtils.getResponseDto("no task record found-", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            List<Object> list = new ArrayList<>();

            tasks.forEach((task)->{
                Map<String, String> NGO = new HashMap<>();
                Map<String, Object> objectMap = new HashMap<>();

                /**
                 * building NGO object response
                 */
                NGO.put("organizationName", task.getOrganizationName());
                NGO.put("city", task.getCity());
                NGO.put("email", task.getEmail());
                NGO.put("address", task.getOrganizationAddress());
                NGO.put("state", task.getState());
                NGO.put("country", task.getCountry());
                NGO.put("website", task.getWebsite());
                NGO.put("latitude", task.getNgoLatitude()!=null?task.getNgoLatitude().toString():null);
                NGO.put("longitude", task.getNgoLatitude()!=null?task.getNgoLatitude().toString():null);
                NGO.put("socialLinks", task.getSocialLinks());
                NGO.put("description", task.getDescription());
                NGO.put("logo", "https://community-service-hub-eif3.onrender.com/api/v1/ngo/logo/"+task.getNgoId());
                NGO.put("certificate", "https://community-service-hub-eif3.onrender.com/api/v1/ngo/certificate/"+task.getNgoId());

                /**
                 * building the general object response(Task and NGO)
                 */
                objectMap.put("NGO", NGO);
                objectMap.put("name", task.getName());
                objectMap.put("description", task.getDescription());
                objectMap.put("address", task.getAddress());
                objectMap.put("longitude", task.getLongitude());
                objectMap.put("latitude", task.getLatitude());
                objectMap.put("numberOfPeopleNeeded", task.getNumberOfPeopleNeeded());
                objectMap.put("startDate", task.getStartDate());
                objectMap.put("category", task.getCategory());
                objectMap.put("id", task.getId());
                objectMap.put("remainingPeopleNeeded", task.getRemainingPeopleNeeded());

                /**
                 * adding to list
                 */
                list.add(objectMap);
            });

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("tasks list", HttpStatus.OK, list);
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'NGO')")
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
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(taskOptional.get().getPostedBy(), null);
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to task->>>{}", taskOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to task", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
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
            existingData.setStartDate(task.getStartDate() != null ? task.getStartDate() : existingData.getStartDate());
            existingData.setStatus(task.getStatus() != null ? task.getStatus() : existingData.getStatus());
            existingData.setCategory(task.getCategory()!=null? task.getCategory() : existingData.getCategory());
            if (task.getNumberOfPeopleNeeded()!=null){
                existingData.setNumberOfPeopleNeeded(task.getNumberOfPeopleNeeded());
                Integer remaining = task.getNumberOfPeopleNeeded()-existingData.getNumberOfPeopleNeeded();
                existingData.setRemainingPeopleNeeded(remaining+ existingData.getRemainingPeopleNeeded());
            }

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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'NGO')")
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
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(task.get().getPostedBy(), null);
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to task->>>{}", task.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to task", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * removing record
             */
            taskRepo.deleteById(task.get().getId());

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("task record deleted", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to get all tasks for current logged in NGO
     * @return ResponseEntity containing a list of tasks and status information
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    @Override
    @PreAuthorize("hasAnyAuthority('NGO')")
    public ResponseEntity<ResponseDTO> fetchTasksForNGO(){
        try {
            log.info("In fetch tasks for NGO method->>>");

            UUID NGOId = UUID.fromString(AppUtils.getAuthenticatedUserId());

            /**
             * loading tasks from db
             */
            List<Task> tasks = taskRepo.fetchTasksForNGO(NGOId);
            if (tasks.isEmpty()){
                log.info("no task record found for NGO->>>{}", NGOId);
                ResponseDTO  response = AppUtils.getResponseDto("no task record found", HttpStatus.NOT_FOUND);
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
}