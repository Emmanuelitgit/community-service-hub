//package com.community_service_hub.chat_service.rest;
//
//import com.community_service_hub.chat_service.models.Chat;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//
//import java.security.Principal;
//
//public class ChatController {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    // Public broadcast: everyone subscribed to /topic/public will receive it
//    @MessageMapping("/public-message")
//    public void sendPublicMessage(@Payload Chat message) {
//        messagingTemplate.convertAndSend("/topic/public", message);
//    }
//
//    // Private message: only the target user will receive it
//    @MessageMapping("/private-message")
//    public void sendPrivateMessage(@Payload Chat message, Principal principal) {
//        String sender = principal.getName(); // Authenticated user's name
//        message.setFrom(sender);
//
//        messagingTemplate.convertAndSendToUser(
//                message.getTo(),        // recipient username
//                "/queue/messages",      // destination the client is subscribed to
//                message
//        );
//    }
//
//}
