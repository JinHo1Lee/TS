package ib.pdu.mfep;

public class PDUGwMtResCode {
	public static final int MT_RES_SUCCESS = 1000;                       /** 수신 성공 */
	public static final int MT_RES_SERVER_BUSY = 1001;                   /** Server Busy (RS 내부 저장 Queue Full) */
	public static final int MT_RES_RECIPIENT_FORMAT_ERROR = 1002;        /** 수신번호 형식 오류 */
	public static final int MT_RES_CALLBACK_FORMAT_ERROR = 1003;         /** 회신번호 형식 오류 */
	public static final int MT_RES_SPAM = 1004;                          /** SPAM */
	public static final int MT_RES_EXCESS_EXTRA_CREDIT = 1005;           /** 사용 건수 초과 */
	public static final int MT_RES_ATTACH_FILE_NOT_EXIST = 1006;         /** 첨부 파일 없음 */
	public static final int MT_RES_ATTACH_FILE_EXIST = 1007;             /** 첨부 파일 있음 */
	public static final int MT_RES_ATTACH_FILE_SAVE_FAIL = 1008;         /** 첨부 파일 저장 실패 */
	public static final int MT_RES_NOT_EXIST_CLIENT_MSG_KEY = 1009;      /** CLIENT MSG KEY 없음 */
	public static final int MT_RES_NOT_EXIST_CONTENT = 1010;             /** 메시지 내용 없음 */
	public static final int MT_RES_NOT_EXIST_CALLBACK = 1011;            /** 회신 번호 없음 */
	public static final int MT_RES_NOT_EXIST_RECIPIENT_INFO = 1012;      /** 수신자 정보 없음 */
	public static final int MT_RES_NOT_EXIST_SUBJECT = 1013;             /** 메시지 제목 없음 */
	public static final int MT_RES_NOT_EXIST_ATTACH_FILE_KEY = 1014;     /** 첨부 파일 키 없음 */
	public static final int MT_RES_NOT_EXIST_ATTACH_FILE_NAME = 1015;    /** 첨부 파일 이름 없음 */
	public static final int MT_RES_NOT_EXIST_ATTACH_FILE_SIZE = 1016;    /** 첨부 파일 크기 없음 */
	public static final int MT_RES_NOT_EXIST_ATTACH_FILE_CONTENT = 1017; /** 첨부 파일 내용 없음 */
	public static final int MT_RES_NO_PRIVILEGE = 1018;                  /** 해당 메시지 전송 권한 없음 */
	public static final int MT_RES_TTL_OUT = 1019;                       /** TTL 초과 */
	public static final int MT_RES_CHARSET_CONVERSION_ERROR = 1020;      /** Charset conversion error */
	public static final int MT_RES_NOT_EXIST_MNTALK_KEY = 1021;          /** MNTALK_KEY 없음 */
	public static final int MT_RES_NOT_ALLOW_CALLBACK_NUMBER = 1022;     /** 발신번호 사전 등록제 미등록번호 */
	public static final int MT_RES_DUPLICATE_MT_MSG = 1023;                /** EMMA KEY 기준 중복체크 접수 거부 (client_id,EMMAkey, pdutype일치)*/
	public static final int MT_RES_AUTH_FAIL                 = 1099;     /** 인증실패      */
}
