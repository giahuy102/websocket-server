package com.websocket.server.exception;

import com.websocket.server.http.HttpStatus;

public class HttpException extends RuntimeException {

    public HttpException(HttpStatus status, String message) {
        super(message);
    }

    public HttpException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
    }
}
