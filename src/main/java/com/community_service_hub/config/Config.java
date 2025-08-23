package com.community_service_hub.config;

import com.community_service_hub.user_service.authentication.CustomFilter;
import com.community_service_hub.user_service.authentication.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties
@EnableWebSecurity
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
@EnableMethodSecurity(
        securedEnabled = true, // For @Secured
        prePostEnabled = true  // For @PreAuthorize and @PostAuthorize
)
public class Config {

    private final UserDetailsService userDetailsService;
    private final CustomFilter customFilter;
    private final CorsConfiguration corsConfiguration;

    @Value("${EMAIL_USERNAME}")
    private String EMAIL_USERNAME;
    @Value("${EMAIL_PASSWORD}")
    private String EMAIL_PASSWORD;

    @Autowired
    public Config(UserDetailsService userDetailsService, CustomFilter customFilter, CorsConfiguration corsConfiguration) {
        this.userDetailsService = userDetailsService;
        this.customFilter = customFilter;
        this.corsConfiguration = corsConfiguration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests((auth)->{
            auth
                    .requestMatchers("/swagger-ui/*", "/api/v1/users/keep-server-alive")
                    .permitAll();
            auth.anyRequest().permitAll();
        })
                .csrf((AbstractHttpConfigurer::disable))
                .cors(c -> c.configurationSource(corsConfiguration))
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    AuditorAware auditorAware(){
        return new AuditorAwareImpl();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }

    @Bean
    AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
