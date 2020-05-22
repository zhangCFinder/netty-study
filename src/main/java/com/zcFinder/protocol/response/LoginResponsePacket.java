package com.zcFinder.protocol.response;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;
import lombok.Data;


@Data
public class LoginResponsePacket extends Packet {

	private String userId;

	private String userName;

	private boolean success;

	private String reason;

	@Override
	public Byte getCommand() {
		return Command.LOGIN_RESPOSE;
	}
}
