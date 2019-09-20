package ib.ts.config;

import java.util.List;
import java.util.Map;

public class RCSInfo {
	private String carrierType;
	private int carrierNet;
	private String carrierIp;
	private int carrierPort;
	private String carrierId;
	private String carrierPassword;
	private int reSendCnt;
	private int reSendTime;
	private int sendTimeOut;
	private int threadPoolCoreSize;
	private int threadMaxPoolSize;
	private int threadKeepAliveTime;
	
	
	
	public RCSInfo() {
		// TODO Auto-generated constructor stub
		reSendCnt = 0;
		reSendTime = 0;
		sendTimeOut = 0;
		threadPoolCoreSize = 0;
		threadMaxPoolSize = 0;
		threadKeepAliveTime = 0;
	}
	public String getCarrierType() {
		return carrierType;
	}
	public void setCarrierType(String carrierType) {
		this.carrierType = carrierType;
	}
	public int getCarrierNet() {
		return carrierNet;
	}
	public void setCarrierNet(int carrierNet) {
		this.carrierNet = carrierNet;
	}
	public String getCarrierIp() {
		return carrierIp;
	}
	public void setCarrierIp(String carrierIp) {
		this.carrierIp = carrierIp;
	}
	public int getCarrierPort() {
		return carrierPort;
	}
	public void setCarrierPort(int carrierPort) {
		this.carrierPort = carrierPort;
	}
	public String getCarrierId() {
		return carrierId;
	}
	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}
	public String getCarrierPassword() {
		return carrierPassword;
	}
	public void setCarrierPassword(String carrierPassword) {
		this.carrierPassword = carrierPassword;
	}
	public int getReSendCnt() {
		return reSendCnt;
	}
	public void setReSendCnt(int reSendCnt) {
		this.reSendCnt = reSendCnt;
	}
	public int getReSendTime() {
		return reSendTime;
	}
	public void setReSendTime(int reSendTime) {
		this.reSendTime = reSendTime;
	}
	public int getSendTimeOut() {
		return sendTimeOut;
	}
	public void setSendTimeOut(int sendTimeOut) {
		this.sendTimeOut = sendTimeOut;
	}
	public int getThreadPoolCoreSize() {
		return threadPoolCoreSize;
	}
	public void setThreadPoolCoreSize(int threadPoolCoreSize) {
		this.threadPoolCoreSize = threadPoolCoreSize;
	}
	public int getThreadMaxPoolSize() {
		return threadMaxPoolSize;
	}
	public void setThreadMaxPoolSize(int threadMaxPoolSize) {
		this.threadMaxPoolSize = threadMaxPoolSize;
	}
	public int getThreadKeepAliveTime() {
		return threadKeepAliveTime;
	}
	public void setThreadKeepAliveTime(int threadKeepAliveTime) {
		this.threadKeepAliveTime = threadKeepAliveTime;
	}
	
	
	
}
