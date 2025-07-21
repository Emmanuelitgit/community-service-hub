package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.dto.UserDTOProjection;
import com.community_service_hub.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    @Query(value = "SELECT u.id AS id, u.first_name, u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tb u " +
            "JOIN user_role_tb ur ON u.id = ur.user_id " +
            "JOIN role_setup_tb rs ON ur.role_id = rs.id", nativeQuery = true)
    List<UserDTOProjection> getUsersDetails();

    @Query(value = "SELECT u.id AS id, u.first_name, u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tb u " +
            "JOIN user_role_tb ur ON u.id = ur.user_id " +
            "JOIN role_setup_tb rs ON ur.role_id = rs.id " +
            "WHERE u.id = ?1", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserId(UUID userId);

    @Query(value = "SELECT u.id AS id, u.first_name, u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tb u " +
            "JOIN user_role_tb ur ON u.id = ur.user_id " +
            "JOIN role_setup_tb rs ON ur.role_id = rs.id " +
            "WHERE u.email = ?1", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserEmail(String email);

    @Query(value = "SELECT rs.name AS role " +
            "FROM user_role_tb ur " +
            "JOIN user_tb u ON u.id = ur.user_id " +
            "JOIN role_setup_tb rs ON rs.id = ur.role_id " +
            "WHERE u.email = ?1", nativeQuery = true)
    UserDTOProjection getUserRole(String username);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);
}