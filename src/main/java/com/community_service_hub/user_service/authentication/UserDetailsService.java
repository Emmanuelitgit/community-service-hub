package com.community_service_hub.user_service.authentication;

import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;
    private final NGORepo ngoRepo;

    @Autowired
    public UserDetailsService(UserRepo userRepo, NGORepo ngoRepo) {
        this.userRepo = userRepo;
        this.ngoRepo = ngoRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findUserByEmail(username);
        NGO ngo = ngoRepo.findByEmail(username);

        if (userOptional.isEmpty() && ngo==null){
           throw new UsernameNotFoundException("Invalid credentials");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(userOptional.isPresent()?userOptional.get().getEmail():ngo.getEmail())
                .password(userOptional.isPresent()?userOptional.get().getPassword():ngo.getPassword())
                .build();
    }
}
