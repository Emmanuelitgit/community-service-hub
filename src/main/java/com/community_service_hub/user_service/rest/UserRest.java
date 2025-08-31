package com.community_service_hub.user_service.rest;

import com.community_service_hub.user_service.dto.Credentials;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.dto.UserUpdateDTO;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "This controller is responsible for performing all the user CRUD")
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
    public ResponseEntity<ResponseDTO> getUsers(Principal principal){
        log.info("Principal->>>{}", principal.getName());
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
    public ResponseEntity<ResponseDTO> updateUser(UserUpdateDTO user){
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

    @Operation(summary = "This endpoint is used to fetch stats for logged-in user." +
            "It takes two optional request params, startDate and endDate." +
            "This filters out items/activities that falls within the range." +
            "When supply only startDate, it will filter out items within that month only." +
            "By default it brings out items within the current month/date")
    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO> fetchStatsForLoggedInUser(
            @RequestParam(name = "startDate", value = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", value = "endDate", required = false) String endDate){
        return userService.fetchStatsForLoggedInUser(startDate, endDate);
    }

    @GetMapping("/keep-server-alive")
    public void keepServerAlive(){
        userService.keepServerAlive();
    }
}