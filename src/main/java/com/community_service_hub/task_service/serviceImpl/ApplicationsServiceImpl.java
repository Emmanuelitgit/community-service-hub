package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.exception.NotFoundException;
import com.community_service_hub.notification_service.dto.ApplicationConfirmationDTO;
import com.community_service_hub.notification_service.serviceImpl.NotificationServiceImpl;
import com.community_service_hub.task_service.models.Applications;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.ApplicationsRepo;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.ApplicationsService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.Activity;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.ActivityRepo;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
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
public class ApplicationsServiceImpl implements ApplicationsService {

    private final ApplicationsRepo applicationsRepo;
    private final NGORepo ngoRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;
    private final AppUtils appUtils;
    private final ActivityRepo activityRepo;
    private final NotificationServiceImpl notificationService;

    @Autowired
    public ApplicationsServiceImpl(ApplicationsRepo applicationsRepo, NGORepo ngoRepo, UserRepo userRepo, TaskRepo taskRepo, AppUtils appUtils, ActivityRepo activityRepo, NotificationServiceImpl notificationService) {
        this.applicationsRepo = applicationsRepo;
        this.ngoRepo = ngoRepo;
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
        this.appUtils = appUtils;
        this.activityRepo = activityRepo;
        this.notificationService = notificationService;
    }


    /**
     * @description This method is used to get all applications from the db
     * @return ResponseEntity containing a list of applications nd status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> getApplications(){
        try {
            log.info("In find all applications method->>>");

            /**
             * loading applications from db
             */
            List<Applications> applications = applicationsRepo.findAll();
            if (applications.isEmpty()){
                log.info("no application record found");
                ResponseDTO responseDTO = AppUtils.getResponseDto("no application record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application list retrieved", HttpStatus.OK, applications);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to save a new application in to the db
     * @param applications the payload data of the application to be created
     * @return ResponseEntity containing the retrieved user record and status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createApplication(Applications applications) {
        try {
            log.info("In create application method->>>{}", applications);

            /**
             * checking if applicant exist in the system
             */
            Optional<NGO> ngoOptional = ngoRepo.findById(applications.getApplicantId());
            Optional<User> userOptional = userRepo.findById(applications.getApplicantId());
            if (ngoOptional.isEmpty() && userOptional.isEmpty()){
                log.info("applicant record cannot be found->>>{}", applications.getApplicantId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking if task exist by id
             */
            Optional<Task> taskOptional = taskRepo.findById(applications.getTaskId());
            if (taskOptional.isEmpty()){
                log.info("task record cannot be found->>>{}", applications.getTaskId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking if task is closed for application
             */
            if (taskOptional.get().getStatus().equalsIgnoreCase(AppConstants.CLOSED)){
                log.info("Task record is closed for application->>>{}", taskOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("Task record is closed for application", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            /**
             * updating task records
             */
            Task task = taskOptional.get();
            if (task.getRemainingPeopleNeeded()==1){
                task.setRemainingPeopleNeeded(0);
                task.setStatus(AppConstants.CLOSED);
            }else {
                task.setRemainingPeopleNeeded(task.getRemainingPeopleNeeded()-1);
            }

            /**
             * saving record
             */
            Task taskResponse = taskRepo.save(task);

            /**
             * saving application record
             */
            applications.setStatus(AppConstants.PENDING);
            Applications applicationsResponse  = applicationsRepo.save(applications);

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(taskResponse.getId())
                    .activity("Created New Application")
                    .entityName(taskResponse.getName())
                    .build();
            activityRepo.save(activity);

            /**
             * sending application confirmation message
             */
            ApplicationConfirmationDTO confirmationDTO = ApplicationConfirmationDTO
                    .builder()
                    .task(taskResponse.getName())
                    .email(applicationsResponse.getEmail())
                    .category(taskResponse.getCategory())
                    .location(taskResponse.getAddress())
                    .status(applicationsResponse.getStatus())
                    .startDate(taskResponse.getStartDate())
                    .userEmail(userOptional.get().getEmail())
                    .build();
            notificationService.sendApplicationConfirmation(confirmationDTO);
            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application created", HttpStatus.CREATED, applicationsResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to get user records given the user id.
     * @param applicationId the id of the application to be retrieved
     * @return ResponseEntity containing the retrieved application record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getApplicationById(UUID applicationId) {
        try{
            log.info("In get application by id method->>>{}", applicationId);

            Optional<Applications> applicationsOptional = applicationsRepo.findById(applicationId);
            if (applicationsOptional.isEmpty()){
                log.info("application record cannot be found->>>{}", applicationId);
                ResponseDTO responseDTO = AppUtils.getResponseDto("application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(applicationsOptional.get().getApplicantId(), applicationsOptional.get().getTaskId());
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to application->>>{}", applicationsOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to application", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application list retrieved", HttpStatus.OK, applicationsOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to update user records.
     * @param applications the payload data of the application to be updated
     * @return ResponseEntity containing the saved application and status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateApplication(Applications applications) {
        try{
            log.info("In update application by id method->>>{}", applications.getId());

            /**
             * checking if the application requesting to update exist
             */
            Optional<Applications> applicationsOptional = applicationsRepo.findById(applications.getId());
            if (applicationsOptional.isEmpty()){
                log.info("application record cannot be found->>>{}", applications.getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking if applicant exist in the system
             */
            Optional<NGO> ngoOptional = ngoRepo.findById(applications.getApplicantId());
            Optional<User> userOptional = userRepo.findById(applications.getApplicantId());
            if (ngoOptional.isEmpty() && userOptional.isEmpty()){
                log.info("applicant record cannot be found->>>{}", applications.getApplicantId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(applicationsOptional.get().getApplicantId(), applicationsOptional.get().getTaskId());
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to application->>>{}", applicationsOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to application", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            Applications existingData = applicationsOptional.get();
            existingData.setApplicantId(applications.getApplicantId() != null ? applications.getApplicantId() : existingData.getApplicantId());
            existingData.setApplicantName(applications.getApplicantName() != null ? applications.getApplicantName() : existingData.getApplicantName());
            existingData.setReasonForApplication(applications.getReasonForApplication() != null ? applications.getReasonForApplication() : existingData.getReasonForApplication());
            existingData.setPhone(applications.getPhone() != null ? applications.getPhone() : existingData.getPhone());
            existingData.setEmail(applications.getEmail() != null ? applications.getEmail() : existingData.getEmail());

            /**
             * saving updated record
             */
            Applications applicationsResponse = applicationsRepo.save(existingData);

            /**
             * retrieving task details to add to activity log
             */
            Optional<Task> taskOptional = taskRepo.findById(applicationsResponse.getTaskId());
            if (taskOptional.isEmpty()){
                log.info("task record cannot be found->>>{}", applications.getTaskId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(taskOptional.get().getId())
                    .activity("Updated Application Record")
                    .entityName(taskOptional.get().getName())
                    .build();
            activityRepo.save(activity);

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application record updated", HttpStatus.OK, applicationsResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to remove user records from the db.
     * @param applicationId the id of the application to be removed
     * @return ResponseEntity containing a success message and status information
     * @auther Emmanuel Yidana
     * @createdAt 25th july April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> removeApplication(UUID applicationId) {
        try{
            log.info("In delete application by id method->>>{}", applicationId);

            Optional<Applications> applicationsOptional = applicationsRepo.findById(applicationId);
            if (applicationsOptional.isEmpty()){
                log.info("application record cannot be found->>>{}", applicationId);
                ResponseDTO responseDTO = AppUtils.getResponseDto("application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(applicationsOptional.get().getApplicantId(), applicationsOptional.get().getTaskId());
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to application->>>{}", applicationsOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to application", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * deleting record
             */
            applicationsRepo.deleteById(applicationsOptional.get().getId());

            /**
             * retrieving task details to add to activity log
             */
            Optional<Task> taskOptional = taskRepo.findById(applicationsOptional.get().getTaskId());
            if (taskOptional.isEmpty()){
                log.info("task record cannot be found->>>{}", applicationsOptional.get().getTaskId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(taskOptional.get().getId())
                    .activity("Deleted Application Record")
                    .entityName(taskOptional.get().getName())
                    .build();
            activityRepo.save(activity);

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application record deleted", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to update application status to either(APPROVED or REJECTED).
     * @param applicationId the id of the application to be updated
     * @return ResponseEntity containing the updated application record and status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
    @PreAuthorize("hasAnyAuthority('NGO')")
    @Override
    public ResponseEntity<ResponseDTO> updateApplicationStatus(String status, UUID applicationId){
        try{
            log.info("In update application status method->>>{}", applicationId);

            /**
             * checking if application exist by id
             */
            Optional<Applications> applicationsOptional = applicationsRepo.findById(applicationId);
            if (applicationsOptional.isEmpty()){
                log.info("application record cannot be found->>>{}", applicationId);
                ResponseDTO responseDTO = AppUtils.getResponseDto("application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * checking user authorization levels
             */
            Boolean isUserAuthorized = appUtils.isUserAuthorized(null, applicationsOptional.get().getTaskId());
            if (Boolean.FALSE.equals(isUserAuthorized)){
                log.info("User not authorized to application->>>{}", applicationsOptional.get().getId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("User not authorized to application", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * updating status
             */
            Applications existingData = applicationsOptional.get();
            if (status.equalsIgnoreCase(AppConstants.APPROVED)){
                existingData.setStatus(AppConstants.APPROVED);
            } else if (status.equalsIgnoreCase(AppConstants.REJECTED)) {
                existingData.setStatus(AppConstants.REJECTED);
            }else {
                log.info("application status provided cannot be found->>>{}", status);
                ResponseDTO responseDTO = AppUtils.getResponseDto("application status provided cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            Applications applicationResponse = applicationsRepo.save(existingData);

            /**
             * retrieving task details to add to activity log
             */
            Optional<Task> taskOptional = taskRepo.findById(applicationResponse.getTaskId());
            if (taskOptional.isEmpty()){
                log.info("task record cannot be found->>>{}", applicationResponse.getTaskId());
                ResponseDTO responseDTO = AppUtils.getResponseDto("applicant record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(taskOptional.get().getId())
                    .activity(applicationResponse.getStatus().equalsIgnoreCase(AppConstants.APPROVED)?"Approved Application" : "Disapproved Application")
                    .entityName(taskOptional.get().getName())
                    .build();
            activityRepo.save(activity);

            /**
             * returning response if success
             */
            log.info("Application status updated to->>>{}", status);
            ResponseDTO responseDTO = AppUtils.getResponseDto("application status updated", HttpStatus.OK, applicationResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to fetch applications for current logged in (NGO or USER)
     * @return ResponseEntity containing list of applications and status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> fetchUserApplications() {
        try {
            log.info("In fetch user applications status method->>>{}", AppUtils.getAuthenticatedUserId());

            /**
             * checking if application exist by id
             */
            List<Applications> applicationsForUser = applicationsRepo.fetchApplicationsForUser(UUID.fromString(AppUtils.getAuthenticatedUserId()));
            List<Applications> applicationsForNGO = applicationsRepo.fetchApplicationsForNGO(UUID.fromString(AppUtils.getAuthenticatedUserId()));
            if (applicationsForUser.isEmpty() && applicationsForNGO.isEmpty()){
                log.info("application record cannot be found->>>{}", UUID.fromString(AppUtils.getAuthenticatedUserId()));
                ResponseDTO responseDTO = AppUtils.getResponseDto("Application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }


            /**
             * this what is returned to the UI when the logged-in user is applicant
             */
            List<Object> response = new ArrayList<>();

            if (!applicationsForUser.isEmpty()){
                applicationsForUser.forEach((app)->{
                    Map<String, Object> application = new HashMap<>();
                    application.put("applicantName", app.getApplicantName());
                    application.put("applicantId", app.getApplicantId());
                    application.put("email", app.getEmail());
                    application.put("reasonForApplication", app.getReasonForApplication());
                    application.put("status", app.getStatus());
                    application.put("phone", app.getPhone());
                    application.put("id", app.getId());

                    /**
                     * fetching task details
                     */
                    Optional<Task> taskOptional = taskRepo.findById(app.getTaskId());
                    if (taskOptional.isEmpty()){
                        log.info("Task record cannot be found->>>{}", UUID.fromString(AppUtils.getAuthenticatedUserId()));
                        throw new NotFoundException("Task record cannot be found");
                    }
                    application.put("task", taskOptional.get());

                    response.add(application);
                });
            }

            /**
             * returning response if success
             */
            if (!applicationsForUser.isEmpty()){
                ResponseDTO responseDTO = AppUtils.getResponseDto("application list retrieved", HttpStatus.OK, response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }else {
                ResponseDTO responseDTO = AppUtils.getResponseDto("application list retrieved", HttpStatus.OK, applicationsForNGO);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}



