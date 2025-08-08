package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for TestOrder service
 * Enables real-time notifications via STOMP over WebSocket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for server-to-client messages (Server -> Client)
        // Frontend will subscribe to topics with this prefix
        config.enableSimpleBroker("/topic");

        // Prefix for client-to-server messages (Client -> Server)
        // Frontend will send messages with this prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // General WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:3000")
                .withSockJS();

        // TestOrder specific WebSocket endpoint
        registry.addEndpoint("/testorder/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}