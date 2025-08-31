package com.community_service_hub.user_service;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.NotificationServiceImpl;
import com.community_service_hub.user_service.dto.DTOMapper;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.models.Activity;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.ActivityRepo;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

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

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * this method is used to simulate the create user method
     */
    @Test
    @DisplayName("Simulating the create user method")
    void createUserTest() {
        User user = User.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .userRole("ADMIN")
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        UserPayloadDTO dto = UserPayloadDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .role("ADMIN")
                .build();

        Activity activity = Activity.builder()
                .activity("Account Creation")
                .entityName("Test Entity")
                .entityId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        /**
         * stubbing external dependencies
         */
        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.empty());
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(null);
        when(dtoMapper.toUserEntity(any(UserPayloadDTO.class))).thenReturn(user);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(activityRepo.save(any(Activity.class))).thenReturn(activity);
        doNothing().when(otpService).sendOtp(any(OTPPayload.class));

        ResponseEntity<ResponseDTO> response = userService.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        /**
         * verify if they were actually called
         */
        verify(userRepo).findUserByEmail("eyidana001@gmail.com");
        verify(ngoRepo).findByEmail("eyidana001@gmail.com");
        verify(userRepo).save(user);
        verify(activityRepo).save(activity);
        verify(otpService).sendOtp(any(OTPPayload.class));

    }


}
