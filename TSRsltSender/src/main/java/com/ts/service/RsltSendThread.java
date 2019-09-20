package com.ts.service;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.RsltSendServer;
import com.ts.netty.NettyHandler;
import com.ts.netty.ResponseFuture;

import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.mfep.PDUReportRes;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.FileQue;
import ib.pdu.util.IOFileQue;
import ib.pdu.util.NIOFileQue;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.ts.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GenericFutureListener;

public class RsltSendThread implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(RsltSendThread.class);	
	private ChannelFuture channelFuture = null;
	
	private ObjectMapper mapper;
	private String ip;
	private int port;
	private FileQue reportQue;
	private PDUGwMt reportReq;
	
	public void destroy(){
		LOGGER.info("destroy Service");
		
		try{
			channelFuture.channel().close();
		}catch (Exception e){
			LOGGER.error("error", e);
		}
		
		if (reportReq !=null){
			TSQueData tsQueData = new TSQueData(TSQueUtil.TS_RECV_REPORT, reportReq);
			try {
				FileQue ioFile = new IOFileQue(reportQue.getQueName());
				ioFile.open();
				ioFile.enque(mapper.writeValueAsBytes(tsQueData));
				ioFile.close();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			}
			LOGGER.info("enque Report Queue {} ", reportReq.toString());
		}
		System.exit(0);
	}
	
	public RsltSendThread() {
		// TODO Auto-generated constructor stub
		
		String ip = RsltSendServer.opt.split(":")[0];
		int port = Integer.parseInt(RsltSendServer.opt.split(":")[1]);
		MDC.put("discriminator", String.format("rsltsend_%s", ip));
		this.ip = ip;
		this.port = port;
		
		String reportQueName = Paths.get(Config.getQueueDir()).toAbsolutePath().toString()+"/"+ip+"."+port+".rslt.que";
		LOGGER.info("Report Queue Info {}", reportQueName);
		reportQue = new NIOFileQue(reportQueName);
		
		try {
			reportQue.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public void connect(){
		EventLoopGroup group;
		Bootstrap b;
		if(Epoll.isAvailable()){
			group = new EpollEventLoopGroup();
			b = new Bootstrap();
			final NettyHandler rsltSendHandler = new NettyHandler(this);
			b.group(group)
			.channel(EpollSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline cp = sc.pipeline();
					cp.addLast(new ByteArrayEncoder(), rsltSendHandler, new ByteArrayDecoder());
				}
			});
		}else{
			group = new NioEventLoopGroup();
			b = new Bootstrap();
			final NettyHandler rsltSendHandler = new NettyHandler(this);
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline cp = sc.pipeline();
					cp.addLast(new ByteArrayEncoder(), rsltSendHandler, new ByteArrayDecoder());
				}
			});
		}
		
		
		try {
			channelFuture = b.connect(ip, port).sync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
			destroy();
		}
		
		processReport();
		
	}
	
	public void processReport(){
		while(!Thread.currentThread().isInterrupted()){
			if (!channelFuture.channel().pipeline().get(NettyHandler.class).isConnected()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOGGER.info("error {}", e);
					Thread.currentThread().interrupt();
				}
				continue;
			}
			try {
				byte[] buffer = null;
				if ((buffer = reportQue.deque()) != null){
					TSQueData tsQueData = mapper.readValue(buffer, TSQueData.class);
					reportReq = tsQueData.getPduGwMt();
					reportReq.setPduType(PDUConst.PDU_TYPE_GW_MT_REPORT_REQ);
					reportReq.setTsRsltSendTime(System.currentTimeMillis()/1000);
					LOGGER.info("(request_report) {}", reportReq.toString());
					ResponseFuture responseFuture = send(reportReq.encode());
					byte[] recvBuffer = null;
					try {
						recvBuffer = responseFuture.get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LOGGER.info("error", e);
						Thread.currentThread().interrupt();
						continue;
					}
					int tag = ByteUtil.getint(recvBuffer);
					if (tag == PDUConst.PDU_TYPE_GW_MT_REPORT_RES){
						PDUReportRes res = new PDUReportRes(recvBuffer);
						LOGGER.info("(response_report) {}", res.toString());
						if (res.getResCode() == 1000){
							reportReq = null;
						}else{
							reportQue.enque(mapper.writeValueAsBytes(tsQueData));
						}
					}else{
						LOGGER.info("Unknown pduType {}", tag);
					}
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LOGGER.error("error", e);
						Thread.currentThread().interrupt();
						continue;
					}
				}
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
				Thread.currentThread().interrupt();
			} catch (PduException | JsonParseException |JsonMappingException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("error", e);
				Thread.currentThread().interrupt();
			}  catch (Exception e){
				LOGGER.error("error", e);
				Thread.currentThread().interrupt();
			}
		}
		destroy();
	}
	public ResponseFuture send(final byte[] buffer) {
		final ResponseFuture responseFuture = new ResponseFuture();

		channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
			public void operationComplete(ChannelFuture future)	throws Exception {
				channelFuture.channel().pipeline().get(NettyHandler.class).setResponseFuture(responseFuture);
				channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(buffer));
			}
		});
		
		return responseFuture;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		connect();
	}
	
}
