package com.bag.accessibility_system.websocket;

import com.bag.accessibility_system.dtos.response.SessionPresenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Rastrea las conexiones STOMP suscritas al canal principal de cada sesión
 * y difunde en tiempo real la cantidad de estudiantes conectados.
 */
@Component
@RequiredArgsConstructor
public class SessionPresenceTracker {

    private static final String SESSION_TOPIC_PREFIX = "/topic/session/";

    private final SimpMessagingTemplate messagingTemplate;

    private final Object lock = new Object();
    // sessionCode -> (wsSessionId -> subscriptionIds)
    private final Map<String, Map<String, Set<String>>> sessionSubscribers = new HashMap<>();
    // wsSessionId -> (subscriptionId -> sessionCode)
    private final Map<String, Map<String, String>> wsSubscriptions = new HashMap<>();

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        String sessionCode = extractSessionCode(accessor.getDestination());

        if (wsSessionId == null || subscriptionId == null || sessionCode == null) {
            return;
        }

        long updatedCount;
        synchronized (lock) {
            wsSubscriptions
                    .computeIfAbsent(wsSessionId, k -> new HashMap<>())
                    .put(subscriptionId, sessionCode);

            sessionSubscribers
                    .computeIfAbsent(sessionCode, k -> new HashMap<>())
                    .computeIfAbsent(wsSessionId, k -> new HashSet<>())
                    .add(subscriptionId);

            updatedCount = countLocked(sessionCode);
        }

        broadcastPresence(sessionCode, updatedCount);
    }

    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String wsSessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        if (wsSessionId == null || subscriptionId == null) {
            return;
        }

        String affectedCode = null;
        long updatedCount = 0;

        synchronized (lock) {
            Map<String, String> subs = wsSubscriptions.get(wsSessionId);
            if (subs == null) {
                return;
            }
            String sessionCode = subs.remove(subscriptionId);
            if (sessionCode == null) {
                return;
            }
            if (subs.isEmpty()) {
                wsSubscriptions.remove(wsSessionId);
            }
            removeSubscriptionLocked(sessionCode, wsSessionId, subscriptionId);
            affectedCode = sessionCode;
            updatedCount = countLocked(sessionCode);
        }

        broadcastPresence(affectedCode, updatedCount);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String wsSessionId = event.getSessionId();
        if (wsSessionId == null) {
            return;
        }

        Map<String, Long> updates = new HashMap<>();
        synchronized (lock) {
            Map<String, String> subs = wsSubscriptions.remove(wsSessionId);
            if (subs == null || subs.isEmpty()) {
                return;
            }
            for (Map.Entry<String, String> entry : subs.entrySet()) {
                String subscriptionId = entry.getKey();
                String sessionCode = entry.getValue();
                removeSubscriptionLocked(sessionCode, wsSessionId, subscriptionId);
                updates.put(sessionCode, countLocked(sessionCode));
            }
        }

        updates.forEach(this::broadcastPresence);
    }

    public long getConnectedCount(String sessionCode) {
        synchronized (lock) {
            return countLocked(sessionCode);
        }
    }

    private void removeSubscriptionLocked(String sessionCode, String wsSessionId, String subscriptionId) {
        Map<String, Set<String>> byWsSession = sessionSubscribers.get(sessionCode);
        if (byWsSession == null) {
            return;
        }
        Set<String> subIds = byWsSession.get(wsSessionId);
        if (subIds != null) {
            subIds.remove(subscriptionId);
            if (subIds.isEmpty()) {
                byWsSession.remove(wsSessionId);
            }
        }
        if (byWsSession.isEmpty()) {
            sessionSubscribers.remove(sessionCode);
        }
    }

    private long countLocked(String sessionCode) {
        Map<String, Set<String>> byWsSession = sessionSubscribers.get(sessionCode);
        return byWsSession == null ? 0L : byWsSession.size();
    }

    private void broadcastPresence(String sessionCode, long connectedCount) {
        messagingTemplate.convertAndSend(
                SESSION_TOPIC_PREFIX + sessionCode + "/presence",
                new SessionPresenceResponse(connectedCount)
        );
    }

    private String extractSessionCode(String destination) {
        if (destination == null || !destination.startsWith(SESSION_TOPIC_PREFIX)) {
            return null;
        }
        String remainder = destination.substring(SESSION_TOPIC_PREFIX.length());
        if (remainder.isEmpty() || remainder.contains("/")) {
            return null;
        }
        return remainder;
    }
}
