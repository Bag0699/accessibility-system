package com.bag.accessibility_system.websocket;

import com.bag.accessibility_system.dtos.response.SessionPresenceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SessionPresenceTrackerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private SessionPresenceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new SessionPresenceTracker(messagingTemplate);
    }

    @Test
    void tracksUniqueStudentConnectionsAcrossSubscribeUnsubscribeAndDisconnect() {
        tracker.handleSessionSubscribe(new SessionSubscribeEvent(
                this,
                stompMessage(StompCommand.SUBSCRIBE, "ws-1", "sub-1", "/topic/session/ABC-123")
        ));
        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(1L);
        verify(messagingTemplate).convertAndSend("/topic/session/ABC-123/presence", new SessionPresenceResponse(1L));

        // Second subscription from the same WebSocket connection should still count as 1
        tracker.handleSessionSubscribe(new SessionSubscribeEvent(
                this,
                stompMessage(StompCommand.SUBSCRIBE, "ws-1", "sub-2", "/topic/session/ABC-123")
        ));
        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(1L);

        // Second student joins from another WebSocket connection
        tracker.handleSessionSubscribe(new SessionSubscribeEvent(
                this,
                stompMessage(StompCommand.SUBSCRIBE, "ws-2", "sub-1", "/topic/session/ABC-123")
        ));
        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(2L);
        verify(messagingTemplate).convertAndSend("/topic/session/ABC-123/presence", new SessionPresenceResponse(2L));

        // First student disconnects
        tracker.handleSessionDisconnect(new SessionDisconnectEvent(
                this,
                stompMessage(StompCommand.DISCONNECT, "ws-1", null, null),
                "ws-1",
                CloseStatus.NORMAL
        ));
        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(1L);

        // Second student unsubscribes
        tracker.handleSessionUnsubscribe(new SessionUnsubscribeEvent(
                this,
                stompMessage(StompCommand.UNSUBSCRIBE, "ws-2", "sub-1", null)
        ));
        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(0L);
        verify(messagingTemplate).convertAndSend("/topic/session/ABC-123/presence", new SessionPresenceResponse(0L));
    }

    @Test
    void ignoresAuxiliarySessionSubtopics() {
        tracker.handleSessionSubscribe(new SessionSubscribeEvent(
                this,
                stompMessage(StompCommand.SUBSCRIBE, "ws-teacher", "sub-1", "/topic/session/ABC-123/presence")
        ));
        tracker.handleSessionSubscribe(new SessionSubscribeEvent(
                this,
                stompMessage(StompCommand.SUBSCRIBE, "ws-student", "sub-2", "/topic/session/ABC-123/status")
        ));

        assertThat(tracker.getConnectedCount("ABC-123")).isEqualTo(0L);
        verifyNoInteractions(messagingTemplate);
    }

    private Message<byte[]> stompMessage(
            StompCommand command,
            String sessionId,
            String subscriptionId,
            String destination
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setSessionId(sessionId);
        if (subscriptionId != null) {
            accessor.setSubscriptionId(subscriptionId);
        }
        if (destination != null) {
            accessor.setDestination(destination);
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
