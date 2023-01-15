package com.bpeak.interpolserver.config;

import com.bpeak.interpolserver.websocket.BpeakHandshakeInterceptor;
import com.bpeak.interpolserver.websocket.BpeakWebsocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final BpeakHandshakeInterceptor bpeakHandshakeInterceptor;
    private final BpeakWebsocketHandler bpeakWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(bpeakWebsocketHandler, "/ws")
                .setAllowedOrigins("*")
                .addInterceptors(bpeakHandshakeInterceptor);
    }

}
