package com.chavessummer.websocket.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("ws-notify.web-socket")
@Data
public class WebSocketProperties {

    private String masterUrl;
    private long initialConnect;
    private long retryTimeout;

}
