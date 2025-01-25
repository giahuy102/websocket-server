package com.websocket.server.handshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;

import com.websocket.server.exception.HandshakeException;
import com.websocket.server.http.HttpStatus;

public class HandshakeHandler {

    private ClientHandshakeRequest buildAndValidateClientRequest(Socket socket) {
        ClientHandshakeRequest request = new ClientHandshakeRequest();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String nextLine;
            if ((nextLine = reader.readLine()) == null) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Empty HTTP handshake request");
            }
            request.setHeaderLine(nextLine);
            
            while ((nextLine = reader.readLine()) != null) {
                if (nextLine.isEmpty()) {
                    break;
                }
                String[] line = nextLine.split(":", 2);
                if (line.length != 2) {
                    throw new HandshakeException(HttpStatus.BAD_REQUEST, "Invalid header field line");
                }
                String headerField = line[0].strip();
                String headerValue = line[1].strip();
                switch (headerField) {
                    case "Host":
                        request.setHost(headerValue);
                        break;
                    case "Upgrade":
                        request.setUpgrade(headerValue);
                        break;
                    case "Connection":
                        request.setConnection(headerValue);
                        break;
                    case "Sec-WebSocket-Key":
                        request.setSecWebsocketKey(headerValue);
                        break;
                    case "Sec-WebSocket-Version":
                        request.setSecWebsocketVersion(headerValue);
                    // TODO: Handle optional header fields and other header fields
                }
            }
            if (!request.containsAllRequiredHeaders()) {
                throw new HandshakeException(HttpStatus.BAD_REQUEST, "Handshake request must contain all required headers");
            }
        } catch (IOException e) { 
            throw new UncheckedIOException(e);
        }
        return request;
    }

    private void returnResponse(Socket socket, String clientSecWebsocketKey) {
        ServerHandshakeResponse response = new ServerHandshakeResponse(clientSecWebsocketKey);
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.println(response.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void handle(Socket socket) {
        ClientHandshakeRequest request = buildAndValidateClientRequest(socket);
        returnResponse(socket, request.getSecWebsocketKey());
    }
}
