package com.websocket.server;

import java.net.ServerSocket;

import com.websocket.server.config.ServerConfig;
import com.websocket.server.handshake.HandshakeHandler;
import com.websocket.server.socket.SocketConnection;

public class Application {

	public static void main(String[] args) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT);
			SocketConnection socketConnection = new SocketConnection(serverSocket.accept())) {
			HandshakeHandler handler = new HandshakeHandler();
			handler.handle(socketConnection);
		}
	}
}
