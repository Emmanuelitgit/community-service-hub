package com.community_service_hub.user_service.rest;


import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.models.RoleSetup;
import com.community_service_hub.user_service.serviceImpl.RoleSetupServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/role-setups")
public class RoleSetupRest {

    private final RoleSetupServiceImpl roleSetupServiceImpl;

    @Autowired
    public RoleSetupRest( RoleSetupServiceImpl roleSetupServiceImpl) {
        this.roleSetupServiceImpl = roleSetupServiceImpl;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getRoles(){
        return roleSetupServiceImpl.getRoles();
    }
    @PostMapping
    public ResponseEntity<ResponseDTO> saveRole(@RequestBody @Valid RoleSetup roleSetup){
        return roleSetupServiceImpl.saveRole(roleSetup);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ResponseDTO> updateRole(@RequestBody @Valid RoleSetup roleSetup, @PathVariable UUID roleId){
        return roleSetupServiceImpl.updateRole(roleSetup, roleId);
    }
    @GetMapping("/{roleId}")
    public ResponseEntity<ResponseDTO> findRoleById(@PathVariable UUID roleId){
        return roleSetupServiceImpl.findRoleById(roleId);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ResponseDTO> deleteRole(@PathVariable UUID roleId){
        return roleSetupServiceImpl.deleteRole(roleId);
    }
}