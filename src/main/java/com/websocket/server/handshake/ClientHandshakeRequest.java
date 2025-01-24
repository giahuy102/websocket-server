package com.websocket.server.handshake;

import java.util.Base64;
import java.util.regex.Pattern;

import com.websocket.server.config.ServerConfig;
import com.websocket.server.exception.HandshakeException;
import com.websocket.server.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientHandshakeRequest {

    private final String REQUEST_LINE_PATTERN = "GET (((/[a-zA-Z]+)+/?)|(/[a-zA-Z]*)) HTTP/(1\\.1|2\\.0|3\\.0)";

    private String headerLine;
    private String host;
    private String upgrade;
    private String connection;
    private String secWebsocketKey;
    private String secWebsocketVersion;

    public void setHeaderLine(String headerLine) {
        // TODO: Need to validate the specific route
        Pattern patternRegex = Pattern.compile(REQUEST_LINE_PATTERN);
        if (!patternRegex.matcher(headerLine).matches()) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Request line: Invalid format");
        }
        this.headerLine = headerLine;
    }

    public void setHost(String host) {
        String[] domainAndPort = host.split(":", 2);
        String domain;
        int port;
        domain = domainAndPort[0];
        if (domain != ServerConfig.HOST) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Host header field: Incorrect domain");
        }
        if (domainAndPort.length == 2) {
            domain = domainAndPort[0];
            try {
                port = Integer.parseInt(domainAndPort[1]);
            } catch (NumberFormatException e) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Host header field: Invalid port value format");
            }
            if (port != ServerConfig.PORT) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Host header field: Incorrect port");
            }
        }

        this.host = host;
    }

    public void setUpgrade(String upgrade) {
        if (upgrade != "websocket") {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Upgrade header field: Must be websocket");
        }
    }

    public void setConnection(String connection) {
        if (connection != "Upgrade") {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Upgrade header field: Must be Upgrade");
        }
    }

    public void setSecWebsocketKey(String secWebsocketKey) {
        try {
            Base64.getDecoder().decode(secWebsocketKey);
        } catch (IllegalArgumentException e) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Sec-WebSocket-Key header field: Must in valid base64 encoded format");
        }
    }

    public void setSecWebsocketVersion(String secWebsocketVersion) {
        int version;
        try {
            version = Integer.parseInt(secWebsocketVersion);
        } catch (NumberFormatException e) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Sec-WebSocket-Version header field: Invalid version format");
        }
        if (version != ServerConfig.WEBSOCKET_VERSION) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Sec-WebSocket-Version header field: Not supported version");
        }
    }
}
