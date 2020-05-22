package com.zcFinder.protocol.request;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;

public class HeartBeatRequestPacket extends Packet {
	@Override
	public Byte getCommand() {
		return Command.HEARTBEAT_REQUEST;
	}
}
