package com.community_service_hub.user_service.repo;

import com.community_service_hub.user_service.dto.UserDTO;
import com.community_service_hub.user_service.dto.UserDTOProjection;
import com.community_service_hub.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    @Query(value = "SELECT u.id, u.name, u.user_role, u.address, u.email, u.phone " +
            "FROM user_tb u ", nativeQuery = true)
    List<UserDTOProjection> getUsersDetails();

    @Query(value = "SELECT u.id, u.name, u.address, u.email, u.phone, u.user_role " +
            "FROM user_tb u " +
            "WHERE u.id = ?1", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserId(UUID userId);

    @Query(value = "SELECT u.id AS id, u.name, u.phone, u.user_role " +
            "FROM user_tb u " +
            "WHERE u.email = ?1", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserEmail(String email);

    @Query(value = "SELECT rs.name AS role " +
            "FROM user_role_tb ur " +
            "JOIN user_tb u ON u.id = ur.user_id " +
            "JOIN role_setup_tb rs ON rs.id = ur.role_id " +
            "WHERE u.email = ?1", nativeQuery = true)
    UserDTOProjection getUserRole(String email);

    Optional<User> findUserByEmail(String email);


    @Query(value = "SELECT u.name, u.id, u.email, u.phone, u.user_role, u.address FROM user_tb u " +
                    "JOIN applications_tb ap ON u.id=ap.applicant_id " +
                    "WHERE ap.task_id=:taskId AND ap.status='APPROVED' ", nativeQuery = true)
    List<UserDTOProjection> fetchListOfApprovedApplicantsForTask(@Param("taskId") UUID taskId);

    @Query(value = "SELECT COUNT(*) FROM user_tb", nativeQuery = true)
    Integer totalUsers();

    @Query(value = "SELECT COUNT(*) FROM user_tb WHERE created_at >= date_trunc('month', CURRENT_DATE)", nativeQuery = true)
    Integer totalCreatedUsersForTheMonth();
}