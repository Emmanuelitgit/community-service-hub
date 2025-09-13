package com.community_service_hub.user_service.service;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.NotificationServiceImpl;
import com.community_service_hub.user_service.dto.*;
import com.community_service_hub.user_service.models.Activity;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.ActivityRepo;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import com.community_service_hub.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private NGORepo ngoRepo;

    @Mock
    private ActivityRepo activityRepo;
    @Mock
    private DTOMapper dtoMapper;
    @Mock
    private NotificationServiceImpl otpService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AppUtils appUtils;
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * this method is used to simulate the create user method
     */
    @Test
    @DisplayName("Simulating the create user method")
    void shouldCreateUserSuccessfully() {

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        User user = User.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .userRole("ADMIN")
                .id(Id)
                .build();

        UserPayloadDTO dto = UserPayloadDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .id(Id)
                .role("ADMIN")
                .build();

        UserDTO userDTO = UserDTO
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .role("Admin")
                .id(Id)
                .build();

        /**
         * stubbing external dependencies
         */
        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.empty());
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(null);
        when(dtoMapper.toUserEntity(dto)).thenReturn(user);
        when(dtoMapper.toUserDTO(user)).thenReturn(userDTO);
        when(userRepo.save(user)).thenReturn(user);
        doNothing().when(otpService).sendOtp(any(OTPPayload.class));

        ResponseEntity<ResponseDTO> response = userService.createUser(dto);

        /**
         * assertions
         */
        assertNotNull(Objects.requireNonNull(response.getBody()).getData());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        /**
         * verify if they were actually called
         */
        verify(userRepo).findUserByEmail("eyidana001@gmail.com");
        verify(ngoRepo).findByEmail("eyidana001@gmail.com");
        verify(userRepo).save(user);
        verify(otpService).sendOtp(any(OTPPayload.class));

    }

    /**
     * this method is used to simulate the create user method when user already exist
     */
    @Test
    @DisplayName("Simulating the create user method when user already exist")
    void shouldFailWhenUserAlreadyExists() {

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        User user = User.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .userRole("ADMIN")
                .id(Id)
                .build();

        UserPayloadDTO dto = UserPayloadDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .id(Id)
                .role("ADMIN")
                .build();

        /**
         * stubbing external dependencies
         */
        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.of(user));
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(null);

        ResponseEntity<ResponseDTO> response = userService.createUser(dto);

        /**
         * assertions
         */
        assertNotNull(Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("Email already exist", response.getBody().getMessage());
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());

        /**
         * verify if they were actually called
         */
        verify(userRepo).findUserByEmail("eyidana001@gmail.com");
        verify(ngoRepo).findByEmail("eyidana001@gmail.com");

    }

    /**
     * this method is used to simulate the create user method when payload is null
     */
    @Test
    @DisplayName("Simulating the create user method when payload is null")
    void shouldFailWhenPayloadIsNull() {

        ResponseEntity<ResponseDTO> response = userService.createUser(null);

        /**
         * assertions
         */
        assertNotNull(Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("User payload cannot be null", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * this method is used to simulate the create user method
     */
    @Test
    @DisplayName("Simulating the update user method")
    void shouldUpdateUserSuccessfully() {

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        User user = User.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .userRole("ADMIN")
                .id(Id)
                .build();

        UserUpdateDTO dto = UserUpdateDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .address("WaleWale")
                .id(Id)
                .build();

        UserDTO userDTO = UserDTO
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .role("Admin")
                .id(Id)
                .build();

        Activity activity = Activity.builder()
                .activity("Updated Account Details")
                .entityName("Emmanuel Yidana")
                .entityId(Id)
                .build();

        /**
         * stubbing external dependencies
         */
        when(userRepo.findById(Id))
                .thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(activityRepo.save(activity)).thenReturn(activity);
        when(appUtils.getAuthenticatedUserId()).thenReturn(Id.toString());
        when(dtoMapper.toUserDTO(user)).thenReturn(userDTO);

        ResponseEntity<ResponseDTO> response = userService.updateUser(dto);

        /**
         * assertions
         */
        assertNotNull(Objects.requireNonNull(response.getBody()).getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        /**
         * verify if they were actually called
         */
        verify(userRepo).findById(Id);
        verify(userRepo).save(user);
        verify(activityRepo).save(activity);
    }

    /**
     * This method is used to simulate the method for fetching users
     */
    @DisplayName("Simulating get all users method")
    @Test
    void shouldGetAllUsersSuccessfully(){

        /**
         * this used to build the payload to be returned since tht DTO is an interface
         */
        UserDTOProjection mockUser = mock(UserDTOProjection.class);

        List<UserDTOProjection> users = List.of(mockUser);
        when(userRepo.getUsersDetails()).thenReturn(users);

        ResponseEntity<ResponseDTO> response = userService.getUsers();

        /**
         * assertions
         */
        assertNotNull(Objects.requireNonNull(response.getBody()).getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        /**
         * verify if it was actually called
         */
        verify(userRepo).getUsersDetails();


    }

    /**
     * This method is used to simulate the method for removing user records
     */
    @DisplayName("Simulating the method for deleting user")
    @Test
    void shouldDeleteUserSuccessfully(){

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        User user = User.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .userRole("ADMIN")
                .id(Id)
                .build();

        Activity activity = Activity.builder()
                .activity("Deleted Account")
                .entityName("Emmanuel Yidana")
                .entityId(Id)
                .build();

        when(userRepo.findById(Id)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).deleteById(Id);
        when(activityRepo.save(activity)).thenReturn(activity);

        ResponseEntity<ResponseDTO> response = userService.removeUser(Id);

        assertEquals("User record removed successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userRepo).findById(Id);
        verify(activityRepo).save(activity);
        verify(userRepo).deleteById(Id);
    }
}
