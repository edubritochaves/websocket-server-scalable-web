package com.chavessummer.websocket.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.chavessummer.websocket.domain.NotifyData;

@CrossOrigin(allowCredentials = "true", allowedHeaders = "true", origins = "*")
@Controller
public class WebSocketNotifyController {

    @MessageMapping("/notify")
    @SendTo("/topic/notify")
    NotifyData notify(NotifyData message) {
        return message;
    }

}
