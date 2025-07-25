package com.community_service_hub.user_service.serviceImpl;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.serviceImpl.OTPServiceImpl;
import com.community_service_hub.user_service.dto.NGODTO;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserRole;
import com.community_service_hub.user_service.exception.AlreadyExistException;
import com.community_service_hub.user_service.exception.BadRequestException;
import com.community_service_hub.user_service.exception.NotFoundException;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.service.NGOService;
import com.community_service_hub.user_service.util.AppUtils;
import com.community_service_hub.user_service.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NGOServiceImpl implements NGOService {

    private final NGORepo ngoRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final NGODTO ngodto;
    private final OTPServiceImpl otpService;

    @Autowired
    public NGOServiceImpl(NGORepo ngoRepo, PasswordEncoder passwordEncoder, UserRepo userRepo, NGODTO ngodto, OTPServiceImpl otpService) {
        this.ngoRepo = ngoRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.ngodto = ngodto;
        this.otpService = otpService;
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
                ResponseDTO  response = AppUtils.getResponseDto("email already exist", HttpStatus.ALREADY_REPORTED);
                return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
            }

            /**
             * saving record
             */
            ngo.setPassword(passwordEncoder.encode(ngo.getPassword()));
            ngo.setRole(UserRole.NGO.toString());
            NGO ngoResponse = ngoRepo.save(ngo);

            /**
             * send otp to user
             */
            OTPPayload otpPayload = OTPPayload
                    .builder()
                    .email(ngoResponse.getEmail())
                    .build();
            otpService.sendOtp(otpPayload);

            /**
             * returning response if everything is success
             */
            NGODTO data = NGODTO.toNGODTO(ngoResponse);
            ResponseDTO responseDTO = AppUtils.getResponseDto("NGO added successfully", HttpStatus.CREATED, data);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to update an existing NGO records
     * @param ngo the payload data of the NGO to be updated
     * @param ngoId the id of the NGO to be updated
     * @return ResponseEntity containing the updated NGO record and status information
     * @auther Emmanuel Yidana
     * @createdAt 21st july 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateNGO(NGO ngo, UUID ngoId) {
        try{
            log.info("In update NGO records by id method->>>{}->>>ID({})", ngo, ngoId);

            /**
             * loading NGO record from db by id
             */
            Optional<NGO> ngoOptional = ngoRepo.findById(ngoId);
            if (ngoOptional.isEmpty()){
                log.info("NGO record not found->>>{}", ngo.getId());
                ResponseDTO  response = AppUtils.getResponseDto("NGO record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * building the updated data to be saved
             */
            NGO existingData = ngoOptional.get();
            existingData.setRole(ngo.getRole()!=null?ngo.getRole(): existingData.getRole());
            existingData.setOrganizationName(ngo.getOrganizationName() != null ? ngo.getOrganizationName() : existingData.getOrganizationName());
            existingData.setLogo(ngo.getLogo() != null ? ngo.getLogo() : existingData.getLogo());
            existingData.setState(ngo.getState() != null ? ngo.getState() : existingData.getState());
            existingData.setCity(ngo.getCity() != null ? ngo.getCity() : existingData.getCity());
            existingData.setWebsite(ngo.getWebsite() != null ? ngo.getWebsite() : existingData.getWebsite());
            existingData.setSocialLinks(ngo.getSocialLinks() != null ? ngo.getSocialLinks() : existingData.getSocialLinks());
            existingData.setCertificate(ngo.getCertificate() != null ? ngo.getCertificate() : existingData.getCertificate());
            existingData.setEmail(ngo.getEmail() != null ? ngo.getEmail() : existingData.getEmail());
            existingData.setPassword(ngo.getPassword() != null ? ngo.getPassword() : existingData.getPassword());
            existingData.setAddress(ngo.getAddress() != null ? ngo.getAddress() : existingData.getAddress());
            existingData.setLatitude(ngo.getLatitude() != null ? ngo.getLatitude() : existingData.getLatitude());
            existingData.setLongitude(ngo.getLongitude() != null ? ngo.getLongitude() : existingData.getLongitude());
            existingData.setDescription(ngo.getDescription() != null ? ngo.getDescription() : existingData.getDescription());
            existingData.setRole(ngo.getRole() != null ? ngo.getRole() : existingData.getRole());

            /**
             * saving updated records
             */
            NGO ngoResponse = ngoRepo.save(existingData);
            /**
             * returning response if everything is success
             */
            NGODTO data = NGODTO.toNGODTO(ngoResponse);
            ResponseDTO responseDTO = AppUtils.getResponseDto("NGO added successfully", HttpStatus.CREATED, data);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * @description This method is used to get NGO record by id
     * @param ngoId the id of the record to retrieve
     * @return ResponseEntity containing the NGO record and status information
     * @auther Emmanuel Yidana
     * @createdAt 24th july 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> findNGOById(UUID ngoId) {
        log.info("In get NGO record by id method->>>{}", ngoId);
        /**
         * loading ngo record from db by id
         */
        Optional<NGO> ngo = ngoRepo.findById(ngoId);
        if (ngo.isEmpty()){
            log.info("NGO record cannot be found with the id->>>{}", ngoId);
            ResponseDTO  response = AppUtils.getResponseDto("no ngo record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        /**
         * returning response if successfully
         */
        NGODTO data = NGODTO.toNGODTO(ngo.get());
        ResponseDTO  response = AppUtils.getResponseDto("ngos list", HttpStatus.OK, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * @description This method is used to delete an NGO record by id
     * @param ngoId the id of the record to be deleted
     * @return ResponseEntity containing the response and status information
     * @auther Emmanuel Yidana
     * @createdAt 24th july 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> deleteNGO(UUID ngoId) {
       try{
           log.info("In delete NGO by id method->>>{}", ngoId);
           /**
            * loading ngo record from db by id
            */
           Optional<NGO> ngo = ngoRepo.findById(ngoId);
           if (ngo.isEmpty()){
               log.info("NGO record cannot be found with the id->>>{}", ngoId);
               ResponseDTO  response = AppUtils.getResponseDto("no ngo record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }

           /**
            * removing record
            */
           ngoRepo.deleteById(ngoId);

           /**
            * returning response if successfully
            */
           ResponseDTO  response = AppUtils.getResponseDto("ngo record deleted", HttpStatus.OK);
           return new ResponseEntity<>(response, HttpStatus.OK);

       }catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           throw new ServerException(e.getMessage());
       }

    }


    /**
     * @description This method is used to fetch all NGOs
     * @return ResponseEntity containing the NGO record list and status information
     * @auther Emmanuel Yidana
     * @createdAt 24th july 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getNGOs() {
       try{
           log.info("In fetch all NGOs list method->>>");
           /**
            * loading ngos list from the db
            */
           List<NGO> ngos = ngoRepo.findAll();
           if (ngos.isEmpty()){
               log.info("no NGO record found");
               ResponseDTO  response = AppUtils.getResponseDto("no ngo record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }

           /**
            * returning response if successfully
            */
           List<NGODTO > ngodtos  = ngodto.toNGOList(ngos);
           ResponseDTO  response = AppUtils.getResponseDto("ngos list", HttpStatus.OK, ngodtos);
           return new ResponseEntity<>(response, HttpStatus.OK);

       }catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           throw new ServerException(e.getMessage());
       }

    }


    @Transactional
    public byte[] getCertificate(UUID id) {
        log.info("In get image method:->>>>");
        NGO dbImage = ngoRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("ngo record not found"));
        return ImageUtil.decompressImage(dbImage.getCertificate());
    }

    @Transactional
    public byte[] getLogo(UUID id) {
        log.info("In get image method:->>>>");
        NGO dbImage = ngoRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("ngo record not found"));
        return ImageUtil.decompressImage(dbImage.getLogo());
    }
}
