package com.chavessummer.websocket.service;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.chavessummer.websocket.domain.NotifyData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebSocketStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketStompSessionHandler.class);

    private SimpMessageSendingOperations messagingTemplate;

    private WebSocketStompSessionService sessionService;

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.info("handleException");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return NotifyData.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.info("handleFrame");
        NotifyData data = (NotifyData) payload;
        messagingTemplate.convertAndSend(data.getTopic(), data.getData());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.info("handleTransportError");
        if (exception instanceof ConnectionLostException) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sessionService.getConnect();
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.error("", e);
                    }
                }
            }).start();
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        LOGGER.info("afterConnected");
        session.subscribe("/topic/notify", this);
    }

}
