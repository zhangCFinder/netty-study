package com.zcFinder.server.handler;

import com.zcFinder.protocol.request.HeartBeatRequestPacket;
import com.zcFinder.protocol.response.HeartBeatResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<HeartBeatRequestPacket> {
	public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

	private HeartBeatRequestHandler() {

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket requestPacket) {
		System.out.println("接收到心跳包");
		ctx.writeAndFlush(new HeartBeatResponsePacket());
	}
}