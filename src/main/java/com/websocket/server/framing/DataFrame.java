package com.websocket.server.framing;

import java.math.BigInteger;

import com.websocket.server.constants.WebsocketStatus;
import com.websocket.server.exception.DataFrameException;

import lombok.Getter;

@Getter
public class DataFrame {
    private boolean sendByClient;
    private boolean isFinalFragment;
    private boolean isMask;
    private String payloadType;
    private BigInteger payloadLengthInBytes;
    
    public DataFrame(boolean sendByClient) {
        this.sendByClient = sendByClient;
        // TODO: Update this field later to include more types
        payloadType = "text";
    }

    public void setIsFinalFragment(boolean isFinalFragment) {
        this.isFinalFragment = isFinalFragment;
    }

    public void setIsMark(boolean isMark) {
        if (!isMark && sendByClient) {
            throw new DataFrameException(WebsocketStatus.CLOSE_PROTOCOL_ERROR, "Payload sending from client to websocket server must be masked");
        }
    }
}
