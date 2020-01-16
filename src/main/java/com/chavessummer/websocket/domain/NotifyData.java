package com.chavessummer.websocket.domain;

import lombok.Data;

@Data
public class NotifyData {

    private String topic;
    private String data;
}
