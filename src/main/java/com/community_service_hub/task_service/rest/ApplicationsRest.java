package com.community_service_hub.task_service.rest;

import com.community_service_hub.task_service.models.Applications;
import com.community_service_hub.task_service.serviceImpl.ApplicationsServiceImpl;
import com.community_service_hub.user_service.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Applications Management", description = "This controller is responsible for performing all the applications crud. " +
        "It provides implementations where users can apply for tasks posted by NGOs and wait for approval." +
        "When applications are approved by NGOs, they will then assign specific tasks to these users.")
public class ApplicationsRest {

    private final ApplicationsServiceImpl applicationsService;

    @Autowired
    public ApplicationsRest(ApplicationsServiceImpl applicationsService) {
        this.applicationsService = applicationsService;
    }

    @Operation(summary = "This endpoint is used to fetch all applications")
    @GetMapping("/applications")
    public ResponseEntity<ResponseDTO> getApplications(){
        return applicationsService.getApplications();
    }

    @Operation(summary = "This endpoint is used to create new application for a task")
    @PostMapping(value = "/applications",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createApplication(Applications applications){
        return applicationsService.createApplication(applications);
    }

    @Operation(summary = "This endpoint is used to get application record by id")
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ResponseDTO> getApplicationById(@PathVariable UUID applicationId){
        return applicationsService.getApplicationById(applicationId);
    }

    @Operation(summary = "This endpoint is used to update application")
    @PutMapping(value = "/applications",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateApplication(Applications applications){
        return applicationsService.updateApplication(applications);
    }

    @Operation(summary = "This endpoint is used to update application status to either[APPROVED or REJECTED]")
    @PutMapping("/applications/status")
    public ResponseEntity<ResponseDTO> updateStatus(@RequestParam(name = "status") String status,
                                                    @RequestParam(name = "applicationId") UUID applicationId){
        return applicationsService.updateApplicationStatus(status, applicationId);
    }

    @Operation(summary = "This endpoint is used to to fetch applications for current logged in [USER or NGO]")
    @GetMapping("/user/applications")
    public ResponseEntity<ResponseDTO> fetchUserApplications(){
        return applicationsService.fetchUserApplications();
    }
}
