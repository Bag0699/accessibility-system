package com.bag.accessibility_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.stream.Stream;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.cors.origins}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para enviar mensajes a los clientes
        // Los clientes se suscribirán a rutas que empiecen con /topic
        config.enableSimpleBroker("/topic");
        
        // Prefijo para los mensajes que el cliente envía al servidor
        // (ej. el docente enviará a /app/transcription/...)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = Stream.of(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
        // Registramos el endpoint principal puro para clientes WebSocket modernos
        registry.addEndpoint("/ws")
                .setAllowedOrigins(origins);

        // Registramos un endpoint secundario con soporte SockJS como fallback
        // por si hay redes restrictivas o clientes antiguos
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOrigins(origins)
                .withSockJS();
    }
}
