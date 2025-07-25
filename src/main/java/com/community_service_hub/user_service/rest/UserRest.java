package com.community_service_hub.user_service.rest;

import com.community_service_hub.user_service.dto.Credentials;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.dto.UserRole;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@Valid UserPayloadDTO user){
        return userService.createUser(user);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable UUID userId){
        log.info("In get user by id controller->>>{}", userId);
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody @Valid UserPayloadDTO user, @PathVariable UUID userId){
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> removeUser(@PathVariable UUID userId){
        return userService.removeUser(userId);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid Credentials credentials){
        log.info("In reset password controller->>>{}", credentials.getEmail());
        return userService.resetPassword(credentials);
    }
}
