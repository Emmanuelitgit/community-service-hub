package com.community_service_hub.user_service.authentication;

import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findUserByEmail(username);
        if (userOptional.isEmpty()){
           throw new UsernameNotFoundException("Invalid credentials");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(userOptional.get().getPassword())
                .build();
    }
}
