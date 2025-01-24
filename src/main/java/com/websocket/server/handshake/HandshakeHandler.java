package com.websocket.server.handshake;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import com.websocket.server.exception.HandshakeException;
import com.websocket.server.http.HttpStatus;

public class HandshakeHandler {

    private boolean validateHeaders(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            ClientHandshakeRequest header = new ClientHandshakeRequest();
            String nextLine;
            if ((nextLine = reader.readLine()) == null) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Empty HTTP handshake request");
            }
            header.setHeaderLine(nextLine);
            
            // Validate required header fields
            while ((nextLine = reader.readLine()) != null) {
                if (nextLine.isEmpty()) {
                    break;
                }
                String[] line = nextLine.split(":", 2);
                if (line.length != 2) {
                    throw new HandshakeException(HttpStatus.BAD_REQUEST, "Invalid header field line");
                }
                String headerField = line[0];
                String headerValue = line[1];
                switch (headerField) {
                    case "Host":
                        header.setHost(headerValue);
                        break;
                    case "Upgrade":
                        header.setUpgrade(headerValue);
                        break;
                    case "Connection":
                        header.setConnection(headerValue);
                        break;
                    case "Sec-WebSocket-Key":
                        header.setSecWebsocketKey(headerValue);
                        break;
                    case "Sec-WebSocket-Version":
                        header.setSecWebsocketVersion(headerValue);
                    // TODO: Handle optional header fields and other header fields
                }
                
            }
            // TODO: Handle all required headers not null
        } catch (Exception e) { 
            return false;
        }
        return true;
    }

    private void returnHandshakeResponse(Socket socket) {
        
    }
}
