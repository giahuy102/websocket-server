package com.websocket.server.exception;

import com.websocket.server.http.HttpStatus;

public class HandshakeException extends HttpException {

    public HandshakeException(HttpStatus status, String message) {
        super(status, message);
    }
}
