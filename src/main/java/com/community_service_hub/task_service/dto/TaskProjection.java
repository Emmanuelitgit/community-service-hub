package com.community_service_hub.task_service.dto;

import java.util.UUID;

public interface TaskProjection {
    UUID getId();
    UUID getPostedBy();
    String getName();
    String getCategory();
    String getDescription();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
    String getStartDate();
    Integer getNumberOfPeopleNeeded();
    String getStatus();
    String getOrganizationName();
    String getEmail();
    String  getOrganizationAddress();
    String getCity();
    String getLogo();
    UUID getNgoId();
    String getState();
    String getCountry();
    String getWebsite();
}
