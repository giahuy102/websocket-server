package com.websocket.server.framing;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.websocket.server.constants.WebsocketStatus;
import com.websocket.server.exception.DataFrameException;
import com.websocket.server.socket.SocketConnection;

public class DataFrameHandler {

    private SocketConnection connection;
    private List<DataFrame> fragments;

    public DataFrameHandler(SocketConnection connection) {
        this.connection = connection;
        fragments = new LinkedList<>();
    }
    
    public void parseClientFrame() throws IOException {
        DataFrame frame = new DataFrame(true);
        DataInputStream inputStream = connection.getDataInputStream();
        byte octet = inputStream.readByte();
        // TODO: Handle RSV bits later (see specification)
        byte fin = (byte)(octet >> 7);
        byte opcode = (byte)(octet & 0x0f);
        if (existPreviousFragment() && opcode != 0x0) {
            throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "A continue fragment must have opcode = 0x0");
        }
        if (!existPreviousFragment() && opcode != 0x1) {
            if (opcode == 0x0) {
                throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Can't be a continue fragment");
            }
            throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Server currently only support text payload");
        }
        frame.setIsFinalFragment(fin == 0x1 ? true : false);
        
        octet = inputStream.readByte();
        byte mask = (byte)(octet >> 7);
        frame.setIsMark(mask == 1 ? true : false);
    }

    private boolean existPreviousFragment() {
        return fragments.size() > 0;
    }
}
