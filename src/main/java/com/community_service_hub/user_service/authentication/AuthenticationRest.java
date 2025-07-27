package com.community_service_hub.user_service.authentication;

import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.Credentials;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserDTOProjection;
import com.community_service_hub.user_service.exception.UnAuthorizeException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.util.AppUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/authenticate")
@Tag(name = "Authentication Management", description = "This controller is responsible for authenticating user credentials.")
public class AuthenticationRest {

    private final JWTAccess jwtAccess;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final OTPServiceImpl otpService;
    private final NGORepo ngoRepo;

    @Autowired
    public AuthenticationRest(JWTAccess jwtAccess, AuthenticationManager authenticationManager, UserRepo userRepo, OTPServiceImpl otpService, NGORepo ngoRepo) {
        this.jwtAccess = jwtAccess;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.otpService = otpService;
        this.ngoRepo = ngoRepo;
    }

    /**
     * @description This method is used to authenticate users abd generate token on authentication success.
     * @param credentials the payload data containing the user credentials to be authenticated
     * @return ResponseEntity containing the authenticated user details and status information
     * @auther Emmanuel Yidana
     * @createdAt 30th April 2025
     */
    @Operation(summary = "This endpoint is used to authenticate user credentials or identity")
    @PostMapping
    public ResponseEntity<ResponseDTO> authenticateUser(@RequestBody Credentials credentials){
        try {
            log.info("In authentication method->>>{}", credentials.getEmail());

            /**
             * checking if user is verified before attempting to log in
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
                log.info("Authentication failed->>>{}", credentials.getEmail());
                ResponseDTO  response = AppUtils.getResponseDto("invalid credentials", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            /**
             * loading user details to build response data
             */
            Optional<NGO> ngoOptional = ngoRepo.findNGOByEmail(credentials.getEmail());
            Optional<User> userOptional = userRepo.findUserByEmail(credentials.getEmail());
            if (userOptional.isEmpty()&&ngoOptional.isEmpty()){
                log.info("User record not found->>>{}", credentials.getEmail());
                ResponseDTO  response = AppUtils.getResponseDto("User record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * generating token
             */
            UUID userId = userOptional.isPresent()?userOptional.get().getId():ngoOptional.get().getId();
            String token = jwtAccess.generateToken(credentials.getEmail(), userId);

            /**
             * building response details
             */
            Map<String, String> tokenData = new HashMap<>();
            tokenData.put("email", userOptional.isPresent()?userOptional.get().getEmail():ngoOptional.get().getEmail());
            tokenData.put("role", userOptional.isPresent()?userOptional.get().getUserRole():ngoOptional.get().getRole());
            tokenData.put("full name", userOptional.isPresent()?userOptional.get().getName():ngoOptional.get().getOrganizationName());
            tokenData.put("token", token);
            tokenData.put("userId", userId.toString());

            /**
             * returning response after successfully authenticating
             */
            log.info("Authentication success->>>");
            ResponseDTO  response = AppUtils.getResponseDto("authentication successfully", HttpStatus.OK, tokenData);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
