package com.websocket.server.handshake;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HandshakeResponse {
    private String headerLine;
    private String upgrade;
    private String connection;
    private String secWebsocketAccept;

    public HandshakeResponse(String secWebsocketKey) {
        headerLine = "HTTP/1.1 101 Switching Protocols";
        upgrade = "websocket";
        connection = "Upgrade";
        secWebsocketAccept = buildSecWebsocketAccept(secWebsocketKey);
    }

    private String buildSecWebsocketAccept(String secWebsocketKey) {
        final String MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] sha1HashKey = messageDigest.digest(secWebsocketKey.concat(MAGIC_STRING)
            .getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sha1HashKey);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s\r\n", headerLine));
        builder.append(String.format("Upgrade: %s\r\n", upgrade));
        builder.append(String.format("Connection: %s\r\n", connection));
        builder.append(String.format("Sec-WebSocket-Accept: %s\r\n", secWebsocketAccept));
        builder.append("\r\n");
        return builder.toString();
    }
}
