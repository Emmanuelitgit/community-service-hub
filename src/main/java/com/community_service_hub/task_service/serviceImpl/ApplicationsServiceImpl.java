package com.community_service_hub.task_service.serviceImpl;

import com.community_service_hub.task_service.dto.ApplicationStatus;
import com.community_service_hub.task_service.dto.TaskStatus;
import com.community_service_hub.task_service.models.Applications;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.ApplicationsRepo;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.task_service.service.ApplicationsService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
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
public class ApplicationsServiceImpl implements ApplicationsService {

    private final ApplicationsRepo applicationsRepo;
    private final NGORepo ngoRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    @Autowired
    public ApplicationsServiceImpl(ApplicationsRepo applicationsRepo, NGORepo ngoRepo, UserRepo userRepo, TaskRepo taskRepo) {
        this.applicationsRepo = applicationsRepo;
        this.ngoRepo = ngoRepo;
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
    }


    /**
     * @description This method is used to get all applications from the db
     * @return ResponseEntity containing a list of applications nd status information
     * @auther Emmanuel Yidana
     * @createdAt 25th July 2025
     */
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
             * updating task records
             */
            Task task = taskOptional.get();
            if (task.getNumberOfPeopleNeeded()==1){
                task.setNumberOfPeopleNeeded(0);
                task.setStatus(TaskStatus.CLOSED.toString());
            }else {
                task.setNumberOfPeopleNeeded(task.getNumberOfPeopleNeeded()-1);
            }
            taskRepo.save(task);

            /**
             * saving application record
             */
            applications.setStatus(ApplicationStatus.PENDING.toString());
            Applications applicationsResponse  = applicationsRepo.save(applications);

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
             * deleting record
             */
            applicationsRepo.deleteById(applicationsOptional.get().getId());

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
             * updating status
             */
            Applications existingData = applicationsOptional.get();
            if (status.equalsIgnoreCase(ApplicationStatus.APPROVED.toString())){
                existingData.setStatus(ApplicationStatus.APPROVED.toString());
            } else if (status.equalsIgnoreCase(ApplicationStatus.REJECTED.toString())) {
                existingData.setStatus(ApplicationStatus.REJECTED.toString());
            }else {
                log.info("application status provided cannot be found->>>{}", applicationId);
                ResponseDTO responseDTO = AppUtils.getResponseDto("application status provided cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            Applications applicationResponse = applicationsRepo.save(existingData);


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
                ResponseDTO responseDTO = AppUtils.getResponseDto("application record cannot be found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * returning response if success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("application list retrieved", HttpStatus.OK, !applicationsForUser.isEmpty()?applicationsForUser:applicationsForNGO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
