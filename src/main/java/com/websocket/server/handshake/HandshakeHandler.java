package com.websocket.server.handshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.websocket.server.exception.HandshakeException;
import com.websocket.server.http.HttpStatus;
import com.websocket.server.socket.SocketConnection;

public class HandshakeHandler {

    private ClientHandshakeRequest buildAndValidateClientRequest(SocketConnection connection) throws IOException {
        ClientHandshakeRequest request = new ClientHandshakeRequest();
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

    private void returnResponse(SocketConnection connection, String clientSecWebsocketKey) {
        ServerHandshakeResponse response = new ServerHandshakeResponse(clientSecWebsocketKey);
        PrintWriter writer = connection.getPrintWriter();
        writer.print(response.toString());
        writer.flush();
    }

    public void handle(SocketConnection connection) throws IOException {
        ClientHandshakeRequest request = buildAndValidateClientRequest(connection);
        returnResponse(connection, request.getSecWebsocketKey());
    }
}
