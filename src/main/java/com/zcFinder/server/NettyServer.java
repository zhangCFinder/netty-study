package com.zcFinder.server;

import com.zcFinder.codeC.PacketCodecHandler;
import com.zcFinder.codeC.Spliter;
import com.zcFinder.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServer {

	private static final int BEGIN_PORT = 8000;

	public static void main(String[] args) {
		NioEventLoopGroup boosGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();

		final ServerBootstrap serverBootstrap = new ServerBootstrap();
		final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");
		serverBootstrap
				.group(boosGroup, workerGroup)
				.handler(new ChannelInitializer<NioServerSocketChannel>() {
					protected void initChannel(NioServerSocketChannel ch) {
						System.out.println("服务端启动中");
					}
				})
				.channel(NioServerSocketChannel.class)
				.attr(AttributeKey.newInstance("serverName"), "nettyServer")
				.childAttr(clientKey, "clientValue")
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childOption(ChannelOption.SO_KEEPALIVE, true) //表示是否开启TCP底层心跳机制，true为开启
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					protected void initChannel(NioSocketChannel ch) {
						ch.pipeline().addLast(new IMIdleStateHandler());//空闲检测
						ch.pipeline().addLast(new Spliter());//处理粘包与半包
						ch.pipeline().addLast(PacketCodecHandler.INSTANCE);//利用MessageToMessageCodec，合并编解码
						ch.pipeline().addLast(LoginRequestHandler.INSTANCE);//用户登陆
						ch.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);//响应客户端心跳检测
						ch.pipeline().addLast(AuthHandler.INSTANCE);//用户身份认证
						ch.pipeline().addLast(IMHandler.INSTANCE);
					}
				});
//     自动绑定递增端口
		bind(serverBootstrap, BEGIN_PORT);
	}

	//自动绑定递增端口
	private static void bind(final ServerBootstrap serverBootstrap, final int port) {
		serverBootstrap.bind(port).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println("端口[" + port + "]绑定成功!");
			} else {
				System.err.println("端口[" + port + "]绑定失败!");
				bind(serverBootstrap, port + 1);
			}
		});
	}
}
