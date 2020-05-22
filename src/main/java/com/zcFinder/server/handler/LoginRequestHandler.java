package com.zcFinder.server.handler;

import com.zcFinder.protocol.response.LoginResponsePacket;
import com.zcFinder.session.Session;
import com.zcFinder.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.zcFinder.protocol.request.LoginRequestPacket;

import java.util.Date;
import java.util.UUID;


// 1. 加上注解标识，表明该 handler 是可以多个 channel 共享的
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
	// 2. 构造单例
	public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginRequestPacket) throws Exception {
		LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
		loginResponsePacket.setVersion(loginRequestPacket.getVersion());
		if (valid(loginRequestPacket)) {
			loginResponsePacket.setSuccess(true);
			String userId = randomUserId();
			String userName = loginRequestPacket.getUsername();
			loginResponsePacket.setUserId(userId);
			loginResponsePacket.setUserName(userName);
			System.out.println(new Date() + "----------"+userName+ ": 登录成功!");
			SessionUtil.bindSession(new Session(userId, loginRequestPacket.getUsername()), ctx.channel());
		} else {
			loginResponsePacket.setReason("账号密码校验失败");
			loginResponsePacket.setSuccess(false);
			System.out.println(new Date() + ": 登录失败!");
		}

		// 将登陆响应写回channel
		ctx.channel().writeAndFlush(loginResponsePacket);
	}

	private boolean valid(LoginRequestPacket loginRequestPacket) {
		return true;
	}

	private static String randomUserId() {
		return UUID.randomUUID().toString().split("-")[0];
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		SessionUtil.unBindSession(ctx.channel());
	}
}
