package com.community_service_hub.user_service;

import com.community_service_hub.exception.ServerException;
import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.NotificationServiceImpl;
import com.community_service_hub.user_service.dto.*;
import com.community_service_hub.user_service.models.Activity;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.ActivityRepo;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class NGOControllerTest {

    @Mock
    private NGORepo ngoRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificationServiceImpl notificationService;
    @Mock
    private ActivityRepo activityRepo;
    @Mock
    private AppUtils appUtils;
    @InjectMocks
    private NGOServiceImpl ngoService;


    /**
     * This method is used to simulate the save NGO method
     */
    @DisplayName("Simulating the save NGO method")
    @Test
    void shouldSaveNGOSuccessfully(){
        NGO ngo = NGO
                .builder()
                .socialLinks("")
                .country("Ghana")
                .description("Hello description")
                .longitude(0.597989)
                .latitude(0.56555)
                .email("eyidana001@gmail.com")
                .password("1234")
                .phone("0597893082")
                .city("Accra")
                .organizationName("Test NGO")
                .isApproved(Boolean.TRUE)
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        Activity activity = Activity.builder()
                .activity("Account Creation")
                .entityName("Test NGO")
                .entityId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        /**
         * stubbing dependencies
         */
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(null);
        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.empty());
        when(ngoRepo.save(ngo)).thenReturn(ngo);
        when(activityRepo.save(activity)).thenReturn(activity);
        doNothing().when(notificationService).sendOtp(any(OTPPayload.class));

        ResponseEntity<ResponseDTO> response = ngoService.saveNGO(ngo);

        /**
         * assertions
         */
        assert Objects.requireNonNull(response.getBody()).getData()!=null;
        assertEquals("NGO added successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        /**
         * verify they were actually called
         */
        verify(ngoRepo).findByEmail("eyidana001@gmail.com");
        verify(userRepo).findUserByEmail("eyidana001@gmail.com");
        verify(ngoRepo).save(ngo);
        verify(activityRepo).save(activity);
    }

    /**
     * This method is used to simulate the save NGO method when NGO already exist
     */
    @DisplayName("Simulating the save NGO method when NGO already exist")
    @Test
    void shouldFailWhenNGOAlreadyExist(){
        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        NGO ngo = NGO
                .builder()
                .socialLinks("")
                .country("Ghana")
                .description("Hello description")
                .longitude(0.597989)
                .latitude(0.56555)
                .email("eyidana001@gmail.com")
                .password("1234")
                .phone("0597893082")
                .city("Accra")
                .organizationName("Test NGO")
                .isApproved(Boolean.TRUE)
                .id(Id)
                .build();

        /**
         * stubbing dependencies
         */
        when(ngoRepo.findByEmail("eyidana001@gmail.com")).thenReturn(ngo);
        when(userRepo.findUserByEmail("eyidana001@gmail.com")).thenReturn(Optional.empty());

        ResponseEntity<ResponseDTO> response = ngoService.saveNGO(ngo);

        /**
         * assertions
         */
        assertEquals("Email already exist",
                Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());

        /**
         * verify they were actually called
         */
        verify(ngoRepo).findByEmail("eyidana001@gmail.com");
        verify(userRepo).findUserByEmail("eyidana001@gmail.com");
    }

    /**
     * This method is used to simulate the save NGO method when NGO already exist
     */
    @DisplayName("Simulating the save NGO method when NGO already exist")
    @Test
    void shouldFailWhenPayloadIsNull(){

        ResponseEntity<ResponseDTO> response = ngoService.saveNGO(null);

        /**
         * assertions
         */
        assertEquals("NGO payload cannot be null", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    /**
     * This method is used to simulate the update NGO method
     */
    @DisplayName("Simulating the update NGO method")
    @Test
    void shouldUpdateNGOSuccessfully(){

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        NGO ngo = NGO
                .builder()
                .socialLinks("")
                .country("Ghana")
                .description("Hello description")
                .longitude(0.597989)
                .latitude(0.56555)
                .email("eyidana001@gmail.com")
                .password("1234")
                .phone("0597893082")
                .city("Accra")
                .organizationName("Test NGO")
                .isApproved(Boolean.TRUE)
                .id(Id)
                .build();

        Activity activity = Activity.builder()
                .activity("Updated Account Details")
                .entityName("Test NGO")
                .entityId(Id)
                .build();

        /**
         * stubbing dependencies
         */
        when(ngoRepo.save(ngo)).thenReturn(ngo);
        when(activityRepo.save(activity)).thenReturn(activity);
        when(ngoRepo.findById(Id)).thenReturn(Optional.of(ngo));
        when(appUtils.isUserAuthorized(Id, null)).thenReturn(Boolean.TRUE);

        ResponseEntity<ResponseDTO> response = ngoService.updateNGO(ngo, Id);

        /**
         * assertions
         */
        assert Objects.requireNonNull(response.getBody()).getData()!=null;
        assertEquals("NGO updated successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        /**
         * verify they were actually called
         */
        verify(ngoRepo).save(ngo);
        verify(activityRepo).save(activity);
        verify(ngoRepo).findById(Id);
    }

    /**
     * This method is used to simulate the behaviour for deleting an NGO
     */
    @DisplayName("Simulating the method for deleting NGO")
    @Test
    void shouldDeleteUserSuccessfully(){
        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        NGO ngo = NGO
                .builder()
                .socialLinks("")
                .country("Ghana")
                .description("Hello description")
                .longitude(0.597989)
                .latitude(0.56555)
                .email("eyidana001@gmail.com")
                .password("1234")
                .phone("0597893082")
                .city("Accra")
                .organizationName("Test NGO")
                .isApproved(Boolean.TRUE)
                .id(Id)
                .build();

        Activity activity = Activity.builder()
                .activity("Deleted Account")
                .entityName("Test NGO")
                .entityId(Id)
                .build();

        when(appUtils.isUserAuthorized(Id, null)).thenReturn(Boolean.TRUE);
        when(ngoRepo.findById(Id)).thenReturn(Optional.of(ngo));
        when(activityRepo.save(activity)).thenReturn(activity);
        doNothing().when(ngoRepo).deleteById(Id);

        ResponseEntity<ResponseDTO> response = ngoService.deleteNGO(Id);

        assertEquals("NGO record deleted", response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
