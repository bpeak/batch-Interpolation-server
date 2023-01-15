package com.bpeak.interpolserver.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class SessionManager {
    private final HashMap<String, WebSocketSession> userSessionMap = new HashMap<>();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();

    public void addSession(String userId, WebSocketSession webSocketSession) {
        try {
            writeLock.lock();
            userSessionMap.put(userId, webSocketSession);
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteSession(String userId) {
        try {
            writeLock.lock();
            userSessionMap.remove(userId);
        } finally {
            writeLock.unlock();
        }
    }

    public List<WebSocketSession> getAllSessions() {
        try {
            readLock.lock();
            return userSessionMap.values().stream().toList();
        } finally {
            readLock.unlock();
        }
    }
}
