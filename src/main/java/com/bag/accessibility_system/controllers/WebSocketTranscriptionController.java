package com.bag.accessibility_system.controllers;

import com.bag.accessibility_system.dtos.request.TranscriptionMessageRequest;
import com.bag.accessibility_system.dtos.response.TranscriptionResponse;
import com.bag.accessibility_system.services.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketTranscriptionController {

    private final TranscriptionService transcriptionService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Escucha mensajes en el destino STOMP: /app/transcription/{sessionCode}
     */
    @MessageMapping("/transcription/{sessionCode}")
    public void handleTranscription(
            @DestinationVariable String sessionCode,
            @Payload TranscriptionMessageRequest request
    ) {
        try {
            // 1. Guardamos el fragmento en PostgreSQL de forma persistente
            TranscriptionResponse response = transcriptionService.saveTranscriptionFragment(
                    sessionCode, 
                    request.text(), 
                    request.startTime(), 
                    request.endTime()
            );

            // 2. Retransmitimos de inmediato a todos los suscriptores (alumnos)
            // que estén escuchando en el tópico: /topic/session/{sessionCode}
            String topicDestination = "/topic/session/" + sessionCode;
            messagingTemplate.convertAndSend(topicDestination, response);
            
            log.debug("Mensaje retransmitido a {}: {}", topicDestination, request.text());
            
        } catch (Exception e) {
            // Capturamos cualquier excepción (ej. ResourceNotFound si la sesión no existe o terminó)
            // para que no rompa el hilo de STOMP. En la práctica, se podría devolver un error 
            // a un tópico privado del docente (/user/queue/errors).
            log.error("Error al procesar mensaje STOMP para la sesión {}: {}", sessionCode, e.getMessage());
        }
    }
}
