package com.zcFinder.protocol.response;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;
import lombok.Data;



@Data
public class MessageResponsePacket extends Packet {
	private String fromUserId;

	private String fromUserName;

	private String message;


	@Override
	public Byte getCommand() {

		return Command.MESSAGE_RESPONSE;
	}
}
