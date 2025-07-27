package com.community_service_hub.user_service.rest;

import com.community_service_hub.user_service.dto.Credentials;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserRest {

    private final UserServiceImpl userService;
    private final NGOServiceImpl ngoService;

    @Autowired
    public UserRest(UserServiceImpl userService, NGOServiceImpl ngoService) {
        this.userService = userService;
        this.ngoService = ngoService;
    }

    @Operation(summary = "This endpoint is used to to create a new user")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createUser(@Valid UserPayloadDTO user){
        return userService.createUser(user);
    }

    @Operation(summary = "This endpoint is used to fetch all users")
    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(){
        return userService.getUsers();
    }

    @Operation(summary = "This endpoint is used to to get user record by id")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable UUID userId){
        log.info("In get user by id controller->>>{}", userId);
        return userService.getUserById(userId);
    }

    @Operation(summary = "This endpoint is used to update user")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody @Valid UserPayloadDTO user){
        return userService.updateUser(user);
    }

    @Operation(summary = "This endpoint is used to delete user given the user id")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> removeUser(@PathVariable UUID userId){
        return userService.removeUser(userId);
    }

    @Operation(summary = "This endpoint is used to reset user password")
    @PutMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid Credentials credentials){
        log.info("In reset password controller->>>{}", credentials.getEmail());
        return userService.resetPassword(credentials);
    }

    @Operation(summary = "This endpoint is used to fetch a list of approved applicants for a task given the task id")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ResponseDTO> fetchListOfApprovedApplicantsForTask(@PathVariable UUID taskId){
        return userService.fetchListOfApprovedApplicantsForTask(taskId);
    }
}