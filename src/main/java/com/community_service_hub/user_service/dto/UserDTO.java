package com.community_service_hub.user_service.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * @description This class is used to map response to the client side.
 * @return
 * @auther Emmanuel Yidana
 * @createdAt 15th  May 2025
 */

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String username;
    private String role;
}