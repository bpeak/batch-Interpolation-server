package com.bpeak.interpolserver.websocket;

import com.bpeak.interpolserver.model.Move;
import com.bpeak.interpolserver.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventBroadcaster {
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
    private final ArrayList<Move> moveList = new ArrayList<>();
    private final ArrayList<String> leftUserIds = new ArrayList<>();
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() -> {
            ArrayList<Move> copiedMoveList;
            ArrayList<String> copiedLeftUserIds;
            try {
                writeLock.lock();
                copiedMoveList = (ArrayList<Move>) moveList.clone();
                copiedLeftUserIds = (ArrayList<String>) leftUserIds.clone();
                moveList.clear();
                leftUserIds.clear();
            } finally {
                writeLock.unlock();
            }
            if (!copiedMoveList.isEmpty() || !copiedLeftUserIds.isEmpty()) {
                // 네트워크 레이턴시 재현 (300ms)
                scheduler2.schedule(() -> {
                    sessionManager.getAllSessions().forEach(webSocketSession -> {
                        try {
                            String msg = objectMapper.writeValueAsString(new Response(copiedMoveList, copiedLeftUserIds));
                            webSocketSession.sendMessage(new TextMessage(msg));
                        } catch (IOException e) {
                            log.info("websocket send message error.", e);
                        }
                    });
                }, 300, TimeUnit.MILLISECONDS);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void sendMove(Move move) {
        try {
            writeLock.lock();
            moveList.add(move);
        } finally {
            writeLock.unlock();
        }
    }

    public void sendLeft(String userId) {
        try {
            writeLock.lock();
            leftUserIds.add(userId);
        } finally {
            writeLock.unlock();
        }
    }
}
