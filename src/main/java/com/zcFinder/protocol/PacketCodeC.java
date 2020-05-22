package com.zcFinder.protocol;

import com.zcFinder.protocol.command.Command;
import com.zcFinder.protocol.request.HeartBeatRequestPacket;
import com.zcFinder.protocol.request.LoginRequestPacket;
import com.zcFinder.protocol.request.LogoutRequestPacket;
import com.zcFinder.protocol.request.MessageRequestPacket;
import com.zcFinder.protocol.response.LoginResponsePacket;
import com.zcFinder.protocol.response.LogoutResponsePacket;
import com.zcFinder.protocol.response.MessageResponsePacket;
import com.zcFinder.serializer.Serializer;
import com.zcFinder.serializer.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;


public class PacketCodeC {
	public static final int MAGIC_NUMBER = 0x12345678;
	public static final PacketCodeC INSTANCE = new PacketCodeC();


	private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
	private static final Map<Byte, Serializer> serializerMap;



	static {
		packetTypeMap = new HashMap<>();
		//command和解码类型的对应
		packetTypeMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
		packetTypeMap.put(Command.LOGIN_RESPOSE, LoginResponsePacket.class);
		packetTypeMap.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
		packetTypeMap.put(Command.MESSAGE_RESPONSE, MessageResponsePacket.class);
		packetTypeMap.put(Command.HEARTBEAT_REQUEST, HeartBeatRequestPacket.class);

		packetTypeMap.put(Command.LOGOUT_REQUEST, LogoutRequestPacket.class);
		packetTypeMap.put(Command.LOGOUT_RESPONSE, LogoutResponsePacket.class);
		serializerMap = new HashMap<>();
		Serializer serializer = new JSONSerializer();
		serializerMap.put(serializer.getSerializerAlogrithm(), serializer);
	}


	public ByteBuf encode(ByteBuf byteBuf, Packet packet) {

		// 2. 序列化 Java 对象
		byte[] bytes = Serializer.DEFAULT.serialize(packet);

		// 3. 实际编码过程
		byteBuf.writeInt(MAGIC_NUMBER);
		byteBuf.writeByte(packet.getVersion());
		byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlogrithm());
		byteBuf.writeByte(packet.getCommand());
		byteBuf.writeInt(bytes.length);
		byteBuf.writeBytes(bytes);

		return byteBuf;
	}


	public Packet decode(ByteBuf byteBuf) {
		// 跳过 magic number
		byteBuf.skipBytes(4);

		// 跳过版本号
		byteBuf.skipBytes(1);

		// 序列化算法
		byte serializeAlgorithm = byteBuf.readByte();

		// 指令
		byte command = byteBuf.readByte();

		// 数据包长度
		int length = byteBuf.readInt();

		byte[] bytes = new byte[length];
		byteBuf.readBytes(bytes);

		//拿到序列化算法，在类启动时将序列化算法放入静态map中
		Serializer serializer = getSerializer(serializeAlgorithm);
		//拿到指令，在类启动时将指令集放入静态map中。
		Class<? extends Packet> requestType = getRequestType(command);

		if (requestType != null && serializer != null) {
			return serializer.deserialize(requestType, bytes);
		}

		return null;
	}
	private Serializer getSerializer(byte serializeAlgorithm) {

		return serializerMap.get(serializeAlgorithm);
	}

	private Class<? extends Packet> getRequestType(byte command) {

		return packetTypeMap.get(command);
	}
}
