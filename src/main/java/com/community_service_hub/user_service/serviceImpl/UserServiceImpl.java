package com.community_service_hub.user_service.serviceImpl;


import com.community_service_hub.config.AppProperties;
import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.*;
import com.community_service_hub.user_service.exception.BadRequestException;
import com.community_service_hub.user_service.exception.NotFoundException;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.models.*;
import com.community_service_hub.user_service.models.UserRole;
import com.community_service_hub.user_service.repo.*;
import com.community_service_hub.user_service.service.UserService;
import com.community_service_hub.user_service.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
    private final UserLocationRepo userLocationRepo;
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final OTPServiceImpl otpService;
    private final NGORepo ngoRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, DTOMapper dtoMapper, PasswordEncoder passwordEncoder, UserRoleServiceImpl userRoleServiceImpl, RoleSetupRepo roleSetupRepo, RoleSetupServiceImpl roleSetupServiceImpl, UserRoleRepo userRoleRepo, UserLocationRepo userLocationRepo, RestTemplate restTemplate, AppProperties appProperties, OTPServiceImpl otpService, NGORepo ngoRepo) {
        this.userRepo = userRepo;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRoleServiceImpl = userRoleServiceImpl;
        this.roleSetupRepo = roleSetupRepo;
        this.roleSetupServiceImpl = roleSetupServiceImpl;
        this.userRoleRepo = userRoleRepo;
        this.userLocationRepo = userLocationRepo;
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
        this.otpService = otpService;
        this.ngoRepo = ngoRepo;
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
//           userPayloadDTO.setPassword(passwordEncoder.encode(userPayloadDTO.getPassword()));
           User user = dtoMapper.toUserEntity(userPayloadDTO);

           /**
            * saving user record
            */
           userPayloadDTO.setRole(userPayloadDTO.getRole().toUpperCase());
           User userResponse = userRepo.save(user);

           /**
            * saving user location
            */
           UserLocation location = UserLocation
                   .builder()
                   .userId(userResponse.getId())
                   .latitude(userPayloadDTO.getLatitude())
                   .longitude(userPayloadDTO.getLongitude())
                   .address(userPayloadDTO.getAddress())
                   .build();
           saveUserLocation(location);

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
     * @param userId the id of the user to be updated
     * @return ResponseEntity containing the saved user role and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateUser(UserPayloadDTO userPayload, UUID userId) {
        try{
            log.info("In update user method:->>>>>>{}", userPayload);

            /**
             * checking if user record exist by id
             */
            User existingData = userRepo.findById(userId)
                    .orElseThrow(()-> new NotFoundException("user record not found"));

            /**
             * building payload details to be saved
             */
            existingData.setEmail(userPayload.getEmail() !=null ? userPayload.getEmail() : existingData.getEmail());
            existingData.setName(userPayload.getName() !=null ? userPayload.getName() : existingData.getName());
            existingData.setPhone(userPayload.getPhone() !=null ? userPayload.getPhone() : existingData.getPhone());
            User userResponse = userRepo.save(existingData);

            /**
             * returning response if successfully
             */
            log.info("user records updated successfully:->>>>>>");
            ResponseDTO  response = AppUtils.getResponseDto("user records updated successfully", HttpStatus.OK, userResponse);
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
                ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            userRepo.deleteById(userId);

            log.info("user records removed successfully:->>>{}", userId);

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
                userRepo.save(existingUser);
            }

            /**
             * updating and hashing the password if it is NGO
             */
            if (ngo != null){
                ngo.setPassword(passwordEncoder.encode(credentials.getPassword()));
                ngoRepo.save(ngo);
            }

            ResponseDTO responseDTO = AppUtils.getResponseDto("password reset was successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.info("Message->>>{}", e.getMessage());
            throw new ServerException(e.getMessage());
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
     * @description A helper method used to save user location
     * @param userLocation the payload data of the user location to be added
     * @auther Emmanuel Yidana
     * @createdAt 21st july 2025
     */
    private void saveUserLocation(UserLocation userLocation){
        try {
            log.info("About to save user location->>>{}", userLocation.getUserId());

            if (userLocation == null){
                log.info("user location payload cannot be null->>>");
                throw new BadRequestException("user location payload cannot be null");
            }

            UserLocation location = userLocationRepo.save(userLocation);
            log.info("user location saved successfully->>>{}", location);

        }catch (Exception e) {
            log.info("Message->>>{}", e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     *  A chron method that will run every minute
     *  in order to keep the server alive when deployed to render
     */
    @Scheduled(fixedRate = 30000)
    public void fixedRateTask() {
        log.info("Hello server!");
    }

}
