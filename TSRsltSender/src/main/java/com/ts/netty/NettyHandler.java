package com.ts.netty;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ts.netty.ResponseFuture;
import com.ts.service.RsltSendThread;

import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUReportReq;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.FileQue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class NettyHandler extends SimpleChannelInboundHandler<ByteBuf>{
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHandler.class);
	private ResponseFuture responseFuture;
	private RsltSendThread nettyClient;
	private boolean isConnected;
	
	
	public NettyHandler(RsltSendThread nettyClient){
		this.nettyClient = nettyClient;
	}
	public void setResponseFuture(ResponseFuture future) {
		this.responseFuture = future;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception{
		LOGGER.info("Connect Info : {}", ctx.channel());
		isConnected = true;
		super.channelActive(ctx);
	}
	
	byte [] buff = new byte[ByteUtil.MAX_PDU_BYTE_LEN];
	int recvLen = 0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		// TODO Auto-generated method stub
		while (msg.isReadable()) {
            buff[recvLen++] = msg.readByte();
		}
		int len = ByteUtil.getint(buff ,4);
		
		if (recvLen <= len){
			return;
		}else{
			responseFuture.set(buff);
			recvLen = 0;
		}
	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("exceptionCaught {}", cause.getLocalizedMessage());
		System.exit(0);
		super.exceptionCaught(ctx, cause);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("Disconnect Info {}: ", ctx.channel());
		isConnected = false;
		
		final EventLoop eventLoop = ctx.channel().eventLoop(); 
		eventLoop.schedule(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				nettyClient.connect();
			} 

		}, 1L, TimeUnit.SECONDS);
		
		super.channelInactive(ctx);
	}
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}	
	
	
}