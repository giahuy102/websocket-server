package com.websocket.server.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import lombok.Getter;

/*
    A separate class to manage socket resources, because closing a high level
    resource make a lower level resource be closed. Ex: Closing a BufferedReader
    make a socket be closed, so a PrintWriter can not be used anymore (alghough it's
    not called close method specifically) -> Have to call close on BufferedReader and
    PrintWriter together
*/
@Getter
public class SocketConnection implements AutoCloseable {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private InputStreamReader inputStreamReader;
    private OutputStreamWriter outputStreamWriter;

    public SocketConnection(Socket socket) throws IOException {
        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        outputStream = socket.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(outputStream);
        printWriter = new PrintWriter(outputStreamWriter);
    }

    @Override
    public void close() throws Exception {
        bufferedReader.close();
        printWriter.close();
    }
}
