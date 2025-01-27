package com.websocket.server.exception;

import com.websocket.server.constants.HttpStatus;

public class HandshakeException extends HttpException {

    public HandshakeException(HttpStatus status, String message) {
        super(status, message);
    }
}
