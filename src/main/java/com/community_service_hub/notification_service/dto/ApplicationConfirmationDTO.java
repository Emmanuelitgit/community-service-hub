package com.community_service_hub.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfirmationDTO {
    private String date;
    private String location;
    private String status;
    private String task;
    private String category;
    private String email;
    private String startDate;
    private String userEmail;
}
