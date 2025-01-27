package com.websocket.server.handshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.websocket.server.constants.HttpStatus;
import com.websocket.server.exception.HandshakeException;
import com.websocket.server.socket.SocketConnection;

public class HandshakeHandler {

    private SocketConnection connection;

    public HandshakeHandler(SocketConnection connection) {
        this.connection = connection;
    }

    private HandshakeRequest buildAndValidateClientRequest() throws IOException {
        HandshakeRequest request = new HandshakeRequest();
        BufferedReader reader = connection.getBufferedReader();
        String nextLine;
        if ((nextLine = reader.readLine()) == null) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Empty HTTP handshake request");
        }
        request.setHeaderLine(nextLine);
        
        while ((nextLine = reader.readLine()) != null) {
            System.out.println(nextLine);
            if (nextLine.isEmpty()) {
                break;
            }
            String[] line = nextLine.split(":", 2);
            if (line.length != 2) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Invalid header field line");
            }
            String headerField = line[0].strip().toLowerCase();
            String headerValue = line[1].strip();
            switch (headerField) {
                case "host":
                    request.setHost(headerValue);
                    break;
                case "upgrade":
                    request.setUpgrade(headerValue);
                    break;
                case "connection":
                    request.setConnection(headerValue);
                    break;
                case "sec-websocket-key":
                    request.setSecWebsocketKey(headerValue);
                    break;
                case "sec-websocket-version":
                    request.setSecWebsocketVersion(headerValue);
                // TODO: Handle optional header fields and other header fields
            }
        }
        if (!request.containsAllRequiredHeaders()) {
            throw new HandshakeException(HttpStatus.BAD_REQUEST, "Handshake request must contain all required headers");
        }
        return request;
    }

    private void returnResponse(String clientSecWebsocketKey) {
        HandshakeResponse response = new HandshakeResponse(clientSecWebsocketKey);
        PrintWriter writer = connection.getPrintWriter();
        writer.print(response.toString());
        writer.flush();
    }

    public void handle() throws IOException {
        HandshakeRequest request = buildAndValidateClientRequest();
        returnResponse(request.getSecWebsocketKey());
    }
}
