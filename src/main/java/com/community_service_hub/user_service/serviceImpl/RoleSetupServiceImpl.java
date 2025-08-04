package com.community_service_hub.user_service.serviceImpl;

import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.exception.NotFoundException;
import com.community_service_hub.user_service.models.RoleSetup;
import com.community_service_hub.user_service.repo.RoleSetupRepo;
import com.community_service_hub.user_service.service.RoleSetupService;
import com.community_service_hub.util.AppUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleSetupServiceImpl implements RoleSetupService {

    private final RoleSetupRepo roleSetupRepo;

    @Autowired
    public RoleSetupServiceImpl(RoleSetupRepo roleSetupRepo) {
        this.roleSetupRepo = roleSetupRepo;
    }

    /**
     * @description This method is used to save role setup record.
     * @param roleSetup the payload of the role to be added
     * @return ResponseEntity containing the saved role setup record and status information.
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> saveRole(RoleSetup roleSetup) {
      try {
          if (roleSetup == null){
              ResponseDTO  response = AppUtils.getResponseDto("payload cannot be null", HttpStatus.BAD_REQUEST);
              return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
          }
          roleSetup.setName(roleSetup.getName().toUpperCase());
          RoleSetup roleSetupRes = roleSetupRepo.save(roleSetup);
          ResponseDTO  response = AppUtils.getResponseDto("role record added successfully", HttpStatus.CREATED, roleSetupRes);
          return new ResponseEntity<>(response, HttpStatus.CREATED);
      }catch (Exception e) {
          ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    /**
     * @description This method is used to update role setup record.
     * @param roleSetup the payload of the role to be updated
     * @param roleId the id of the role to be updated
     * @return ResponseEntity containing the updated role setup record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateRole(RoleSetup roleSetup, UUID roleId) {

        RoleSetup existingRoleSetup = roleSetupRepo.findById(roleId)
                .orElseThrow(()-> new NotFoundException("role setup record not found"));

        existingRoleSetup.setName(roleSetup.getName() != null? roleSetup.getName().toUpperCase():existingRoleSetup.getName());
        ResponseDTO  response = AppUtils.getResponseDto("role records updated successfully", HttpStatus.OK, roleSetup);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @description This method is used to fetch ole setup records given the id.
     * @param roleId
     * @return ResponseEntity containing the role setup record and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> findRoleById(UUID roleId) {
        try {
            Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
            if (roleSetupOptional.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("role record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            RoleSetup roleSetup = roleSetupOptional.get();
            ResponseDTO  response = AppUtils.getResponseDto("role records fetched successfully", HttpStatus.OK, roleSetup);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to delete role setup record.
     * @param roleId the id of the role to be removed
     * @return ResponseEntity containing status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> deleteRole(UUID roleId) {
        RoleSetup roleSetup = roleSetupRepo.findById(roleId)
                .orElseThrow(()-> new NotFoundException("role setup record not found"));

        roleSetupRepo.deleteById(roleSetup.getId());
        ResponseDTO  response = AppUtils.getResponseDto("role records deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @description This method is used to fetch all role setups.
     * @return ResponseEntity containing all role setups and status information
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getRoles() {
        try{
            List<RoleSetup> roleSetups = roleSetupRepo.findAll();
            if (roleSetups.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("no role record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            ResponseDTO  response = AppUtils.getResponseDto("roles records fetched successfully", HttpStatus.OK, roleSetups);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
