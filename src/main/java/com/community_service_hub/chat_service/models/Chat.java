package com.community_service_hub.chat_service.models;

import lombok.Data;

@Data
public class Chat {
    private String from;
    private String to; // Only used for private messaging
    private String content;
    private String type; // Optional: "PUBLIC", "PRIVATE"
}
