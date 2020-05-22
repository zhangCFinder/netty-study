package com.zcFinder.codeC;

import com.zcFinder.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import com.zcFinder.protocol.Packet;

import java.util.List;


@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
	public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
		ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
		PacketCodeC.INSTANCE.encode(byteBuf, msg);
		out.add(byteBuf);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		out.add(PacketCodeC.INSTANCE.decode(msg));
	}
}
