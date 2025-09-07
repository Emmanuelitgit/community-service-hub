package com.community_service_hub.user_service.dto;

import com.community_service_hub.user_service.models.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    /**
     * @description this method takes user object and transform it to userDTO
     * @param user
     * @return
     */
    public UserDTO toUserDTO(User user){
       return UserDTO
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getUserRole())
                .phone(user.getPhone())
                .build();
    }


    /**
     * @description his method takes userDTO object and transform it to user entity
     * @param user
     * @return
     */
    public User toUserEntity(UserPayloadDTO user){
        return User
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .id(user.getId())
                .name(user.getName())
                .userRole(user.getRole())
                .build();
    }

}
