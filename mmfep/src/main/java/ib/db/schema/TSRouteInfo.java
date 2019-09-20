package ib.db.schema;

public class TSRouteInfo {
	private String tsCode;
	private String pdutype;
	private String rtgId;
	private int ratio;
	
	public String getPdutype() {
		return pdutype;
	}
	public void setPdutype(String pdutype) {
		this.pdutype = pdutype;
	}
	public String getRtgId() {
		return rtgId;
	}
	public void setRtgId(String rtgId) {
		this.rtgId = rtgId;
	}
	public String getTsCode() {
		return tsCode;
	}
	public void setTsCode(String tsCode) {
		this.tsCode = tsCode;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	
	
	
}
