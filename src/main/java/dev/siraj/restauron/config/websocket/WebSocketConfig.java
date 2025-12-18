package dev.siraj.restauron.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


// WebSocket configuration class

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Register STOMP endpoints for WebSocket connections
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws-restauron")
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }

    // Configure message broker for handling messages
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //prefixes for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");

        //prefixes for messages FROM server TO client (Broadcasting and Point-to-Point)
        registry.enableSimpleBroker("/topic/", "/queue/", "/user/");

        //prefix for user-specific messages
        registry.setUserDestinationPrefix("/user/");
    }
}
