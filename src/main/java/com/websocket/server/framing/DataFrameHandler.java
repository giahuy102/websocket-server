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
                throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Current fragment can't be a continue fragment");
            }
            throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Server currently only support text payload");
        }
        frame.setIsFinalFragment(fin == 0x1 ? true : false);
        
        octet = inputStream.readByte();
        byte mask = (byte)(octet >> 7);
        frame.setIsMark(mask == 1 ? true : false);
        
        int initialLength = octet & 0x7f;
        if (initialLength <= 125) {
            frame.setPayloadLengthInBytes(initialLength);
        } else if (initialLength == 126) {
            short nextLength = inputStream.readShort();
            if (nextLength < 0) {
                // The most significant bit is 1 -> the number will be < 0 in 2's compliment
                throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Payload length (16 bits) must be unsinged integer");
            }
            frame.setPayloadLengthInBytes(nextLength);
        } else if (initialLength == 127) {
            // readLong is a blocking method. It will block the current thread until it receives all 8 bytes for long
            long nextLength = inputStream.readLong();
            if (nextLength < 0) {
                // The most significant bit is 1 -> the number will be < 0 in 2's compliment
                throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Payload length (64 bits) must be unsinged integer");
            }
            frame.setPayloadLengthInBytes(nextLength);
        } else {
            throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "The first 7 bits of payload length must be in range 0 to 127 inclusive");
        }

        if (frame.isMask()) {
            frame.setMaskingKey(inputStream.readInt());
        }

        long remainBytes = frame.getPayloadLengthInBytes();
        final int BUFFER_SIZE = 1024;
        int bytesToRead;
        byte[] payloadBuffer = new byte[BUFFER_SIZE];
        StringBuilder payloadBuilder = new StringBuilder();
        while (remainBytes != 0) {
            bytesToRead = (int)Math.min((long)BUFFER_SIZE, remainBytes);
            int bytesRead = inputStream.read(payloadBuffer, 0, bytesToRead);
            remainBytes -= bytesRead;
            payloadBuilder.append(new String(payloadBuffer, 0, bytesRead));
        }
        frame.setPayload(payloadBuilder.toString());

        // Build and process full payload message
        fragments.add(frame);
        if (frame.isFinalFragment()) {
            StringBuilder fullPayloadBuilder = new StringBuilder();
            fragments.forEach(fragment -> fullPayloadBuilder.append(fragment.getPayload()));
            processMessage(fullPayloadBuilder.toString());
        }
    }

    private boolean existPreviousFragment() {
        return fragments.size() > 0;
    }

    private void processMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
