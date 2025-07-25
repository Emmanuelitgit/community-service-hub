package com.community_service_hub.user_service.authentication;

import com.community_service_hub.user_service.exception.UnAuthorizeException;
import com.community_service_hub.user_service.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomFilter extends OncePerRequestFilter {

    private final JWTAccess jwtAccess;
    private final AppUtils appUtils;

    @Autowired
    public CustomFilter(JWTAccess jwtAccess, AppUtils appUtils) {
        this.jwtAccess = jwtAccess;
        this.appUtils = appUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try {
           String auth = request.getHeader("Authorization");
           if (auth!=null){
               String token = auth.substring(7);
               jwtAccess.isTokenValid(token);
               Object userId = jwtAccess.extractUserId(token);
               appUtils.setAuthorities(userId);
           }
           filterChain.doFilter(request, response);

       }catch (UnAuthorizeException ex) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.setContentType("application/json");
           Map<String, Object> res = new HashMap<>();
           res.put("message", ex.getMessage());
           res.put("statusCode", HttpStatus.valueOf(401));
           res.put("date", new Date());
           ObjectMapper mapper = new ObjectMapper();
           String responseData = mapper.writeValueAsString(res);
           response.getWriter().write(responseData);
       }
    }
}
