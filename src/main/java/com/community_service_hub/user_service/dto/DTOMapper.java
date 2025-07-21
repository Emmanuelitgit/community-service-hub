package com.community_service_hub.user_service.dto;

import com.community_service_hub.user_service.models.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    /* this method takes user object and transform it to userDTO*/
    public static UserDTO toUserDTO(User user, String role){
       return UserDTO
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(role)
                .phone(user.getPhone())
                .build();
    }


    public User toUserEntity(UserPayloadDTO user){
        return User
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}
