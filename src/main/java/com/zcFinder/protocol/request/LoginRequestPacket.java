package com.zcFinder.protocol.request;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;
import lombok.Data;


@Data
public class LoginRequestPacket extends Packet {

	private String userId;

	private String username;

	private String password;

	private String reason;

	@Override
	public Byte getCommand() {
		return Command.LOGIN_REQUEST;
	}
}
