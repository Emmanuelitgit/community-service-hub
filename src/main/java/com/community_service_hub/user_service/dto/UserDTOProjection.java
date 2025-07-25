package com.community_service_hub.user_service.dto;

import java.util.UUID;

/**
 * @description This interface class is used to map to the sql query to return user details.
 * @return
 * @auther Emmanuel Yidana
 * @createdAt 15th  May 2025
 */
public interface UserDTOProjection {
    UUID getId();
    String getName();
    String getEmail();
    String getPhone();
    String getUserRole();
    String getAddress();
}
