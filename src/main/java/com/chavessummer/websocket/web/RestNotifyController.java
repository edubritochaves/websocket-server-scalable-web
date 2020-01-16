package com.chavessummer.websocket.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chavessummer.websocket.domain.NotifyData;
import com.chavessummer.websocket.service.WebSocketStompSessionService;

import lombok.AllArgsConstructor;

@CrossOrigin(allowCredentials = "true", allowedHeaders = "true", origins = "*")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RestNotifyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestNotifyController.class);

    private WebSocketStompSessionService stompSession;

    @PostMapping
    ResponseEntity<String> notify(@RequestBody NotifyData requestBody) {
        String taskName = getClass().getSimpleName() + ".notify()";
        StopWatch stopWatch = new StopWatch(taskName);
        LOGGER.info("ENTER: {}...", taskName);
        LOGGER.info("DATA: {}", requestBody);

        try {
            stopWatch.start(taskName);
            stompSession.send("/topic/notify", requestBody);
        } catch (Throwable t) {
            LOGGER.error("", t);
            return ResponseEntity.badRequest().body("ERROR");
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LOGGER.info("EXIT: {} - {}ms", taskName, stopWatch.getTotalTimeMillis());
        }

        return ResponseEntity.ok("SUCCESS");
    }
}
