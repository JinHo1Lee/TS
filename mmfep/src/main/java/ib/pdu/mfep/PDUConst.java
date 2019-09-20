package ib.pdu.mfep;

public class PDUConst{
	/**
	 * DATA TYPE SIZE
	 */
	public static final int SIZE_HEADER_TYPE = 4;
	public static final int SIZE_HEADER_LENGTH = 8;
	public static final int SIZE_INT = 4;
	public static final int SIZE_SHORT = 2;
	public static final int SIZE_LONG = 8;
	
	/**
	 * MSG CLASS
	 */
	public static final int MSG_CLASS_NORMAL = 1;  /** 메시지 등급 코드 - 일반 */
	public static final int MSG_CLASS_ADULT = 2;      /** 메시지 등급 코드 - 성인*/
	
	/**
	 * NUMBER PORTED
	 */
	public static final  char CODE_DEF = ' ';
	public static final  char CODE_YES = 'Y';
	public static final  char CODE_NO = 'N';
	
	/**
	 * PDU TYPE
	 */
	public static final int PDU_TYPE_SESSION_CHECK_REQ = 0x0102000F;  // SESSION 상태 확인 요청
	public static final int PDU_TYPE_SESSION_CHECK_RES = 0x01020010;  // SESSION 상태 확인 요청 응답
	public static final int PDU_TYPE_EMMA_MT_SMS_REQ = 0x01020001;  // 일반 SMS MT 전송 요청
	public static final int PDU_TYPE_EMMA_MT_SMS_RES = 0x01020002; // 일반 SMS MT 전송 요청 응답
	public static final int PDU_TYPE_GW_MT_SMS_REQ = 0x01020011;  // 일반 SMS MT 전송 요청
	public static final int PDU_TYPE_GW_MT_SMS_RES = 0x01020012;  // EMMA_MT_SMS_REQ에 대한 응답
	public static final int PDU_TYPE_GW_MT_MMS_REQ = 0x01020015;  // MMS MT 전송 요청
	public static final int PDU_TYPE_GW_MT_MMS_RES = 0x01020016;  // EMMA_MT_MMS_REQ에 대한 응답
	public static final int PDU_TYPE_GW_MT_FILE_REQ = 0x01020017;  // MT 첨부 파일 전송 요청
	public static final int PDU_TYPE_GW_MT_FILE_RES = 0x01020018;  // EMMA_MT_FILE_REQ에 대한 응답
	public static final int PDU_TYPE_GW_MT_REPORT_REQ = 0x01020019;  // MT 전송에 대한 결과           
	public static final int PDU_TYPE_GW_MT_REPORT_RES = 0x0102001A; // EMMA_MT_REPORT_REQ에 대한 응답
	public static final int PDU_TYPE_GW_RCS_REPORT_REQ = 0x0102001B; // RCSReport
	public static final int PDU_TYPE_GW_RCS_REPORT_RES = 0x0102001C; // RCSReport
	/**
	 * PDU TAG TYPE
	 */
	public static final int TC_PDU_TYPE = 0x02000001;
	public static final int TC_MSG_CLASS = 0x20000002;
	public static final int TC_CLIENT_MSG_KEY = 0x20000003;
	public static final int TC_CONTENT_TYPE = 0x20000004;
	public static final int TC_CONTENT = 0x20000005;
	public static final int TC_MSG_TYPE = 0x20000006;
	public static final int TC_CALLBACK = 0x20000007;
	public static final int TC_RECIPIENTS = 0x20000008;
	public static final int TC_RS_MSG_KEY = 0x20000009;
	public static final int TC_RS_ID  = 0x20000010;
	public static final int TC_CLIENT_CODE = 0x20000011;
	public static final int TC_ROUTE_GROUP = 0x20000012;
	public static final int TC_WORD_CNT = 0x20000013;
	public static final int TC_TTL = 0x20000014;
	public static final int TC_PRIORITY = 0x20000015;
	public static final int TC_NUMBER_PORTED = 0x20000016;
	public static final int TC_BRIDGE_TAG = 0x20000017;
	public static final int TC_EMMA_TAG = 0x20000018;
	
	public static final int TC_COUNTRY_CODE = 0x01820008; //국가 번호
	
	/* RS Tag Code */
	public static final int TAG_CODE_CLIENT_ID                  = 0x01810001;  // 클라이언트가 사용할 수 있도록 발급 받은 ID                 
	public static final int TAG_CODE_SYMMETRIC_KEY              = 0x01810002;  // 인증 요청 후 암/복호화 시 사용할 대칭키                    
	public static final int TAG_CODE_CI_RV_ST                   = 0x01810003;  // CLIENT_ID와 서버에서 받은 random value, SERVER_TIME을 붙여 놓은 값 ('_'가 구분자)
	public static final int TAG_CODE_LAST_BLOCK                 = 0x01810004;  // 최종 차단 번호 파일 이름 (CLIENT_ID와 생성시간)            
	public static final int TAG_CODE_SESSION_TYPE               = 0x01810005;  // 세션 종류
	public static final int TAG_CODE_PDU_VERSION                = 0x01810006;  // PDU 버전
	public static final int TAG_CODE_EMMA_VERSION               = 0x01810007;  // EMMA 버전                                                  
	public static final int TAG_CODE_SESSION_CHARSET            = 0x01810008;  // 세션에 적용될 CHARSET (default는 KSC-5601)                 
	public static final int TAG_CODE_RS_ID                      = 0x01810009;  // RS ID
	public static final int TAG_CODE_RS_ADDRESS                 = 0x0181000A;  // EMMA가 접속할 RS 주소 정보 (IP:Port 형식)                  
	public static final int TAG_CODE_USE_SMSMT                  = 0x0181000B;  // 일반 SMS MT 사용 여부 (Y or N)
	public static final int TAG_CODE_USE_CALLBACKURL            = 0x0181000C;  // Callback URL 사용 여부 (Y or N)                            
	public static final int TAG_CODE_USE_MMSMT                  = 0x0181000D;  // MMS MT 사용 여부 (Y or N)
	public static final int TAG_CODE_USE_SMSMO                  = 0x0181000E;  // SMS MO 사용 여부 (Y or N)                                  
	public static final int TAG_CODE_USE_MMSMO                  = 0x0181000F;  // MMS MO 사용 여부 (Y or N)                                  
	public static final int TAG_CODE_CNT_SESSION_MT             = 0x01810010;  // 생성할 수 있는 SMS MT 세션의 수                            
	public static final int TAG_CODE_CNT_SESSION_SMSMO          = 0x01810011;  // 생성할 수 있는 SMS MO 세션의 수                            
	public static final int TAG_CODE_CNT_SESSION_MMSMO          = 0x01810012;  // 생성할 수 있는 MMS MT 세션의 수                            
	public static final int TAG_CODE_CNT_SESSION_REPORT         = 0x01810013;  // 생성할 수 있는 REPORT 세션의 수                            
	public static final int TAG_CODE_CHANGE_BLOCKINFO           = 0x01810014;  // 차단 번호 정보 변경 여부 (Y or N)                          
	public static final int TAG_CODE_BLOCK_FILENAME             = 0x01810015;  // 차단 번호 리스트가 들어 있는 파일 이름                     
	public static final int TAG_CODE_BLOCK_CONTENT              = 0x01810016;  // 차단 번호 파일의 내용
	public static final int TAG_CODE_CLIENT_SPAM_CHECK          = 0x01810017;  // CLIENT_ID의 SPAM Check 여부 (Y or N)                       
	public static final int TAG_CODE_CLIENT_READ_REPLY          = 0x01810018;  // Read_Reply 결과 수신 여부 (Y or N)
	public static final int TAG_CODE_CREDIT_CLIENT_CHECK        = 0x01810019;  // CLIENT_ID의 건수 제한 사용 여부 (Y or N)                   
	public static final int TAG_CODE_CREDIT_CLIENT_RESET        = 0x0181001A;  // CLIENT_ID의 매월 초 건수 초기화 여부 (Y or N)              
	public static final int TAG_CODE_CREDIT_CLIENT_CNT_GIVING   = 0x0181001B;  // CLIENT_ID에 최초 부여된 건수
	public static final int TAG_CODE_CREDIT_CLIENT_CNT_EXTRA    = 0x0181001C;  // CLIENT_ID의 잔여 건수
	public static final int TAG_CODE_CREDIT_GROUP_CHECK         = 0x0181001D;  // CLIENT_ID GROUP의 건수 제한 사용 여부 (Y or N)             
	public static final int TAG_CODE_CREDIT_GROUP_RESET         = 0x0181001E;  // CLIENT_ID GROUP의 매월 초 건수 초기화 여부 (Y or N)        
	public static final int TAG_CODE_CREDIT_GROUP_CNT_GIVING    = 0x0181001F;  // CLIENT_ID GROUP에 최초 부여된 건수
	public static final int TAG_CODE_CREDIT_GROUP_CNT_EXTRA     = 0x01810020;  // CLIENT_ID GROUP의 잔여 건수
	public static final int TAG_CODE_CRYPTO_METHOD              = 0x01810021;  // 암호화 방법
	public static final int TAG_CODE_CRYPTO_KEY_SIZE            = 0x01810022;  // 대칭키의 길이                                              
	public static final int TAG_CODE_RI_RV                      = 0x01810023;  // RS_ID와 서버에서 받은 random value를 붙여 놓은 값 ('_'가 구분자)
	public static final int TAG_CODE_PUBLIC_KEY                 = 0x01810024;  // 공개 키
	public static final int TAG_CODE_RANDOM_VALUE               = 0x01810025;  // Random value                                               
	public static final int TAG_CODE_CREDIT_TYPE                = 0x01810026;  // 여신 종류                                                  
	public static final int TAG_CODE_CREDIT_INFO                = 0x01810027;  // 여신 (건수 제한) 정보                                      
	public static final int TAG_CODE_SERVER_TIME                = 0x01810028;  // 서버 시간                                                  
	public static final int TAG_CODE_PRIVATE_KEY                = 0x01810029;  // 개인 키
	public static final int TAG_CODE_ENCRYPTED_SYMM_KEY         = 0x0181002A;  // 수신자 공개키로 암호화된 대칭키                            
	public static final int TAG_CODE_ENCRYPTED_DATA             = 0x0181002B;  // 대칭키로 암호화된 Data                                     
	public static final int TAG_CODE_CREDIT_PERIOD              = 0x0181002C;  // 여신 기간 종류
	public static final int TAG_CODE_CREDIT_CLIENT_CONFIG       = 0x0181002D;  // Client 여신 Config 정보                                    
	public static final int TAG_CODE_CREDIT_GROUP_CONFIG        = 0x0181002E;  // Group 여신 Config 정보                                     
	public static final int TAG_CODE_CREDIT_CLIENT_BLOCK        = 0x0181002F;  // Client 여신 초과시 전송 제한 여부                          
	public static final int TAG_CODE_CREDIT_GROUP_BLOCK         = 0x01810030;  // Group 여신 초과시 전송 제한 여부                           
	public static final int TAG_CODE_CREDIT_CLIENT_ALERT        = 0x01810031;  // Client 여신 초과시 정해진 담당자에게 Alert 여부            
	public static final int TAG_CODE_CREDIT_GROUP_ALERT         = 0x01810032;  // Group 여신 초과시 정해진 담당자에게 Alert 여부
	public static final int TAG_CODE_CLIENT_PASSWORD            = 0x01810033;  // CLIENT OneTimePassword
	public static final int TAG_CODE_SMSMT_LIMIT_COUNT          = 0x01810034;  // 1분당 전송 가능한 SMS MT 최대 건수 
	public static final int TAG_CODE_CBU_LIMIT_COUNT            = 0x01810035;  // 1분당 전송 가능한 Callback URL 최대 건수
	public static final int TAG_CODE_MMSMT_LIMIT_COUNT          = 0x01810036;  // 1분당 전송 가능한 MMS MT 최대 건수
	public static final int TAG_CODE_MNTALK_ENABLE              = 0x01810037;  // m&Talk 으로 전송 가능 여부 (Y or N)
	public static final int TAG_CODE_MNTALK_ONLY                = 0x01810038;  // m&Talk 으로만 전송할 지 여부 (Y or N)
	public static final int TAG_CODE_AUTH_IP_ADDR               = 0x01810039;  // Client 가 인증에 사용할 IP Address
	public static final int TAG_CODE_AUTH_MAC_ADDR              = 0x0181003A;  // Client 가 인증에 사용할 MAC Address
	public static final int TAG_CODE_EMMA_OS_INFO               = 0x0181003B;  // EMMA 설치된 시스템 OS 종류/버전 값
	public static final int TAG_CODE_EMMA_DB_INFO               = 0x0181003C;  // EMMA 설치된 시스템 DB 종류/버전 값
	public static final int TAG_CODE_EMMA_JRE_INFO              = 0x0181003D;  // EMMA 설치된 시스템 JRE 종류/버전 값
	public static final int TAG_CODE_EMMA_INSTALL_PATH          = 0x0181003E;  // EMMA 설치된 path 값
	public static final int TAG_CODE_ALL_SPAM_CHECK             = 0x0181003F;  // 모든 client대상 SPAM Check 여부 (Y or N)
	public static final int TAG_CODE_USE_MMORPT                 = 0x01810040;  // MMO Report 사용 여부 (Y or N, default N) 
	public static final int TAG_CODE_USE_SMORPT                 = 0x01810041;  // SMO Report 사용 여부 (Y or N, default N) 
	public static final int TAG_CODE_CREDIT_DEDUCT_COUNT        = 0x01810042;  // Credit 차감건수 
	public static final int TAG_CODE_NOTIFY_TYPE                = 0x01810043;  // Auto to RS Client Notify Type 
	
	public static final int TAG_CODE_CLIENT_MSG_KEY             = 0x01820001;  // 클라이언트와 RS가 메시지 송수신 시 사용하는 키
	public static final int TAG_CODE_SUBJECT                    = 0x01820002;  // 메시지 제목                                   
	public static final int TAG_CODE_CONTENT                    = 0x01820003;  // 메시지 내용                                   
	public static final int TAG_CODE_RECIPIENT_CNT              = 0x01820004;  // 수신 번호 개수                                
	public static final int TAG_CODE_RECIPIENT_ORDER            = 0x01820005;  // 수신 번호 순번                                
	public static final int TAG_CODE_RECIPIENT_NET              = 0x01820006;  // 전송 요청 통신사                              
	public static final int TAG_CODE_RECIPIENT_NPSEND           = 0x01820007;  // 번호 이동 결과 수신 시 재전송 할건지 여부     
	public static final int TAG_CODE_COUNTRY_CODE               = 0x01820008;  // 국가 번호                                     
	public static final int TAG_CODE_RECIPIENT_NUM              = 0x01820009;  // 수신 번호                                     
	public static final int TAG_CODE_REPLACE_WORD               = 0x0182000A;  // 동보 단어                                     
	public static final int TAG_CODE_WORD_CNT                   = 0x0182000B;  // 동보 단어 개수                                
	public static final int TAG_CODE_RECIPIENT_INFO             = 0x0182000C;  // 수신자 정보                                   
	public static final int TAG_CODE_MSG_CLASS                  = 0x0182000D;  // 메시지 등급                                   
	public static final int TAG_CODE_CALLBACK                   = 0x0182000E;  // 회신 번호                                     
	public static final int TAG_CODE_TTL                        = 0x0182000F;  // 메시지 전송 유효 시간 (절대 시간)             
	public static final int TAG_CODE_PRIORITY                   = 0x01820010;  // 전송 우선 순위                                
	public static final int TAG_CODE_CHARSET                    = 0x01820011;  // 메시지의 CHARSET                              
	public static final int TAG_CODE_ATTACH_FILE_KEY            = 0x01820012;  // 첨부 파일 키                                  
	public static final int TAG_CODE_ATTACH_FILE_NAME           = 0x01820013;  // 첨부 파일 이름                                
	public static final int TAG_CODE_ATTACH_FILE_SIZE           = 0x01820014;  // 첨부 파일 크기                                
	public static final int TAG_CODE_ATTACH_FILE_CONTENT        = 0x01820015;  // 첨부 파일 내용                                
	public static final int TAG_CODE_CARRIER                    = 0x01820016;  // 최종 착신망                                   
	public static final int TAG_CODE_MO_RECIPIENT               = 0x01820017;  // MO 특번                                       
	public static final int TAG_CODE_MO_ORIGINATOR              = 0x01820018;  // MO 발신 번호                                  
	public static final int TAG_CODE_MO_CALLBACK                = 0x01820019;  // MO 발신 시 발신자가 입력한 번호               
	public static final int TAG_CODE_DATE_CLIENT_REQ            = 0x0182001A;  // 클라이언트의 전송 요청 시간                   
	public static final int TAG_CODE_DATE_TSRECV                = 0x0182001B;  // TS가 RS로부터 MT 메시지를 받은 시간           
	public static final int TAG_CODE_DATE_TSSENT                = 0x0182001C;  // TS가 MT 메시지를 통신사로 전송한 시간         
	public static final int TAG_CODE_DATE_TSRSLTRECV            = 0x0182001D;  // TS가 통신사로부터 결과를 수신한 시간          
	public static final int TAG_CODE_DATE_RSLT                  = 0x0182001E;  // 결과 발생 시간 (성공일 경우 폰 수신 시각)     
	public static final int TAG_CODE_DATE_TSRSLTSENT            = 0x0182001F;  // TS가 결과를 RS로 전송한 시간                  
	public static final int TAG_CODE_DATE_MO                    = 0x01820020;  // MO 발생 시간                                  
	public static final int TAG_CODE_DATE_MO_TSRECV             = 0x01820021;  // MO 메시지를 TS가 통신사로부터 받은 시간       
	public static final int TAG_CODE_DATE_MO_TSSENT             = 0x01820022;  // MO 메시지를 TS가 RS로 전송한 시간             
	public static final int TAG_CODE_DATE_MO_RSRECV             = 0x01820023;  // MO 메시지를 RS가 TS로부터 받은 시간                     
	public static final int TAG_CODE_DATE_MO_RSSENT             = 0x01820024;  // MO 메시지를 RS가 Client에 전송한 시간                   
	public static final int TAG_CODE_RS_MSG_KEY                 = 0x01820025;  // RS와 TS가 메시지 송수신 시 사용하는 키                  
	public static final int TAG_CODE_CLIENT_CODE                = 0x01820026;  // Client Code                                             
	public static final int TAG_CODE_ROUTE_GROUP                = 0x01820027;  // Routing Group                                           
	public static final int TAG_CODE_NUMBER_PORTED              = 0x01820028;  // 번호 이동 여부 (Y or N)                                 
	public static final int TAG_CODE_TSCODE                     = 0x01820029;  // TS 식별 코드. TS별로 Unique                             
	public static final int TAG_CODE_NP_RESULT                  = 0x0182002A;  // 번호 이동하여 재전송한 메시지의 결과인지 여부           
	public static final int TAG_CODE_CARRIER_KEY                = 0x0182002B;  // 통신사에서 받은 키                                      
	public static final int TAG_CODE_MSG_TYPE                   = 0x0182002C;  // 메시지 종류                                             
	public static final int TAG_CODE_ATTACH_FILE_INFO           = 0x0182002D;  // 첨부 파일 정보                                          
	public static final int TAG_CODE_CLIENT_GRP_CODE            = 0x0182002E;  // Client Group Code                                       
	public static final int TAG_CODE_REPORT_TYPE                = 0x0182002F;  // REPORT 종류                                             
	public static final int TAG_CODE_PDU_TYPE                   = 0x01820030;  // PDU TYPE                                                
	public static final int TAG_CODE_RECIPIENTS                 = 0x01820031;  // 수신자 정보 리스트                                      
	public static final int TAG_CODE_REPLACE_WORDS              = 0x01820032;  // 동보 단어 리스트                                        
	public static final int TAG_CODE_ATTACH_FILES               = 0x01820033;  // 첨부 파일 리스트                                        
	public static final int TAG_CODE_ATTACH_FILE_KEYS           = 0x01820034;  // 첨부 파일 키 리스트                                     
	public static final int TAG_CODE_ATTACH_FILE_CNT            = 0x01820035;  // 첨부 파일 키 개수                                       
	public static final int TAG_CODE_FAULT_INFO                 = 0x01820036;  // 장애 정보                                               
	public static final int TAG_CODE_FAULT_TYPE                 = 0x01820037;  // 장애 유형                                               
	public static final int TAG_CODE_FAULT_VALUE                = 0x01820038;  // 장애 값                                                 
	public static final int TAG_CODE_FAULT_SRC                  = 0x01820039;  // 장애 소스                                               
	public static final int TAG_CODE_FAULT_RMDF                 = 0x0182003A;  // 자동 응답된 MO 메시지인지 여부 (Y or N)                 
	public static final int TAG_CODE_MO_RECIPIENT_CNT           = 0x0182003B;  // MO 특번 개수                                            
	public static final int TAG_CODE_ATTACH_FILE_CARRIER        = 0x0182003C;  // 첨부 파일 해당 통신사                                   
	public static final int TAG_CODE_PAYMENT_CODE               = 0x0182003D;  // 정산 부서 코드                                          
	public static final int TAG_CODE_EMMA_TAG                   = 0x0182003E;  // EMMA 지정 Tag                                           
	public static final int TAG_CODE_ALERT_ID                   = 0x0182003F;  // Alert ID                                                
	public static final int TAG_CODE_ALERT_CODE                 = 0x01820040;  // Alert Code                                              
	public static final int TAG_CODE_CLIENT_CODE_CNT            = 0x01820041;  // Client Code count                                       
	public static final int TAG_CODE_EMS_TYPE                   = 0x01820042;  // EMS Type 여부 (Y or N)                                  
	public static final int TAG_CODE_CONTENT_INFO               = 0x01820043;  // 메시지 Info = (TAG_CODE_CONTENT_TYPE + TAG_CODE_CONTENT)
	public static final int TAG_CODE_CONTENT_TYPE               = 0x01820044;  // IBML 메시지 Type                                        
	public static final int TAG_CODE_BRIDGE_TAG                 = 0x01820045;  // Bridge 지정 Tag                                         
	public static final int TAG_CODE_MEDIA_TYPE                 = 0x01820046;  // MT 유입 매체 구분 코드
	public static final int TAG_CODE_CARRIER_RETRY              = 0x01850003;
	public static final int TAG_CODE_RCS_REPORT 				= 0x02000001;	
	public static final int TAG_CODE_RS_RECV_TIME				= 0x01850004;  // RS RECV Time (RS -> TS로 전달, 현재 RCS TS만 수신 가능) 
	public static final int TAG_CODE_RS_SND_CARRIER				= 0x01850005; // RS 에서 라우팅 한 착신망 정보 ( RS->TS 로 전달 현재 RCS TS만 수신가능)

}
