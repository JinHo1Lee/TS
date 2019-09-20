package com.ts.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.Buffer;
import com.ts.MtRecvServer;

import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMMtReq;
import ib.pdu.mfep.PDUGwMtRes;
import ib.pdu.mfep.PDUGwMtResCode;
import ib.pdu.mfep.exception.PduException;
import ib.pdu.util.ByteUtil;
import ib.pdu.util.TSQueData;
import ib.pdu.util.TSQueData.TSQueUtil;
import ib.ts.config.Config;
import ib.pdu.util.FileQue;
import ib.pdu.util.NIOFileQue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ReceiveHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveHandler.class);
	private ChannelHandlerContext ctx;
	private ObjectMapper mapper;
	private ConcurrentHashMap<String, FileQue> recvQueList = new ConcurrentHashMap<>();
	private String ip;
	
	public ReceiveHandler(){
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public boolean processMMS(byte[] reqBuf){
		int resCode = PDUGwMtResCode.MT_RES_SUCCESS;
		
		PDUGwMMtReq pduReq = null;
		try {
			pduReq = new PDUGwMMtReq(reqBuf);
			pduReq.setCarrierKey(pduReq.getRsMsgKey());				
			pduReq.setTsRecvTime(System.currentTimeMillis()/1000);

			LOGGER.info("(request_rcsmt) {}",pduReq.toString());
			
			try {
				String key = pduReq.getTsCode()+"."+pduReq.getRouteGroup();
				FileQue sendQue = null;
				if(!recvQueList.containsKey(key)){
					sendQue = new NIOFileQue(Config.getQueueDir()+"/"+pduReq.getCarrier()+"."+pduReq.getRouteGroup()+".que");
					sendQue.open();
					recvQueList.put(key, sendQue);
					LOGGER.info("New File {}", sendQue.getQueName());
				}else{
					sendQue = recvQueList.get(key);
				}
				
				sendQue.enque(mapper.writeValueAsBytes(new TSQueData(TSQueUtil.TS_RECV_RS, pduReq)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				resCode = PDUGwMtResCode.MT_RES_SERVER_BUSY;
				LOGGER.error("{}", e);
			}
			
		} catch (PduException e) {
			// TODO Auto-generated catch block
			LOGGER.error("{}", e);
			resCode = PDUGwMtResCode.MT_RES_SERVER_BUSY;
		}catch (UnsupportedEncodingException e){
			LOGGER.error("{}", e);
			resCode = PDUGwMtResCode.MT_RES_SERVER_BUSY;
		}
		
		try {
			PDUGwMtRes pduRes = pduReq.makePduGwRes(resCode);
			sendMessage(pduRes.encode());
			LOGGER.info("(response_rcsmt) {}", pduRes.toString());
		} catch (PduException e) {
			// TODO Auto-generated catch block
			LOGGER.error("{}", e);
			return false;
		}
		return true;
	}
	
	public boolean processReport(byte[] reqBuf, int bufSize){
		int resCode = PDUGwMtResCode.MT_RES_SUCCESS;
		
		try {
			FileQue reportQue = null;
			ByteBuffer buffer = ByteBuffer.allocate(bufSize+4);
			buffer.putInt(bufSize);
			buffer.put(reqBuf);
			buffer.limit(buffer.position());
			
			String queName = Config.getQueueDir() +"/"+ip+".rsltrecv.que";
			if (!recvQueList.contains(ip)){
				reportQue = new NIOFileQue(queName);
				reportQue.open();
				recvQueList.put(ip,  reportQue);
			}else{
				reportQue = recvQueList.get(ip);
			}
			
			reportQue.enque(ByteUtil.ByteBufferToByte(buffer));
			buffer.flip();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
			resCode = PDUGwMtResCode.MT_RES_SERVER_BUSY;
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.putInt(PDUConst.PDU_TYPE_GW_MT_REPORT_RES);
		byteBuffer.putInt(resCode);
		byteBuffer.limit(byteBuffer.position());
		
		sendMessage(ByteUtil.ByteBufferToByte(byteBuffer));
		byteBuffer.flip();
		
		return true;
	}

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws IOException{
		SocketAddress remote = ctx.channel().remoteAddress();
		ip = remote.toString().substring(1).split(":")[0];
		if (MtRecvServer.opt.equals("R")){
			
		}else{
			MDC.put("discriminator", String.format("rcsrecv_%s", ip));
		}
			
		LOGGER.info("Connect Info : {}", ctx.channel());
		this.ctx = ctx;
		
	}
	
	byte [] buff = new byte[ByteUtil.MAX_PDU_BYTE_LEN];
	int recvLen = 0;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		// TODO Auto-generated method stub
        while (msg.isReadable()) {
             buff[recvLen++] = msg.readByte();
        } 
        
        int tag = ByteUtil.getint(buff ,0);
		int len = ByteUtil.getint(buff ,4);
		if (recvLen <= len){
			return;
		}else{
			
			if (len > 0){
				if (tag == PDUConst.PDU_TYPE_GW_MT_MMS_REQ){
					processMMS(buff);
				}else if (tag == PDUConst.PDU_TYPE_GW_MT_REPORT_REQ){
					processReport(ByteUtil.getBytes(buff, 8, len), len);
				}else{
					LOGGER.info("Unknown Packet : {}", tag);
				}
			}else{
				
			}
			Arrays.fill(buff, 0, recvLen, (byte)0x00);
			recvLen=0;
		}
	}
	/*
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx )throws Exception{
		ctx.flush();
	}
	*/

	public void sendMessage(byte[] mtReq) {
		ctx.writeAndFlush(Unpooled.copiedBuffer(mtReq));
	}
	
	@Override 
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("Disconnect Info {}: ", ctx.channel());
		ctx.close();
		for(String key : recvQueList.keySet()){
			recvQueList.get(key).close();
		}
	}

}
