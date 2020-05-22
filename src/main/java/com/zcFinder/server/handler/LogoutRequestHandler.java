package com.zcFinder.server.handler;

import com.zcFinder.protocol.response.LogoutResponsePacket;
import com.zcFinder.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.zcFinder.protocol.request.LogoutRequestPacket;


@ChannelHandler.Sharable
public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {
	public static final LogoutRequestHandler INSTANCE = new LogoutRequestHandler();
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LogoutRequestPacket logoutRequestPacket) throws Exception {
		SessionUtil.unBindSession(ctx.channel());
		LogoutResponsePacket logoutResponsePacket = new LogoutResponsePacket();
		logoutResponsePacket.setSuccess(true);
		ctx.channel().writeAndFlush(logoutResponsePacket);
	}
}
