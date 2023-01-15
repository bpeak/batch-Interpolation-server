package com.bpeak.interpolserver.websocket;

import com.bpeak.interpolserver.model.Move;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class BpeakWebsocketHandler extends TextWebSocketHandler {
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final EventBroadcaster eventBroadcaster;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = (String) session.getAttributes().get("id");
        String msg = message.getPayload();
        log.info("user message recv. id={}, message={}", userId, msg);

        Move move = objectMapper.readValue(msg, Move.class);
        move.setId(userId);

        log.info("user_move_request={}", move);

        eventBroadcaster.sendMove(move);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("id");
        sessionManager.addSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("id");
        log.info("user disconnected. user_id={}", userId);
        sessionManager.deleteSession(userId);
        eventBroadcaster.sendLeft(userId);
    }
}
