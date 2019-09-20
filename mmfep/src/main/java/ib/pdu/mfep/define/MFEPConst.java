package ib.pdu.mfep.define;

/**
 * @(#)MMGPConst.java Copyright 2008-2009 InfoBank Corporation. All rights
 *                    reserved. PDU Code
 * 
 * @author zagals@infobank.net
 * @version 1.0
 * @since 10/02/01
 * @history
 */
public class MFEPConst {
	/**
	 * PDU TYPE
	 */
	public static final int PT_MT_SMS_REQ = 0x01070001; //SMS MT 전송 요청
	public static final int PT_MT_SMS_RES = 0x01070002; //SMT MT 전송 요청 응답	
	public static final int PT_MT_MMS_REQ = 0x01070005; //MMT MT 전송 요청 응답
	public static final int PT_MT_MMS_RES = 0x01070006; //MMT MT 전송 요청 응답
	public static final int PT_MT_REPORT_REQ = 0x01070003; //MT 전송 결과 수신 요청
	public static final int PT_MT_REPORT_RES = 0x01070004; //MT 전송 결과 수신 응답
	public static final int PT_SESSION_CHECK_REQ = 0x0102000F; //SESSION 상태 확인 요청
	public static final int PT_SESSION_CHECK_RES = 0x01020010; //SESSION_CHECK_REQ에 대한 응답

	/**
	 * Tag Code
	 */
	public static final int TC_CLIENT_ID = 0x01810001; //클라이언트 ID
	public static final int TC_CLIENT_PASSWORD = 0x01810033; //클라이언트 암호
	public static final int TC_CLIENT_MSG_KEY = 0x01820001; //클라이언트와 메시지 송수신 시 사용하는 키
	public static final int TC_SUBJECT = 0x01820002; //메시지 제목
	public static final int TC_CONTENT = 0x01820003; //메시지 내용
	public static final int TC_RECIPIENT_CNT = 0x01820004; //수신 번호 개수
	public static final int TC_RECIPIENT_ORDER = 0x01820005; //수신 번호 순번
	public static final int TC_COUNTRY_CODE = 0x01820008; //국가 번호
	public static final int TC_RECIPIENT_NUM = 0x01820009; //수신번호
	public static final int TC_RECIPIENT_INFO = 0x0182000C; //수신자 정보
	public static final int TC_RS_ID = 0x01810009; //RS ID
	public static final int TC_CALLBACK = 0x0182000E; //회신번호
	public static final int TC_TTL = 0x0182000F; //메시지 전송 유효 시간(절대시간, KST 기준)
	public static final int TC_CHARSET = 0x01820011; //메시지의 CHARSET
	public static final int TC_CARRIER = 0x01820016; //최종 통신사 정보
	public static final int TC_DATE_CLIENT_REQ = 0x0182001A; //클라이언트의 전송 요청 시간 (FEP 서버인 경우, FEP서버 수신시각)
	public static final int TC_DATE_RSLT = 0x0182001E; //결과 발생 시각
	public static final int TC_ATTACH_FILE_INFO = 0x0182002D; //첨부 파일 정보
	public static final int TC_RECIPIENTS = 0x01820031; //수신자 정보 리스트
	public static final int TC_ATTACH_FILES = 0x01820033; //첨부 파일 리스트
	public static final int TC_ATTACH_FILE_CNT = 0x01820035; //첨부 파일 개수
	public static final int TC_FAULT_INFO = 0x01820036; //장애 정보
	public static final int TC_FAULT_TYPE = 0x01820037; //장애유형
	public static final int TC_FAULT_VALUE = 0x01820038; //장애 값
	public static final int TC_FAULT_SRC = 0x01820039; //장애 소스
	public static final int TC_PAYMENT_CODE = 0x0182003D; //수신자 정보 리스트
	public static final int TC_CONTENT_INFO = 0x01820043; //메시지 정보
	public static final int TC_CONTENT_TYPE = 0x01820044; //Content Type(IBML..)
	public static final int TC_MT_REPORT_CODE_IB = 0x01821002; //IB 결과 코드
	public static final int TC_MT_RES_CNT = 0x01821005; //MT전송 요청 시, 개별 발급되는 접수 순번
	public static final int TC_RCV_REPORT = 0x01880001; //MT 전송 결과 수신 여부
	public static final int TC_FEP_RCV_SPEC = 0x01880002; //FEP서버에서 수신한 Client MT 요청 SPEC
	public static final int TC_CLIENT_REF_VALUE = 0x01880003; //Cleint에서 설정한 데이터
	public static final int TC_FEP_MT_RES_CODE = 0x01881001; //FEP서버에서 전송 MT에 대한 응답코드
	public static final int TC_ACPT_MSG_KEY = 0x01881002; //FEP서버에서 전송 접수 MT에 대한 응답코드
	public static final int TC_ACPT_MSG_CNT = 0x01881003; //FEP서버에서 전송 접수 MT에 대한 응답코드
	public static final int TC_ACPT_MSG_TYPE = 0x01881004; //FEP서버에서 전송 접수MT에 대한 응답코드
	public static final int TC_CLNT_RPT_ADDR = 0x01881005; //고객이 MT결과 수신 받는 주소 (IP or URL)
	public static final int TC_CLNT_RPT_PORT = 0x01881006; //고객이 MT결과 수신 받는 주소(PORT)
	
	public static final int TC_MSG_TYPE = 0x0182002C; //고객 요청 MSG_TYPE
	
	public static final int TC_SUB_ID = 0x01880004; //CLIENT_SUB_ID	
	
	

	/**
	 * MSG_TYPE
	 */
	public static final int MT_MSG_TYPE_GENERAL = 1001; // 일반
	public static final int MT_MSG_TYPE_GLOBAL = 1004; // 국제
	public static final int MT_MSG_TYPE_BIZTALK = 1008; // BizTalk
	public static final int MT_MSG_TYPE_BIZTALK_FRIEND = 1009; // 친구톡
	
	
	/**
	 * MT_RES_CODE
	 */
	public static final int MT_RES_CODE_SUCCESS = 1000; // 정상 접수 성공 
	public static final int MT_RES_CODE_ID_INVAILD = 1001; // ID 존재하지 않음
	public static final int MT_RES_CODE_PASSWORD_INVAILD = 1002; // 클라이언트 패스워드 틀림
	public static final int MT_RES_CODE_SERVER_BUSY = 1003; // Server Busy (RS 내부 저장 Queue Fail)
	public static final int MT_RES_CODE_RECIPIENT_INVAILD = 1004; // 수신번호 형식 오류
	public static final int MT_RES_CODE_CALLBACK_INVALID = 1005; // 회신번호 형식 오류
	public static final int MT_RES_CODE_SPAM = 1006; // SPAM
	public static final int MT_RES_CODE_EXCEED_CREDIT = 1007; // 사용 건수 초과
	public static final int MT_RES_CODE_CONTENT_NONE = 1008; // CONTENT 없음
	public static final int MT_RES_CODE_CALLBACK_NONE = 1009; // CALLBACK 없음
	public static final int MT_RES_CODE_RECIPIENT_NONE = 1010; // RECIPIENT_INFO 없음
	public static final int MT_RES_CODE_NOT_AUTHORIZED = 1011; // 전송 권한 없음
	public static final int MT_RES_CODE_EXCEED_TTL = 1012; // TTL 초과(KST 기준)
	public static final int MT_RES_CODE_CHARSETCONVERT_ERROR = 1013; // Charset conversion error
	public static final int MT_RES_CODE_NOT_ALLOWED = 1014; // 서비스 거부 됨. (고객 접속 금지)
	public static final int MT_RES_CODE_CALLBACK_NOT_ALLOWED = 1015; // 발신번호 사전 미등록 번호 사용으로 거부
	public static final int MT_RES_CODE_CALLBACK_FORMAT_ERROR = 1016; // 사용 발신번호가 발신번호 변작방지 세칙 위반
	public static final int MT_RES_CODE_ETC = 9999; // 기타 에러

	/**
	 * CHARSET CODE
	 */
	public static final String CHARSET_UTF8 = "UTF-8"; //default
	public static final String CHARSET_EUCKR = "EUC-KR";

	/**
	 * CHARSET CODE
	 */
	public static final int CONTENT_TYPE_GENERAL = 0; //일반
	public static final int CONTENT_TYPE_IBML = 1; //IBML

	/**
	 * RCV_REPORT
	 */
	public static final String RCV_REQUEST_NO = "0"; //MT 전송 결과 수신 안함
	public static final String RCV_REQUEST_YES = "1"; //MT 전송 결과 수신

	/**
	 * APT_MSG_TYPE
	 */
	public static final int MSG_TYPE_SMS = 1; //SMS
	public static final int MSG_TYPE_MMS = 2; //MMS

	/**
	 * CARRIER
	 */
	public static final int CARRIER_ETC = 10000; //ETC
	public static final int CARRIER_SKT = 10001; //SKT
	public static final int CARRIER_KTF = 10002; //KTF
	public static final int CARRIER_LGT = 10003; //LGT
	public static final int CARRIER_KTP = 10004; //KT 파워텔
	public static final int CARRIER_MNTAK = 10005; //M&Talk
	public static final int CARRIER_KTA = 10006; //KTAnn
	public static final int CARRIER_MNMEDIA = 10007; //M&Media
	public static final int CARRIER_NGM = 10008; //NGM

	/**
	 * RECV_SPEC
	 */
	public static final int SPEC_RESTFUL = 1001; //RESTFul
	public static final int SPEC_JSON = 1002; //JSON
	public static final int SPEC_XML = 1003; //XML
	public static final int SPEC_ETC = 9999; //ETC

	/**
	 * DATA TYPE SIZE
	 */
	public static final int SIZE_HEADER_TYPE = 4;
	public static final int SIZE_HEADER_LENGTH = 4;
	public static final int SIZE_INT = 4;
	public static final int SIZE_SHORT = 2;
	public static final int SIZE_LONG = 8;

	/**
	 * MAX LENGTH
	 */
	public static final int CHAR_SET_LENTGH = 8;

}
