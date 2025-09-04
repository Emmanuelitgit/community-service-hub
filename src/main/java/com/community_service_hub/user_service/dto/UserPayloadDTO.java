package com.community_service_hub.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPayloadDTO {
    private UUID id;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot be null or empty")
    private String email;
    @NotBlank(message = "Phone number cannot be null or empty")
    @Size(max = 10, min = 10, message = "Phone number must be 10 digits maximum")
    private String phone;
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 4, max = 20, message = "Password must be between 8 and 20 characters")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$",
//            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    @NotBlank(message = "Role cannot be null or empty")
    private String role;
    @NotBlank(message = "Address cannot be null or empty")
    private String address;
}
