package com.community_service_hub.user_service;

import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.DTOMapper;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.models.Activity;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.ActivityRepo;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    private OTPServiceImpl otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * this method is used to simulate the create user method
     */
    @Test
    void createUserTest(){
        User user = User
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .address("Accra")
                .name("Emmanuel Yidana")
                .latitude(0.864864)
                .longitude(0.5564)
                .userRole("ADMIN")
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        UserPayloadDTO dto =UserPayloadDTO
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .address("Accra")
                .name("Emmanuel Yidana")
                .latitude(0.864864)
                .longitude(0.5564)
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .role("ADMIN")
                .build();

        Activity activity = Activity
                .builder()
                .activity("Account Creation")
                .entityName("Test Entity")
                .entityId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.empty());
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(null);
        when(userRepo.save(user)).thenReturn(user);
        when(activityRepo.save(activity)).thenReturn(activity);

        ResponseEntity<ResponseDTO> response = userService.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }
}
