package com.community_service_hub.user_service.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import com.community_service_hub.user_service.authentication.CustomFilter;
import com.community_service_hub.user_service.authentication.JWTAccess;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserDTO;
import com.community_service_hub.user_service.dto.UserPayloadDTO;
import com.community_service_hub.user_service.dto.UserUpdateDTO;
import com.community_service_hub.user_service.rest.UserRest;
import com.community_service_hub.user_service.serviceImpl.NGOServiceImpl;
import com.community_service_hub.user_service.serviceImpl.UserServiceImpl;
import com.community_service_hub.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@WebMvcTest(UserRest.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JWTAccess jwtAccess;

    @MockitoBean
    private CustomFilter customFilter;

    @MockitoBean
    private AppUtils appUtils;

    @MockitoBean
    NGOServiceImpl ngoService;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;


//    @BeforeEach
//    void generateToken(){
//        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
//        String SECRET = "RKUGLRKBKBSKLGSFIJSBKFBKJSDJBVugdtyidvctyfktvgkuyrcggchvrydtxtxuvyvgghghhhjhkjkjjurtyvkgvK";
//        long MINUTES = TimeUnit.DAYS.toMillis(7);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("issuer", "www.emma.com");
//        claims.put("userId", Id);
//
//        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
//        SecretKey secretKey = Keys.hmacShaKeyFor(decodedKey);
//
//        token = Jwts.builder()
//                .setClaims(claims)
//                .signWith(secretKey)
//                .setExpiration(Date.from(Instant.now().plusMillis(MINUTES)))
//                .setIssuedAt(Date.from(Instant.now()))
//                .setSubject("")
//                .compact();
//    }


    /**
     * This method is used to simulate the endpoint for creating users
     * @throws Exception
     */
    @DisplayName("Simulating the endpoint for creating users")
    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UserDTO userDTO = UserDTO
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .role("Admin")
                .id(Id)
                .build();

        UserPayloadDTO dto = UserPayloadDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .password("1234")
                .name("Emmanuel Yidana")
                .id(Id)
                .role("Admin")
                .address("WaleWale")
                .build();

        ResponseDTO responseDTO = AppUtils.getResponseDto("", HttpStatus.CREATED, userDTO);
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        when(userService.createUser(dto)).thenReturn(response);

        /**
         * simulating the api call
         */
        mockMvc.perform(multipart("/api/v1/users")
                        .param("email", "eyidana001@gmail.com")
                        .param("phone", "0597893082")
                        .param("name", "Emmanuel Yidana")
                        .param("id", Id.toString())
                        .param("role", "Admin")
                        .param("address", "WaleWale")
                        .param("password", "1234")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Emmanuel Yidana"));

        /**
         * verify if it was actually called
         */
        verify(userService, times(1)).createUser(dto);
    }

    /**
     * This method is used to simulate the endpoint for creating users
     * @throws Exception
     */
    @DisplayName("Simulating the endpoint for creating users")
    @Test
    void shouldUpdateUserSuccessfully() throws Exception {

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UserDTO userDTO = UserDTO
                .builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .role("Admin")
                .id(Id)
                .build();

        UserUpdateDTO dto = UserUpdateDTO.builder()
                .email("eyidana001@gmail.com")
                .phone("0597893082")
                .name("Emmanuel Yidana")
                .id(Id)
                .address("WaleWale")
                .build();

        ResponseDTO responseDTO = AppUtils.getResponseDto("", HttpStatus.OK, userDTO);
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(responseDTO, HttpStatus.OK);

        when(userService.updateUser(dto)).thenReturn(response);

        /**
         * simulating the api call
         */
        mockMvc.perform(multipart("/api/v1/users")
                        .param("email", "eyidana001@gmail.com")
                        .param("phone", "0597893082")
                        .param("name", "Emmanuel Yidana")
                        .param("id", Id.toString())
                        .param("role", "Admin")
                        .param("address", "WaleWale")
                        .with(request -> { request.setMethod("PUT"); return request; }))  // force PUT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Emmanuel Yidana"));

        /**
         * verify if it was actually called
         */
        verify(userService, times(1)).updateUser(dto);
    }

    /**
     * This method is used to simulate the endpoint for deleting users
     * @throws Exception
     */
    @DisplayName("Simulating the endpoint for deleting users")
    @Test
    void shouldDeleteUserSuccessfully() throws Exception {

        UUID Id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        ResponseDTO responseDTO = AppUtils.getResponseDto("", HttpStatus.OK);
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(responseDTO, HttpStatus.OK);

        when(userService.removeUser(Id)).thenReturn(response);

        /**
         * simulating the api call
         */
        mockMvc.perform(delete("/api/v1/users/"+Id.toString()))
                .andExpect(status().isOk());
        /**
         * verify if it was actually called
         */
        verify(userService, times(1)).removeUser(Id);
    }
}
