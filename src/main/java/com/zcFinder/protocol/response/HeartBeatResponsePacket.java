package com.zcFinder.protocol.response;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;



public class HeartBeatResponsePacket extends Packet {
    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_REQUEST;
    }
}
