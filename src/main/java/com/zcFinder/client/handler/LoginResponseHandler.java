package com.zcFinder.client.handler;

import com.zcFinder.protocol.response.LoginResponsePacket;
import com.zcFinder.session.Session;
import com.zcFinder.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
		String userId = loginResponsePacket.getUserId();
		String userName = loginResponsePacket.getUserName();

		if (loginResponsePacket.isSuccess()) {
			System.out.println("[" + userName + "]登录成功，userId 为: " + loginResponsePacket.getUserId());
			SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
		} else {
			System.out.println("[" + userName + "]登录失败，原因：" + loginResponsePacket.getReason());
		}
	}
}
