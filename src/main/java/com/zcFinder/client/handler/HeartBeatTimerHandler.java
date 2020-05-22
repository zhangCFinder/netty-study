package com.zcFinder.client.handler;

import com.zcFinder.protocol.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * 客户端定时发送心跳包
 */
public class HeartBeatTimerHandler  extends ChannelInboundHandlerAdapter {
	private static final int HEARTBEAT_INTERVAL = 5;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		scheduleSendHeartBeat(ctx);

		super.channelActive(ctx);
	}

	private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
		ctx.executor().schedule(() -> {

			if (ctx.channel().isActive()) {
				ctx.writeAndFlush(new HeartBeatRequestPacket());
				System.out.println("-----------发送心跳包----------");
				scheduleSendHeartBeat(ctx);
			}

		}, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
	}

}
