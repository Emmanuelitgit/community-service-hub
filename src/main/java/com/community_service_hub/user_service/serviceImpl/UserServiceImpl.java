package com.community_service_hub.user_service.serviceImpl;


import com.community_service_hub.config.AppProperties;
import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.NotificationServiceImpl;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.ApplicationsRepo;
import com.community_service_hub.task_service.repo.SubTaskRepo;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.user_service.dto.*;
import com.community_service_hub.exception.NotFoundException;
import com.community_service_hub.exception.ServerException;
import com.community_service_hub.user_service.models.*;
import com.community_service_hub.user_service.models.UserRole;
import com.community_service_hub.user_service.repo.*;
import com.community_service_hub.user_service.service.UserService;
import com.community_service_hub.util.AppConstants;
import com.community_service_hub.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final DTOMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleServiceImpl userRoleServiceImpl;
    private final RoleSetupRepo roleSetupRepo;
    private final RoleSetupServiceImpl roleSetupServiceImpl;
    private final UserRoleRepo userRoleRepo;
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final NotificationServiceImpl otpService;
    private final NGORepo ngoRepo;
    private final TaskRepo taskRepo;
    private final ApplicationsRepo applicationsRepo;
    private final SubTaskRepo subTaskRepo;
    private final ActivityRepo activityRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, DTOMapper dtoMapper, PasswordEncoder passwordEncoder, UserRoleServiceImpl userRoleServiceImpl, RoleSetupRepo roleSetupRepo, RoleSetupServiceImpl roleSetupServiceImpl, UserRoleRepo userRoleRepo, RestTemplate restTemplate, AppProperties appProperties, NotificationServiceImpl otpService, NGORepo ngoRepo, TaskRepo taskRepo, ApplicationsRepo applicationsRepo, SubTaskRepo subTaskRepo, SubTaskRepo subTaskRepo1, ActivityRepo activityRepo) {
        this.userRepo = userRepo;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRoleServiceImpl = userRoleServiceImpl;
        this.roleSetupRepo = roleSetupRepo;
        this.roleSetupServiceImpl = roleSetupServiceImpl;
        this.userRoleRepo = userRoleRepo;
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
        this.otpService = otpService;
        this.ngoRepo = ngoRepo;
        this.taskRepo = taskRepo;
        this.applicationsRepo = applicationsRepo;
        this.subTaskRepo = subTaskRepo1;
        this.activityRepo = activityRepo;
    }

    /**
     * @description This method is used to save user to the db
     * @param userPayloadDTO the payload data of the user to be added
     * @return ResponseEntity containing the saved user record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> createUser(UserPayloadDTO userPayloadDTO) {
       try {
           log.info("In create user method:->>>>>>{}", userPayloadDTO);

           /**
            * check if payload is null
            */
           if (userPayloadDTO  == null){
               ResponseDTO  response = AppUtils.getResponseDto("user payload cannot be null", HttpStatus.BAD_REQUEST);
               return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
           }

           /**
            * check if email already exist
            */
           Optional<User> userEmailExist =  userRepo.findUserByEmail(userPayloadDTO.getEmail());
           NGO ngo = ngoRepo.findByEmail(userPayloadDTO.getEmail());
           if (userEmailExist.isPresent() || ngo != null){
               ResponseDTO  response = AppUtils.getResponseDto("email already exist", HttpStatus.ALREADY_REPORTED);
               return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
           }

           /**
            * hashing user password
            */
           userPayloadDTO.setPassword(passwordEncoder.encode(userPayloadDTO.getPassword()));
           User user = dtoMapper.toUserEntity(userPayloadDTO);

           /**
            * saving user record
            */
           userPayloadDTO.setRole(userPayloadDTO.getRole().toUpperCase());
           User userResponse = userRepo.save(user);

           /**
            * update activity log
            */
           Activity activity = Activity
                   .builder()
                   .entityId(userResponse.getId())
                   .activity("Account Creation")
                   .entityName(userResponse.getName())
                   .build();
           activityRepo.save(activity);

           /**
            * sending an otp email notification to user
            */
           log.info("About to send an otp code to user->>>");
           OTPPayload otpPayload = OTPPayload
                   .builder()
                   .email(userResponse.getEmail())
                   .build();

           otpService.sendOtp(otpPayload);

           /**
            * returning response if everything is successfully
            */
           UserDTO userDTOResponse = DTOMapper.toUserDTO(user);
           ResponseDTO  response = AppUtils.getResponseDto("user record added successfully", HttpStatus.CREATED, userDTOResponse);
           return new ResponseEntity<>(response, HttpStatus.CREATED);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           throw new ServerException(e.getMessage());
       }
    }

    /**
     * @description This method is used to get all users from the db
     * @return ResponseEntity containing a list of users nd status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUsers() {
       try{
           log.info("In get all users method:->>>>>>");
           /**
            * loading user details from the db
            */
           List<UserDTOProjection> users = userRepo.getUsersDetails();
           if (users.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * returning response if successfully
            */
           ResponseDTO  response = AppUtils.getResponseDto("users records fetched successfully", HttpStatus.OK, users);
           return new ResponseEntity<>(response, HttpStatus.OK);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to get user records given the user id.
     * @param userId the id of the user to be retrieved
     * @return ResponseEntity containing the retrieved user record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUserById(UUID userId) {
       try{
           log.info("In get user by id method:->>>>>>");
           /**
            * loading user details from db
            */
           UserDTOProjection user = userRepo.getUsersDetailsByUserId(userId);
           if (user == null){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * returning response if successfully
            */
           ResponseDTO  response = AppUtils.getResponseDto("user records fetched successfully", HttpStatus.OK, user);
           return new ResponseEntity<>(response, HttpStatus.OK);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to update user records.
     * @param userPayload the payload data of the user to be updated
     * @return ResponseEntity containing the saved user and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateUser(UserUpdateDTO userPayload) {
        try{
            log.info("In update user method:->>>>>>{}", userPayload);

            /**
             * checking if user record exist by id
             */
            User existingData = userRepo.findById(userPayload.getId())
                    .orElseThrow(()-> new NotFoundException("user record not found"));

            /**
             * building payload details to be saved
             */
            existingData.setEmail(userPayload.getEmail() !=null ? userPayload.getEmail() : existingData.getEmail());
            existingData.setName(userPayload.getName() !=null ? userPayload.getName() : existingData.getName());
            existingData.setPhone(userPayload.getPhone() !=null ? userPayload.getPhone() : existingData.getPhone());
            existingData.setAddress(userPayload.getAddress()!=null?userPayload.getAddress():existingData.getAddress());
            User userResponse = userRepo.save(existingData);

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(userResponse.getId())
                    .activity("Updated Account Details")
                    .entityName(userResponse.getName())
                    .build();
            activityRepo.save(activity);

            /**
             * returning response if successfully
             */
            UserDTO userDTOResponse = DTOMapper.toUserDTO(userResponse);
            log.info("user records updated successfully:->>>>>>");
            ResponseDTO  response = AppUtils.getResponseDto("user records updated successfully", HttpStatus.OK, userDTOResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to remove user records from the db.
     * @param userId the id of the user to be removed
     * @return ResponseEntity containing the id of the user to be removed and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> removeUser(UUID userId) {
        try {
            log.info("In remove user method:->>>{}", userId);
            /**
             * loading user details from db
             */
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                log.info("user record cannot be found->>>{}", userId);
                ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            userRepo.deleteById(userId);

            log.info("user records removed successfully:->>>{}", userId);

            /**
             * update activity log
             */
            Activity activity = Activity
                    .builder()
                    .entityId(userOptional.get().getId())
                    .activity("Deleted Account")
                    .entityName(userOptional.get().getName())
                    .build();
            activityRepo.save(activity);

            /**
             * returning response if successfully
             */
            ResponseDTO  response = AppUtils.getResponseDto("user record removed successfully", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to reset user password.
     * @param credentials the payload data containing email and password
     * @return ResponseEntity
     * @auther Emmanuel Yidana
     * @createdAt 22nd July 2025
     */
    public ResponseEntity<ResponseDTO> resetPassword(Credentials credentials){
        try {

            /**
             * loading user data from the db
             */
            Optional<User> user = userRepo.findUserByEmail(credentials.getEmail());
            NGO ngo = ngoRepo.findByEmail(credentials.getEmail());
            if (user.isEmpty() && ngo == null){
                log.info("no user record found with the email provided->>>{}", credentials.getEmail());
                ResponseDTO response = AppUtils.getResponseDto("no user record found with the email provided", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * updating and hashing the password if it is user
             */
            if (user.isPresent()){
                User existingUser = user.get();
                existingUser.setPassword(passwordEncoder.encode(credentials.getPassword()));
                User userResponse = userRepo.save(existingUser);

                /**
                 * update activity log
                 */
                Activity activity = Activity
                        .builder()
                        .entityId(userResponse.getId())
                        .activity("Changed Password")
                        .entityName(userResponse.getName())
                        .build();
                activityRepo.save(activity);
            }

            /**
             * updating and hashing the password if it is NGO
             */
            if (ngo != null){
                ngo.setPassword(passwordEncoder.encode(credentials.getPassword()));
                NGO ngoResponse = ngoRepo.save(ngo);

                /**
                 * update activity log
                 */
                Activity activity = Activity
                        .builder()
                        .entityId(ngoResponse.getId())
                        .activity("Changed Password")
                        .entityName(ngoResponse.getOrganizationName())
                        .build();
                activityRepo.save(activity);
            }

            ResponseDTO responseDTO = AppUtils.getResponseDto("password reset was successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.info("Message->>>{}", e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to get a list of approved applicants for a given task.
     * @param taskId the id of the task
     * @return ResponseEntity containing a list of approved applicants and status info.
     * @auther Emmanuel Yidana
     * @createdAt 26th July 2025
     */
    public ResponseEntity<ResponseDTO> fetchListOfApprovedApplicantsForTask(UUID taskId){
        try {
            log.info("In fetch approved applicants for task->>>{}", taskId);

            /**
             * checking if task exist
             */
            Optional<Task> task = taskRepo.findById(taskId);
            if (task.isEmpty()){
                log.info("task record cannot be found->>>{}", taskId);
                ResponseDTO  response = AppUtils.getResponseDto("no task record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * loading applicants list from db
             */
            List<UserDTOProjection> users = userRepo.fetchListOfApprovedApplicantsForTask(taskId);
            if (users.isEmpty()){
                log.info("no user record found->>>{}", taskId);
                ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            ResponseDTO responseDTO = AppUtils.getResponseDto("applicants list", HttpStatus.OK, users);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description A helper method used to save user role
     * @param userId the id of the user
     * @param roleId the id of the role to be assigned
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    public void saveUserRole(UUID userId, UUID roleId){
        try {
            log.info("About to save user role->>>{}", userId);

            Optional<User> userOptional = userRepo.findById(userId);
            Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);

            if (userOptional.isEmpty() || roleSetupOptional.isEmpty()){
                throw new NotFoundException("user or role record not found");
            }

            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);

            userRoleRepo.save(userRole);
            log.info("user role saved successfully->>>{}", userId);

        }catch (Exception e) {
            throw new ServerException("Internal server error");
        }
    }

    /**
     * @description A helper method used to remove user role
     * @param userId the id of the user
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    public void removeUserRole(UUID userId){
        try {
            log.info("About to remove user role->>>{}", userId);

            UserRole userRole = userRoleRepo.findByUserId(userId);
            if (userRole == null){
                log.info("no role is associated with the given user id->>>{}", userId);
                throw new NotFoundException("no role is associated with the given user id");
            }

            userRoleRepo.deleteById(userRole.getId());
            log.info("user role removed successfully->>>{}", userRole.getId());

        }catch (Exception e) {
            throw new ServerException("Internal server error");
        }
    }

    /**
     * @description this method is used to fetch stats for both VOLUNTEERS, NGOs, and ADMIN
     * @return map of stats
     * @atuher Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    public ResponseEntity<ResponseDTO> fetchStatsForLoggedInUser(String startDate, String endDate){

        /**
         * converting start and end dates to LocalDateTime if provided
         */
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime=null;
        if (startDate!=null&&endDate!=null){
            LocalDate start = AppUtils.convertStringToLocalDateTime(startDate);
            LocalDate end = AppUtils.convertStringToLocalDateTime(endDate);

            startDateTime = start.atStartOfDay();
            endDateTime = end.atTime(LocalTime.MAX);
        }


        /**
         * getting details of logged-in user
         */
        UUID userId = UUID.fromString(AppUtils.getAuthenticatedUserId());
        String authenticatedUserRole = AppUtils.getAuthenticatedUserRole();
        log.info("Logged-in user role->>>{}", authenticatedUserRole);

        /**
         * an object to map the response to UI
         */
        List<Activity> recentActivities = activityRepo.getRecentActivitiesByUserId(userId);
        List<UserDTOProjection> activeVolunteers = userRepo.getActiveVolunteersOfTaskForLoggedInNGO(userId);
        List<UserDTOProjection> allVolunteers = userRepo.getTopFiveActiveVolunteersOfTaskForLoggedInNGO(userId);
        Map<String, Object> stats = new HashMap<>();

        /**
         * stats for NGO user
         */
        if (authenticatedUserRole.equalsIgnoreCase(AppConstants.NGO)){

            Integer totalCreatedTasksForTheMonthForNGO = 0;
            Integer totalTasksForNGO = 0;
            Integer totalCompletedTasksForNGO = 0;
            Integer totalActiveTasksForNGO = 0;
            Integer totalApplicationsForNGO = 0;
            Integer totalActiveVolunteers = 0;
            Integer totalVolunteers = 0;

            if(startDateTime!=null && endDateTime!=null){
                log.info("Fetching stats for NGO with date rage->>{} to {}", startDate, endDate);
                totalTasksForNGO = taskRepo.totalTasksForNGOWithRange(userId, startDateTime, endDateTime);
                totalCompletedTasksForNGO = taskRepo.totalCompletedTasksForNGOWithRange(userId,startDateTime, endDateTime);
                totalActiveTasksForNGO = taskRepo.totalActiveTasksForNGOWithRange(userId, startDateTime, endDateTime);
                totalApplicationsForNGO = applicationsRepo.totalApplicationsForNGOWithRange(userId, startDateTime, endDateTime);
            }else {
                totalTasksForNGO = taskRepo.totalTasksForNGO(userId);
                totalCompletedTasksForNGO = taskRepo.totalCompletedTasksForNGO(userId);
                totalActiveTasksForNGO = taskRepo.totalActiveTasksForNGO(userId);
                totalApplicationsForNGO = applicationsRepo.totalApplicationsForNGO(userId);
            }
//            totalCreatedTasksForTheMonthForNGO = taskRepo.totalCreatedTasksForTheMonthForNGO(userId);

            /**
             * remove duplicates for active volunteers
             */
            Collection<UserDTOProjection> activeSetResponse = activeVolunteers.stream()
                    .collect(Collectors.toMap(UserDTOProjection::getId, v -> v, (v1, v2) -> v1))
                    .values();
            totalActiveVolunteers = activeSetResponse.size();

            /**
             * remove duplicates for all voulunteers
             */
            Collection<UserDTOProjection> totalSetResponse = allVolunteers.stream()
                    .collect(Collectors.toMap(UserDTOProjection::getId, v -> v, (v1, v2) -> v1))
                    .values();

            totalActiveVolunteers = activeSetResponse.size();
            totalVolunteers = totalSetResponse.size();


//            stats.put("totalCreatedTasksForTheMonthForNGO", totalCreatedTasksForTheMonthForNGO);
            stats.put("totalTasksForNGO", totalTasksForNGO);
            stats.put("totalCompletedTasksForNGO", totalCompletedTasksForNGO);
            stats.put("totalActiveTasksForNGO", totalActiveTasksForNGO);
            stats.put("totalApplicationsForNGO", totalApplicationsForNGO);
            stats.put("totalActiveVolunteers", totalActiveVolunteers);
            stats.put("totalVolunteers", totalVolunteers);
        }

        /**
         * stats for VOLUNTEER user type
         */
        if (authenticatedUserRole.equalsIgnoreCase(AppConstants.VOLUNTEER)){

            Integer totalApplicationsForApplicant = 0;
            Integer totalApprovedApplicationsForApplicant = 0;
            Integer totalRejectedApplicationsForApplicant = 0;
            Integer totalPendingApplicationsForApplicant = 0;

            if (startDateTime!=null && endDateTime!=null){
                log.info("Fetching stats for VOLUNTEER with date rage->>{} to {}", startDate, endDate);
                totalApplicationsForApplicant = applicationsRepo.totalApplicationsForApplicantWithRange(userId, startDateTime, endDateTime);
                totalApprovedApplicationsForApplicant = applicationsRepo.totalApprovedApplicationsForApplicantWithRange(userId, startDateTime, endDateTime);
                totalRejectedApplicationsForApplicant = applicationsRepo.totalRejectedApplicationsForApplicantWithRange(userId, startDateTime, endDateTime);
                totalPendingApplicationsForApplicant = applicationsRepo.totalPendingApplicationsForApplicantWithRange(userId, startDateTime, endDateTime);
            }else {
                totalApplicationsForApplicant = applicationsRepo.totalApplicationsForApplicant(userId);
                totalApprovedApplicationsForApplicant = applicationsRepo.totalApprovedApplicationsForApplicant(userId);
                totalRejectedApplicationsForApplicant = applicationsRepo.totalRejectedApplicationsForApplicant(userId);
                totalPendingApplicationsForApplicant = applicationsRepo.totalPendingApplicationsForApplicant(userId);
            }

            stats.put("totalApplicationsForApplicant", totalApplicationsForApplicant);
            stats.put("totalApprovedApplicationsForApplicant", totalApprovedApplicationsForApplicant);
            stats.put("totalRejectedApplicationsForApplicant", totalRejectedApplicationsForApplicant);
            stats.put("totalPendingApplicationsForApplicant", totalPendingApplicationsForApplicant);
        }

        /**
         * stats for ADMIN user
         */
        if (authenticatedUserRole.equalsIgnoreCase(AppConstants.ADMIN)){

            Integer totalTasks=0;
            Integer totalActiveTasks =0;
            Integer totalCompletedTasks=0;
            Integer totalNGOSPendingApproval=0;
            Integer totalNGOs=0;
            Integer totalCreatedTasksForTheMonth=0;
            Integer totalCreatedNGOSForTheMonth=0;
            Integer totalUsers=0;
            Integer totalUsersCreatedForTheMonth=0;
            Integer totalApplications=0;
            Integer totalApprovedNGOS=0;

            if (startDate!=null&&endDate!=null){
                log.info("Fetching stats for ADMIN with date rage->>{} to {}", startDate, endDate);
                totalTasks= taskRepo.totalTasksWithRange(startDateTime, endDateTime);
                totalActiveTasks = taskRepo.totalActiveTasksWithRange(startDateTime, endDateTime);
                totalCompletedTasks = taskRepo.totalCompletedTasksWithRange(startDateTime, endDateTime);
                totalNGOSPendingApproval = ngoRepo.totalNGOSPendingApprovalWithRange(startDateTime, endDateTime);
                totalNGOs = ngoRepo.totalNGOsWithRange(startDateTime, endDateTime);
                totalUsers = userRepo.totalUsersWithRange(startDateTime, endDateTime);
                totalApplications = applicationsRepo.totalApplicationsWithRange(startDateTime, endDateTime);
                totalApprovedNGOS = ngoRepo.totalApprovedNGOSWithRange(startDateTime, endDateTime);
            }else {
                totalTasks=taskRepo.totalTasks();
                totalActiveTasks = taskRepo.totalActiveTasks();
                totalCompletedTasks = taskRepo.totalCompletedTasks();
                totalNGOSPendingApproval = ngoRepo.totalNGOSPendingApproval();
                totalNGOs = ngoRepo.totalNGOs();
                totalUsers = userRepo.totalUsers();
                totalApplications = applicationsRepo.totalApplications();
                totalApprovedNGOS = ngoRepo.totalApprovedNGOS();
            }

//            totalUsersCreatedForTheMonth = userRepo.totalCreatedUsersForTheMonth();
//            totalCreatedTasksForTheMonth = taskRepo.totalCreatedTasksForTheMonth();
//            totalCreatedNGOSForTheMonth = ngoRepo.totalCreatedNGOSForTheMonth();



            stats.put("totalTasks", totalTasks);
            stats.put("totalActiveTasks", totalActiveTasks);
            stats.put("totalCompletedTasks", totalCompletedTasks);
            stats.put("totalNGOSPendingApproval", totalNGOSPendingApproval);
            stats.put("totalApprovedNGOS", totalApprovedNGOS);
            stats.put("totalNGOs", totalNGOs);
            stats.put("totalUsers", totalUsers);
            stats.put("totalApplications", totalApplications);
//            stats.put("totalCreatedTasksForTheMonth", totalCreatedTasksForTheMonth);
//            stats.put("totalCreatedNGOSForTheMonth", totalCreatedNGOSForTheMonth);
//            stats.put("totalUsersCreatedForTheMonth", totalUsersCreatedForTheMonth);
        }

        /**
         * building the general response object
         */
        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("recentActivities", recentActivities);
        responseObject.put("stats", stats);

        /**
         * available to only NGOs
         */
        if (authenticatedUserRole.equalsIgnoreCase(AppConstants.NGO)){
            //remove duplicates
            Collection<UserDTOProjection> setResponse = activeVolunteers.stream()
                    .collect(Collectors.toMap(UserDTOProjection::getId, v -> v, (v1, v2) -> v1))
                    .values();

            // fetching recent tasks for logged-in NGO
            List<Task> recentTasks = taskRepo.fetchRecentTasksForNGO(userId);
            responseObject.put("recentVolunteers", setResponse);
            responseObject.put("recentTasks", recentTasks);
        }

        ResponseDTO responseDTO = AppUtils.getResponseDto("stats", HttpStatus.OK, responseObject);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    public void keepServerAlive() {
        log.info("Hello server!");
    }

    @Scheduled(fixedRate = 30000)
    public void keepServerServiceAlive(){
        String url = "https://server-service-6ab8.onrender.com/keep-server-alive";
        restTemplate.getForEntity(url, Object.class);
    }

}
