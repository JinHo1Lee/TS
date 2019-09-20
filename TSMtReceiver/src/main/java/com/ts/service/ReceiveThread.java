package com.ts.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ts.DefaultThreadFactory;
import com.ts.MtRecvServer;
import com.ts.handler.ReceiveHandler;

import ib.pdu.util.ByteUtil;
import ib.ts.config.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class ReceiveThread implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveThread.class);
	private int port;
	
	private int workerThreads = Config.getRecvWorkerThreadCnt();
	private ExecutorService workerPool = Executors.newCachedThreadPool(new DefaultThreadFactory("ReceiveThread"));
	public ReceiveThread() {
		// TODO Auto-generated constructor stub
		if (MtRecvServer.opt.equals("R"))
			this.port = Config.getRsltListenPort();
		else
			this.port = Config.getRCSListenPort();
		
		LOGGER.info("Listen Port : {}", port);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		ServerBootstrap bootstrap;
		EventLoopGroup workGroups = null;
	    if(Epoll.isAvailable()){
			workGroups = new EpollEventLoopGroup(workerThreads, workerPool);
			System.out.println("EpollEventLoopGroup");
			bootstrap = new ServerBootstrap();
			bootstrap.group(workGroups);
			
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.childOption(ChannelOption.SO_RCVBUF, ByteUtil.MAX_PDU_BYTE_LEN);
			bootstrap.childOption(ChannelOption.SO_SNDBUF, ByteUtil.MAX_PDU_BYTE_LEN);
			
			bootstrap.channel(EpollServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sc) {
						sc.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(ByteUtil.MAX_PDU_BYTE_LEN));
						sc.pipeline().addLast(new ByteArrayEncoder(), new ReceiveHandler(), new ByteArrayDecoder(), new ChunkedWriteHandler());
					}
				});
	    }else{
			workGroups = new NioEventLoopGroup(workerThreads, workerPool);
			System.out.println("NioEventLoopGroup");
			
			bootstrap = new ServerBootstrap();
			bootstrap.group(workGroups);
			
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.childOption(ChannelOption.SO_RCVBUF, ByteUtil.MAX_PDU_BYTE_LEN);
			bootstrap.childOption(ChannelOption.SO_SNDBUF, ByteUtil.MAX_PDU_BYTE_LEN);
			
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					// TODO Auto-generated method stub
					sc.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(ByteUtil.MAX_PDU_BYTE_LEN));
					sc.pipeline().addLast(new ByteArrayEncoder(), new ReceiveHandler(), new ByteArrayDecoder(), new ChunkedWriteHandler());
					
				}
			});
	    }
	    
	    try{
	    	LOGGER.info("Bind {}", port);
	    	ChannelFuture channelFuture = bootstrap.bind(port).sync();
	    	channelFuture.channel().closeFuture().sync();
	    }catch (Exception e){
	    	LOGGER.error("error ", e);
	    	System.exit(0);
	    }finally{
	    	try {
				destroy();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error("erro", e);
			}
	    	workGroups.shutdownGracefully();
	    }
	}
	public void destroy() throws InterruptedException{
		workerPool.shutdown();
		if (!workerPool.awaitTermination(1, TimeUnit.SECONDS)) { 
			LOGGER.info("Executor did not terminate in the specified time.(30sec)"); 
			List<Runnable> droppedTasks = workerPool.shutdownNow();		
			if (!workerPool.awaitTermination(2, TimeUnit.SECONDS)) {
				LOGGER.warn("Pool did not terminate");
			}
		}
		Thread.sleep(1000*3);
		
		workerPool.shutdown();
		LOGGER.info("TSDBManager Stop Complted");
	}
}
