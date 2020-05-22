package com.zcFinder.protocol.request;

import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;
import lombok.Data;


@Data
public class MessageRequestPacket extends Packet {
	private String toUserId;
	private String message;

	public  MessageRequestPacket(String toUserId, String message){
		this.toUserId = toUserId;
		this.message = message;
	}
	@Override
	public Byte getCommand() {
		return Command.MESSAGE_REQUEST;
	}

}