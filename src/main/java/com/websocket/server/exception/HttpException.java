package com.websocket.server.exception;

import com.websocket.server.constants.HttpStatus;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {

    private HttpStatus httpStatus; 

    public HttpException(HttpStatus status, String message) {
        super(message);
    }

    public HttpException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
    }
}
