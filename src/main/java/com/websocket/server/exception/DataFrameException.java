package com.websocket.server.exception;

import com.websocket.server.constants.WebsocketStatus;

import lombok.Getter;

@Getter
public class DataFrameException extends RuntimeException {

    private WebsocketStatus websocketStatus;

    public DataFrameException(WebsocketStatus status, String message) {
        super(message);
    }
}
