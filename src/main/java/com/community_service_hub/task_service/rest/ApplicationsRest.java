package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.Applications;
import com.community_service_hub.task_service.serviceImpl.ApplicationsServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApplicationsRest {

    private final ApplicationsServiceImpl applicationsService;

    @Autowired
    public ApplicationsRest(ApplicationsServiceImpl applicationsService) {
        this.applicationsService = applicationsService;
    }

    @GetMapping("/applications")
    public ResponseEntity<ResponseDTO> getApplications(){
        return applicationsService.getApplications();
    }

    @PostMapping(value = "/applications",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createApplication(Applications applications){
        return applicationsService.createApplication(applications);
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ResponseDTO> getApplicationById(@PathVariable UUID applicationId){
        return applicationsService.getApplicationById(applicationId);
    }

    @PutMapping(value = "/applications",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateApplication(Applications applications){
        return applicationsService.updateApplication(applications);
    }

    @PutMapping("/applications/status")
    public ResponseEntity<ResponseDTO> updateStatus(@RequestParam(name = "status") String status,
                                                    @RequestParam(name = "applicationId") UUID applicationId){
        return applicationsService.updateApplicationStatus(status, applicationId);
    }

    @GetMapping("/user/applications")
    public ResponseEntity<ResponseDTO> fetchUserApplications(){
        return applicationsService.fetchUserApplications();
    }
}
