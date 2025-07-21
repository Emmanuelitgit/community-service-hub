package com.community_service_hub.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Data
public class UserPayloadDTO {
    private UUID id;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "email cannot be null")
    @Email(message = "invalid email")
    private String email;
    @NotNull(message = "phone number cannot be null")
    @Size(max = 10, min = 10)
    private String phone;
    @NotNull(message = "password cannot be null")
    @Size(min = 4, max = 20, message = "Password must be between 8 and 20 characters")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$",
//            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    @NotNull(message = "username cannot be null")
    private String role;
    @NotNull(message = "latitude cannot be null")
    private Double latitude;
    @NotNull(message = "longitude cannot be null")
    private Double longitude;
    @NotNull(message = "address cannot be null")
    private String address;
}
