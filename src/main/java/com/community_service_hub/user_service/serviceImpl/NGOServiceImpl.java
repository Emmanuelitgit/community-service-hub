package com.community_service_hub.user_service.serviceImpl;

import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.exception.AlreadyExistException;
import com.community_service_hub.user_service.exception.BadRequestException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.service.NGOService;
import com.community_service_hub.user_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NGOServiceImpl implements NGOService {

    private final NGORepo ngoRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Autowired
    public NGOServiceImpl(NGORepo ngoRepo, PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.ngoRepo = ngoRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }


    /**
     * @description This method is used to save NGO to the db
     * @param ngo the payload data of the NGO to be added
     * @return ResponseEntity containing the saved NGO record and status information
     * @auther Emmanuel Yidana
     * @createdAt 21st july 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> saveNGO(NGO ngo) {
        try {
            log.info("In save NGO method->>>{}", ngo);
            /**
             * checking if payload is null
             */
            if (ngo == null){
                log.info("payload is null->>>{}", HttpStatus.BAD_REQUEST);
                throw new BadRequestException("NGO payload cannot be null");
            }

            /**
             * checking if email already exist
             */
            NGO ngoEmailAlreadyExist = ngoRepo.findByEmail(ngo.getEmail());
            Optional<User> user = userRepo.findUserByEmail(ngo.getEmail());
            if (ngoEmailAlreadyExist != null || user.isPresent()){
                log.info("email already exist->>>{}", ngo.getEmail());
                throw new AlreadyExistException("email already exist");
            }

            /**
             * saving record
             */
            ngo.setPassword(passwordEncoder.encode(ngo.getPassword()));
            NGO ngoResponse = ngoRepo.save(ngo);

            /**
             * returning response if everything is success
             */
            ResponseDTO responseDTO = AppUtils.getResponseDto("NGO added successfully", HttpStatus.CREATED, ngoResponse);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updateNGO(NGO ngo, UUID ngoId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> findNGOById(UUID ngoId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteNGO(UUID ngoId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> getNGOs() {
        return null;
    }
}
