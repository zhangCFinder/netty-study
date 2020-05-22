package com.zcFinder.client;

import com.zcFinder.client.handler.HeartBeatTimerHandler;
import com.zcFinder.client.handler.LoginResponseHandler;
import com.zcFinder.client.handler.MessageResponseHandler;
import com.zcFinder.codeC.PacketCodecHandler;
import com.zcFinder.codeC.Spliter;
import com.zcFinder.protocol.request.LoginRequestPacket;
import com.zcFinder.protocol.request.MessageRequestPacket;
import com.zcFinder.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class NettyClient {
	private static final int MAX_RETRY = 5;
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8000;

	public static void main(String[] args) {
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap
				// 1.指定线程模型
				.group(workerGroup)
				// 2.指定 IO 类型为 NIO
				.channel(NioSocketChannel.class)
				// 绑定自定义属性到 channel
				.attr(AttributeKey.newInstance("clientName"), "nettyClient")
				// 设置TCP底层属性
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				// 3.IO 处理逻辑
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) {
						ch.pipeline().addLast(new Spliter());
						ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
						ch.pipeline().addLast(new LoginResponseHandler());
						ch.pipeline().addLast(new MessageResponseHandler());
						ch.pipeline().addLast(new HeartBeatTimerHandler());//心跳检测
					}
				});

		// 4.建立连接
		connect(bootstrap, HOST, PORT, MAX_RETRY);
	}

	private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
		bootstrap.connect(host, port).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println("连接成功!");
				Channel channel = ((ChannelFuture) future).channel();
				startConsoleThread(channel);
			} else if (retry == 0) {
				System.err.println("重试次数已用完，放弃连接！");
			} else {
				// 第几次重连
				int order = (MAX_RETRY - retry) + 1;
				// 本次重连的间隔，每隔 1 秒、2 秒、4 秒、8 秒，以 2 的幂次（可以用位操作完成）来建立连接，到达一定次数之后就放弃连接
				int delay = 1 << order;
				System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
				bootstrap.config()//返回的是BootstrapConfig，它是对Bootstrap 配置参数的抽象
						.group()//返回的就是我们在一开始的时候配置的线程模型workerGroup,调 workerGroup 的 schedule 方法即可实现定时任务逻辑。
						.schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
			}
		});
	}


	private static void startConsoleThread(Channel channel) {
		Scanner sc = new Scanner(System.in);
		LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
		new Thread(() -> {
			while (!Thread.interrupted()) {
				if (!SessionUtil.hasLogin(channel)) {
					System.out.print("输入用户名登录: ");
					String username = sc.nextLine();
					loginRequestPacket.setUsername(username);

					// 密码使用默认的
					loginRequestPacket.setPassword("pwd");

					// 发送登录数据包
					channel.writeAndFlush(loginRequestPacket);
					waitForLoginResponse();
				} else {
					String toUserId = sc.next();
					String message = sc.next();
					channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
				}
			}
		}).start();
	}

	private static void waitForLoginResponse() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}

}
