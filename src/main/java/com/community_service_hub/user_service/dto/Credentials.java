package com.community_service_hub.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    @NotNull(message = "email is required")
    private String email;
    @NotNull(message = "password is required")
    private String password;
}
