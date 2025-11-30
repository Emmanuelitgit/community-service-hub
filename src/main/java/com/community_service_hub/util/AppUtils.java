package com.community_service_hub.util;
import com.community_service_hub.task_service.models.Task;
import com.community_service_hub.task_service.repo.TaskRepo;
import com.community_service_hub.user_service.dto.PaginationPayload;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.dto.UserDTOProjection;
import com.community_service_hub.exception.NotFoundException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.RoleSetupRepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.repo.UserRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Slf4j
@Component
public class AppUtils {

    private final RoleSetupRepo roleSetupRepo;
    private final UserRoleRepo userRoleRepo;
    private final UserRepo userRepo;
    private final NGORepo ngoRepo;
    private final TaskRepo taskRepo;

    @Autowired
    public AppUtils(RoleSetupRepo roleSetupRepo, UserRoleRepo userRoleRepo, UserRepo userRepo, NGORepo ngoRepo, TaskRepo taskRepo) {
        this.roleSetupRepo = roleSetupRepo;
        this.userRoleRepo = userRoleRepo;
        this.userRepo = userRepo;
        this.ngoRepo = ngoRepo;
        this.taskRepo = taskRepo;
    }


    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status){
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        return responseDto;
    }

    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @param data
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status, Object data){
        if(data==null){
            ResponseDTO responseDto = getResponseDto(message, status);
            return responseDto;
        }
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        responseDto.setData(data);
        return responseDto;
    }

    /**
     * This method is used to get authenticated user role and cache it.
     * @param username
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 18h May 2025
     */
    @Cacheable(cacheNames = "userRole")
    public String getUserRole(String username) {
        UserDTOProjection role = userRepo.getUserRole(username);
        log.info("fetched role from db->>>>>");
        return role.getUserRole();
    }

    /**
     * This method is used to set authenticated user authorities.
     * @param userId
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public void setAuthorities(Object userId, String username) {
        Optional<User> user = userRepo.findById(UUID.fromString(userId.toString()));
        Optional<NGO> ngo = ngoRepo.findById(UUID.fromString(userId.toString()));
        if (user.isEmpty()&&ngo.isEmpty()){
            log.info("User record not found with the provided id->>>{}", userId.toString());
            throw new NotFoundException("User record not found");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                user.map(value -> value.getUserRole().toUpperCase()).orElseGet(() -> ngo.get().getRole().toUpperCase()));
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(authority);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId, null, grantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * This method is used to check user authority levels to a resource.
     * @param userId this is used when the requester is just a normal user like VOLUNTEER
     * @param taskId this is used when the requester is NGO. it used to load the NGO details
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 4TH August 2025
     */
    public Boolean isUserAuthorized(UUID userId, UUID taskId){
        /**
         * loading authenticated user authority
         */
        log.info("Authenticated user id->>>{}", getAuthenticatedUserId());
        UUID authenticatedUserId = UUID.fromString(getAuthenticatedUserId());
        String authenticatedUserRole = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .toString();

        if (userId!=null&&taskId!=null) {
            Optional<Task> taskOptional = taskRepo.findById(taskId);
            if (
                    (authenticatedUserRole.equalsIgnoreCase("ADMIN") || authenticatedUserId.equals(userId))||
                            (authenticatedUserRole.equalsIgnoreCase("ADMIN") || authenticatedUserId.equals(taskOptional.get().getPostedBy()))
            ){
                return Boolean.TRUE;
            }else {
                return Boolean.FALSE;
            }

        } else if (taskId != null){
            Optional<Task> taskOptional = taskRepo.findById(taskId);
            return authenticatedUserRole.equalsIgnoreCase("ADMIN") || authenticatedUserId.equals(taskOptional.get().getPostedBy());

        }else if (userId != null){
            return authenticatedUserRole.equalsIgnoreCase("ADMIN") || authenticatedUserId.equals(userId);
        }

        return Boolean.TRUE;
    }

    /**
     * @description this method is used to get the authenticated user role
     * @return role of the user
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    public static String getAuthenticatedUserRole(){
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst().get()
                .toString()
                .toUpperCase();
    }

    /**
     * This method is used to get user full name.
     * @param first
     * @param last
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static String getFullName(String first, String last){
        return first + " " + " " + last;
    }

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PAGE_SORT = "createdAt";
    public static final String DEFAULT_PAGE_SORT_DIR = "desc";

    /**
     * This method is used to set or handle pagination items.
     * @param paginationPayload
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static Pageable getPageRequest(PaginationPayload paginationPayload){
        return PageRequest.of(paginationPayload.getPage()-1, paginationPayload.getSize());
    }

    /**
     * @description a method to convert a string date to local date on the (yyyy-MM-dd) date format
     * @param startDate
     * @return
     */
    public static LocalDate convertStringToLocalDateTime(String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(startDate, formatter);
    }

    /**
     * This method is used to get authenticated user id.
     * @return UUID string
     * @auther Emmanuel Yidana
     * @createdAt 19h May 2025
     */
    public String getAuthenticatedUserId(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static final ExampleMatcher SEARCH_CONDITION_MATCH_ALL = ExampleMatcher.matchingAll()
            .withMatcher("price", exact())
            .withIgnorePaths("id", "createdBy", "updatedBy", "createdAt", "updatedAt")
            .withMatcher("name", contains().ignoreCase());

    public static final ExampleMatcher SEARCH_CONDITION_MATCH_ANY = ExampleMatcher.matchingAny()
            .withMatcher("price", exact())
            .withIgnorePaths("id", "createdBy", "updatedBy", "createdAt", "updatedAt")
            .withMatcher("name", contains().ignoreCase());
}
