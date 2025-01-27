package com.websocket.server.constants;

public enum WebsocketStatus {
    CLOSE_NORMAL(1000, "Close normal"),
    CLOSE_GOING_AWAY(1001, "Close going away"),
    CLOSE_PROTOCOL_ERROR(1002, "Close protocol error");

    private final int code;
    private final String description;

    WebsocketStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
