package com.community_service_hub.user_service.authentication;

import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.Credentials;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserDTOProjection;
import com.community_service_hub.user_service.exception.UnAuthorizeException;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/authenticate")
public class AuthenticationRest {

    private final JWTAccess jwtAccess;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final OTPServiceImpl otpService;

    @Autowired
    public AuthenticationRest(JWTAccess jwtAccess, AuthenticationManager authenticationManager, UserRepo userRepo, OTPServiceImpl otpService) {
        this.jwtAccess = jwtAccess;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.otpService = otpService;
    }

    /**
     * @description This method is used to authenticate users abd generate token on authentication success.
     * @param credentials the payload data containing the user credentials to be authenticated
     * @return ResponseEntity containing the authenticated user details and status information
     * @auther Emmanuel Yidana
     * @createdAt 30th April 2025
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> authenticateUser(@RequestBody Credentials credentials){
        try {
            log.info("In authentication method->>>");

            /**
             * checking if user verified before attempting to log in
             */
            Boolean isUserVerified = otpService.checkOTPStatusDuringLogin(credentials.getEmail());
            if (Boolean.FALSE.equals(isUserVerified)){
                log.info("user not verified->>>{}", credentials.getEmail());
                throw new UnAuthorizeException("user not verified");
            }

            /**
             * building authentication object
             */
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword()
                    )
            );

            /**
             * authenticating user credentials against the database records
             */
            if (!authentication.isAuthenticated()){
                log.info("Authentication failed->>>");
                ResponseDTO  response = AppUtils.getResponseDto("invalid credentials", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            UserDTOProjection user = userRepo.getUsersDetailsByUserEmail(credentials.getEmail());

            /**
             * generating token
             */
            String token = jwtAccess.generateToken(credentials.getEmail(), user.getId());

            /**
             * building response details
             */
            Map<String, String> tokenData = new HashMap<>();
            tokenData.put("email", credentials.getEmail());
            tokenData.put("role", user.getUserRole());
            tokenData.put("full name", user.getName());
            tokenData.put("token", token);

            /**
             * returning response after successfully authenticating
             */
            log.info("Authentication success->>>");
            ResponseDTO  response = AppUtils.getResponseDto("authentication successfully", HttpStatus.OK, tokenData);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
