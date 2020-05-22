package com.zcFinder.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.zcFinder.protocol.Packet;
import com.zcFinder.protocol.command.Command;

import java.util.HashMap;
import java.util.Map;


/**
 * 对这个应用程序来说，每次 decode 出来一个指令对象之后，其实只会在一个指令 handler 上进行处理，
 * 因此，其实可以把这么多的指令 handler 压缩为一个 handler
 *
 * 定义一个 map，存放指令到各个指令处理器的映射。
 * 每次回调到 IMHandler 的 channelRead0() 方法的时候，
 * 我们通过指令找到具体的 handler，然后调用指令 handler 的 channelRead，
 * 他内部会做指令类型转换，最终调用到每个指令 handler 的 channelRead0() 方法。
 *
 * 这样对于一个指令而言，就不用经过所有Handler的判断，直接命中需要的那个Handler
 */
public class IMHandler extends SimpleChannelInboundHandler<Packet> {
	public static final IMHandler INSTANCE = new IMHandler();
	private Map<Byte, SimpleChannelInboundHandler<? extends Packet>> handlerMap;

	private IMHandler() {
		handlerMap = new HashMap<>();

		handlerMap.put(Command.MESSAGE_REQUEST, MessageRequestHander.INSTANCE);
		handlerMap.put(Command.LOGOUT_REQUEST, LogoutRequestHandler.INSTANCE);
	}



	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
		handlerMap.get(packet.getCommand()).channelRead(ctx, packet);
	}
}
