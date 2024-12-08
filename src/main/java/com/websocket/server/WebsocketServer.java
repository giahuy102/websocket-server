package com.websocket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class WebsocketServer {

    private static final int PORT = 8080;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            clientSocket = serverSocket.accept();
        } catch (Exception ex) {
            System.out.println("An exception occurs, exception is: " + ex);
        } finally {
            try {
                serverSocket.close();
                if (clientSocket != null) {
                    clientSocket.close();;
                }
            } catch (IOException ex) {
                System.out.println("An IO exception occurs during close the socket, exception is: " + ex);
            }
        }
    }

    private boolean sendHandshake(Socket socket) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String nextLine;
            if ((nextLine = reader.readLine()) == null) {
                return false;
            }
            Pattern pattern = Pattern.compile("GET (((/[a-zA-Z]+)+/?)|(/[a-zA-Z]*)) HTTP/(1\\.1|2\\.0|3\\.0)");

        } catch (Exception ex) {
            System.out.println("An exception occurs during close the socket, exception is: " + ex);
            return false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                System.out.println("An IO exception occurs during close the socket, exception is: " + ex);
            }
        }
        return true;
    }
}
