package com.community_service_hub.user_service.serviceImpl;

import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.RoleSetup;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.models.UserRole;
import com.community_service_hub.user_service.repo.RoleSetupRepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.repo.UserRoleRepo;
import com.community_service_hub.user_service.service.UserRoleService;
import com.community_service_hub.user_service.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepo userRepo;
    private final RoleSetupRepo roleSetupRepo;
    private final UserRoleRepo userRoleRepo;

    @Autowired
    public UserRoleServiceImpl(UserRepo userRepo, RoleSetupRepo roleSetupRepo, UserRoleRepo userRoleRepo) {
        this.userRepo = userRepo;
        this.roleSetupRepo = roleSetupRepo;
        this.userRoleRepo = userRoleRepo;
    }

    /**
     * @description This method is used to save user role
     * @param roleId the id of the role to be assigned to the user
     * @param userId the id of the user
     * @return ResponseEntity containing the saved user role and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> saveUserRole(UUID userId, UUID roleId) {
       try {

           /**
            * checking for user and role availability. an exception is thrown in the absence of one
            */
           Optional<User> userOptional = userRepo.findById(userId);
           Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);

           if (userOptional.isEmpty() || roleSetupOptional.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("user or role record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }

           UserRole userRole = new UserRole();
           userRole.setRoleId(roleId);
           userRole.setUserId(userId);

           UserRole userRoleResponse = userRoleRepo.save(userRole);

           ResponseDTO  response = AppUtils.getResponseDto("users records fetched successfully", HttpStatus.OK, userRoleResponse);
           return new ResponseEntity<>(response, HttpStatus.OK);
       }catch (Exception e) {
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
}
