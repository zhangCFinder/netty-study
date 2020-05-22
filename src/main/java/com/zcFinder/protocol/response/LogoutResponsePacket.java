package com.zcFinder.protocol.response;

import lombok.Data;
import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;


@Data
public class LogoutResponsePacket  extends Packet {
	private boolean success;

	private String reason;
	@Override
	public Byte getCommand() {
		return Command.LOGOUT_RESPONSE;
	}
}
