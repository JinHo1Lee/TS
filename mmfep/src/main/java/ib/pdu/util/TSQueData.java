package ib.pdu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ib.pdu.mfep.PDUConst;
import ib.pdu.mfep.PDUGwMMtReq;
import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUGwSMtReq;
import ib.pdu.mfep.PDUReportReq;

public class TSQueData{	
	@JsonProperty("stauts")
	private int status;
	
	@JsonProperty("pduType")
	private int pduType;
	
	@JsonProperty("pduGwMMtReq")
	private PDUGwMMtReq pduGwMMtReq;
	
	@JsonProperty("pduReportReq")
	private PDUReportReq pduReportReq;
	
	@JsonProperty("jsonArray")
	private String jsonArray;
	
	
	public TSQueData(){
		
	}
	public TSQueData(int status, PDUGwMt pduGwReq){
		this.status = status;
		if (status == TSQueUtil.TS_RECV_RS){
			pduGwMMtReq = (PDUGwMMtReq) pduGwReq;
		}else{
			pduReportReq = (PDUReportReq) pduGwReq;
		}
	}
	public PDUGwMMtReq getPduGwMMtReq() {
		return pduGwMMtReq;
	}
	public void setPduGwMMtReq(PDUGwMMtReq pduGwMMtReq) {
		this.pduGwMMtReq = pduGwMMtReq;
	}
	
	public PDUReportReq getPduReportReq() {
		return pduReportReq;
	}
	public void setPduReportReq(PDUReportReq pduReportReq) {
		this.pduReportReq = pduReportReq;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public int getPduType() {
		return pduType;
	}
	public void setPduType(int pduType) {
		this.pduType = pduType;
	}
	
	@JsonIgnore
	public PDUGwMt getPduGwMt(){
		if (status == TSQueUtil.TS_RECV_RS){
			return pduGwMMtReq;
		}else{
			return pduReportReq;
		}
	}
	
	public String getJsonArray() {
		return jsonArray;
	}
	public void setJsonArray(String jsonArray) {
		this.jsonArray = jsonArray;
	}

	public static class TSQueUtil{
		public static final int TS_RECV_MT = 0x000001;
		public static final int TS_SEND_ACK = 0x000002;
		public static final int TS_SEND_NAK = 0x000003;
		public static final int TS_RECV_REPORT = 0x000004;
		public static final int TS_RECV_RS = 0x000005;
	}
	
	
}