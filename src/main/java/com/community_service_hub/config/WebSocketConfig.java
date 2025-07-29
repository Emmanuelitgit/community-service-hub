package com.community_service_hub.config;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * @description this method is used to specify the endpoint in which the client can establish a connection with
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS(); // WebSocket handshake endpoint
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // Enable public & private
        registry.setApplicationDestinationPrefixes("/app"); // Client sends here
        registry.setUserDestinationPrefix("/user"); // Internal routing for convertAndSendToUser
    }

}
