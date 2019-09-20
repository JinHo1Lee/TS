package ib.db;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import ib.db.schema.RCSResCode;
import ib.db.schema.TSRouteInfo;
import ib.db.schema.TSRsInfo;

import ib.pdu.mfep.PDUGwMt;
import ib.pdu.mfep.PDUReportReq;

public interface MysqlSessionDAO {
	
	//TSDBManager
	@Insert("CALL SP_TSRsltMapCreateTable(#{strTableName})")
	int createResultMapTable(@Param("strTableName") String strTableName);
	
	@Select("CALL SP_TSRsltMapInsertSql(#{strTableName}, " +
									   "#{resultMap.carrierKey}, " +
									   "#{resultMap.tsCode}, " +
									   "#{resultMap.carrier}, " +
									   "#{resultMap.msgStatus}, " +
									   "#{resultMap.pduType}, " +
									   "#{resultMap.rsMsgKey}, " +
									   "#{resultMap.clientMsgKey}, " +
									   "#{resultMap.rsID}, " +
									   "#{resultMap.clientCode}, " +
									   "#{resultMap.netCode}, " +
									   "#{resultMap.ibRsltCode}, " +
									   "#{resultMap.subject}, " +
									   "#{resultMap.content}, " +
									   "#{resultMap.callBack}, " +
									   "#{resultMap.routeGroup}, " +
									   "#{resultMap.wordCnt}, " +
									   "#{resultMap.recipientInfo.recipientOrder}, " +
									   "#{resultMap.recipientInfo.recipientNum}, " +
									   "#{resultMap.recipientInfo.recipientNet}, " +
									   "#{resultMap.npResult}, " +
									   "#{resultMap.recipientInfo.conuntryCode}, " +
									   "#{resultMap.wordCnt}, " +
									   "#{resultMap.priority}, " +
									   "#{resultMap.numPorted}, " +
									   "#{resultMap.msgType}, " +
									   "#{resultMap.msgClass}, " +
									   "#{resultMap.ttl}, " +
									   "#{resultMap.strFileKeyList}, " +
									   "#{resultMap.strFileKeyCarrier}, " +
									   "#{resultMap.rsRecvTime}, " +
									   "#{resultMap.tsRecvTime}, " +
									   "#{resultMap.tsSendTime}, " +
									   "#{resultMap.emmaTag}, " +
									   "#{resultMap.contentType}, " +
									   "#{resultMap.bridgeTag} " +
									   ");")
	@Results(value ={
			@Result(property="code", column="CODE"),
			@Result(property="msg", column="msg")
	})
	MysqlErrorHandler insertResultMap(@Param("strTableName") String strTableName, @Param("resultMap")PDUGwMt resultMap);
	
	@Select("CALL SP_TSRsltMapMoveToLogSql(#{strMapName}, #{logTableName}, #{netKey}, #{mapStatus}, #{netCode}, #{ibRslt}, #{lTsRsltDate}, #{iTsCode}, #{strRsKey}, #{strEmmaKey}, #{iRecipientSeq}, #{strRecipientNum})")
	@Results(value ={
			@Result(property="code", column="CODE"),
			@Result(property="msg", column="msg")
	})
	MysqlErrorHandler updateMoveLogResultMap(@Param("strMapName") String strTableName, 
											@Param("logTableName") String logTableName,
										 	@Param("netKey") String netKey,
											@Param("mapStatus") int  mapStatus,
											@Param("netCode") String netCode,
											@Param("ibRslt") String ibRslt,
											@Param("lTsRsltDate") long lTsRsltDate,
											@Param("iTsCode") int iTsCode,
											@Param("strRsKey") String strRsKey,
											@Param("strEmmaKey") String strEmmaKey,
											@Param("iRecipientSeq") int iRecipientSeq,
											@Param("strRecipientNum") String strRecipientNum
											);
	
	@Select("CALL SP_TSRsltMapUpdateSql(#{strMapName}, #{netKey}, #{mapStatus}, #{lTsSentDate}, #{iTsCode}, #{strRsKey}, #{strEmmaKey}, #{iRecipientSeq}, #{strRecipientNum})")
	@Results(value ={
			@Result(property="code", column="CODE"),
			@Result(property="msg", column="msg")
	})
	MysqlErrorHandler updateResultMap(@Param("strMapName") String strTableName, 
							@Param("netKey") String netKey,
							@Param("mapStatus") int  mapStatus,
							@Param("lTsSentDate") long lTsSentDate,
							@Param("iTsCode") int iTsCode,
							@Param("strRsKey") String strRsKey,
							@Param("strEmmaKey") String strEmmaKey,
							@Param("iRecipientSeq") int iRecipientSeq,
							@Param("strRecipientNum") String strRecipientNum
							);
	
	@Select("CALL SP_TSRsltMapSelectSql(#{strTableName}, #{strNetKey})")
	@Results(value ={
			@Result(property="carrierKey", column="map_netkey"),
			@Result(property="tsCode", column="ts_code"),
			@Result(property="carrier", column="netid"),
			@Result(property="msgStatus", column="map_status"),
			@Result(property="pduType", column="map_pdutype"),
			@Result(property="rsMsgKey", column="map_rskey"),
			@Result(property="clientMsgKey", column="map_emmakey"),
			@Result(property="rsID", column="rs_id"),
			@Result(property="clientCode", column="client_code"),
			@Result(property="content", column="map_content"),
			@Result(property="callBack", column="map_callback"),
			@Result(property="priority", column="map_priority"),
			@Result(property="routeGroup", column="map_routinggroup"),
			@Result(property="wordCnt", column="map_changewordcnt"),
			@Result(property="recipientInfo.recipientOrder", column="recipient_seq"),
			@Result(property="recipientInfo.recipientNum", column="recipient_num"),
			@Result(property="recipientInfo.recipientNet", column="recipient_net"),
			@Result(property="npResult", column="recipient_npsend"),
			@Result(property="recipientInfo.conuntryCode", column="country_code"),
			@Result(property="msgType", column="msg_type"),
			@Result(property="ttl", column="map_ttltime"),
			@Result(property="rsRecvTime", column="map_rsrecvdate"),
			@Result(property="tsRecvTime", column="map_tsrecvdate"),
			@Result(property="tsSendTime", column="map_tssentdate"),
			@Result(property="emmaTag", column="map_emmatag")
		})
	PDUReportReq selectResultMap(@Param("strTableName")String strTableName, @Param("strNetKey") String netKey);
	
	@Select("CALL SP_TSRsltMapDeleteSql(#{strTableName}, #{iTsCode}, #{strNetKey}, #{iRecipientSeq}, #{strRecipientNum})")
	@Results(value ={
			@Result(property="code", column="CODE"),
			@Result(property="msg", column="msg")
	})
	MysqlErrorHandler deleteResultMap(@Param("strTableName") String strTableName,
									  @Param("iTsCode") int iTsCode,
									  @Param("strNetKey")String strNetKey,
									  @Param("iRecipientSeq") int iRecipientSeq,
									  @Param("strRecipientNum") String strRecipientNum);
	
	
	@Select("CALL SP_TSRsInfoSelectSql(#{type})")
	@Results(value = {
			@Result(property="rsId", column="rs_id"),
			@Result(property="rsIp", column="rsip"),
			@Result(property="rsPort", column="rsport")
	})
	List<TSRsInfo> selectTSRsInfo(@Param("type") int rsType);
	
	@Select("CALL SP_TSRsltMapTimeOutSelectSql(#{strTableName})")
	@Results(value ={
			@Result(property="carrierKey", column="map_netkey"),
			@Result(property="tsCode", column="ts_code"),
			@Result(property="carrier", column="netid"),
			@Result(property="msgStatus", column="map_status"),
			@Result(property="pduType", column="map_pdutype"),
			@Result(property="rsMsgKey", column="map_rskey"),
			@Result(property="clientMsgKey", column="map_emmakey"),
			@Result(property="rsID", column="rs_id"),
			@Result(property="clientCode", column="client_code"),
			@Result(property="content", column="map_content"),
			@Result(property="callBack", column="map_callback"),
			@Result(property="priority", column="map_priority"),
			@Result(property="routeGroup", column="map_routinggroup"),
			@Result(property="wordCnt", column="map_changewordcnt"),
			@Result(property="recipientInfo.recipientOrder", column="recipient_seq"),
			@Result(property="recipientInfo.recipientNum", column="recipient_num"),
			@Result(property="recipientInfo.recipientNet", column="recipient_net"),
			@Result(property="npResult", column="recipient_npsend"),
			@Result(property="recipientInfo.conuntryCode", column="country_code"),
			@Result(property="msgType", column="msg_type"),
			@Result(property="ttl", column="map_ttltime"),
			@Result(property="rsRecvTime", column="map_rsrecvdate"),
			@Result(property="tsRecvTime", column="map_tsrecvdate"),
			@Result(property="tsSendTime", column="map_tssentdate"),
			@Result(property="emmaTag", column="map_emmatag")
		})
	List<PDUReportReq> selectTimeOutResultMap(@Param("strTableName")String strTableName);
	
	//RCS Sender
	@Select("SELECT rtg_id, pdutype, ts_code, ratio from tsrouteinfo where ts_code = #{tsCode}")
	@Results(value ={
		@Result(property="rtgId", column="rtg_id"),  
		@Result(property="pdutype", column="pdutype"),
		@Result(property="tsCode", column="ts_code"),  
		@Result(property="ratio", column="ratio")
	})
	List<TSRouteInfo> getRtRuleInfo(@Param("tsCode") String tsCode);
	
	@Select("select carrier, res_code, ibrslt from rcsrescode")
	@Results(value ={
		@Result(property="carrier", column="carrier"),
		@Result(property="resCode", column="res_code"),
		@Result(property="ibRslt", column="ibrslt")
	})
	List<RCSResCode> getResCode();
}
