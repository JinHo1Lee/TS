package ib.rcs.msg;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RCSMessage {
	private String agcMsgId;
	private String chatbotId;
	private String messagebaseId;
	private String contactNum;
	private String resellerId;
	private String expiryOption;
	private String groupId;
	private String header;
	private String footer;
	private String copyAllowed;
	private String productCode;
	private Map<String,String> body; 
	private List<Button> buttons;

	public RCSMessage() {
		// TODO Auto-generated constructor stub
	}
	public String getAgcMsgId() {
		return agcMsgId;
	}

	public void setAgcMsgId(String agcMsgId) {
		this.agcMsgId = agcMsgId;
	}
	
	public String getChatbotId() {
		return chatbotId;
	}
	public void setChatbotId(String chatbotId) {
		this.chatbotId = chatbotId;
	}
	public String getMessagebaseId() {
		return messagebaseId;
	}
	public void setMessagebaseId(String messagebaseId) {
		this.messagebaseId = messagebaseId;
	}
	public String getContactNum() {
		return contactNum;
	}
	public void setContactNum(String contactNum) {
		this.contactNum = contactNum;
	}
	public String getResellerId() {
		return resellerId;
	}
	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}
	public String getExpiryOption() {
		return expiryOption;
	}
	public void setExpiryOption(String expiryOption) {
		this.expiryOption = expiryOption;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public String getCopyAllowed() {
		return copyAllowed;
	}
	public void setCopyAllowd(String copyAllowed) {
		this.copyAllowed = copyAllowed;
	}
	public Map<String, String> getBody() {
		return body;
	}
	public void setBody(Map<String, String> body) {
		this.body = body;
	}
	public List<Button> getButtons() {
		return buttons;
	}
	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public static class Button{
		private List<Suggestions> suggestions;
		
		public List<Suggestions> getSuggestions() {
			return suggestions;
		}
		public void setSuggestions(List<Suggestions> suggestions) {
			this.suggestions = suggestions;
		}
		public static class Suggestions{
			private Action action;
			public Suggestions() {
				// TODO Auto-generated constructor stub
				action = new Action();
			}
			public Action getAction() {
				return action;
			}
			public void setAction(Action action) {
				this.action = action;
			}
			public class Action{
				private UrlAction urlAction;
				private DialerAction dialerAction;
				private CalendarAction calendarAction;
				private ClipboardAction clipboardAction;
				private MapAction mapAction;
				private ComposeAction composeAction;
				
				private String displayText;
				private Map<String, String> postback;
				
				
				public Action() {
					// TODO Auto-generated constructor stub
				}
				public String getDisplayText() {
					return displayText;
				}

				public void setDisplayText(String displayText) {
					this.displayText = displayText;
				}
				public Map<String, String> getPostback() {
					return postback;
				}

				public void setPostback(Map<String, String> postback) {
					this.postback = postback;
				}
				public UrlAction getUrlAction() {
					return urlAction;
				}
				public void setUrlAction(UrlAction urlAction) {
					this.urlAction = urlAction;
				}
				
				public DialerAction getDialerAction() {
					return dialerAction;
				}
				public void setDialerAction(DialerAction dialerAction) {
					this.dialerAction = dialerAction;
				}
				public CalendarAction getCalendarAction() {
					return calendarAction;
				}
				public void setCalendarAction(CalendarAction calendarAction) {
					this.calendarAction = calendarAction;
				}
				public ClipboardAction getClipboardAction() {
					return clipboardAction;
				}
				public void setClipboardAction(ClipboardAction clipboardAction) {
					this.clipboardAction = clipboardAction;
				}
				public MapAction getMapAction() {
					return mapAction;
				}
				public void setMapAction(MapAction mapAction) {
					this.mapAction = mapAction;
				}
				public ComposeAction getComposeAction() {
					return composeAction;
				}
				public void setComposeAction(ComposeAction composeAction) {
					this.composeAction = composeAction;
				}


				public class UrlAction{
					private Map<String, String> openUrl;
					public UrlAction() {
						// TODO Auto-generated constructor stub
					}
					public Map<String, String> getOpenUrl() {
						return openUrl;
					}
					public void setOpenUrl(Map<String, String> openUrl) {
						this.openUrl = openUrl;
					}			
					
				}
				public class DialerAction{
					private Map<String, String> dialPhoneNumber;
					public DialerAction(){
						
					}
					public Map<String, String> getDialPhoneNumber() {
						return dialPhoneNumber;
					}

					public void setDialPhoneNumber(Map<String, String> dialPhoneNumber) {
						this.dialPhoneNumber = dialPhoneNumber;
					}
				}
				public class ClipboardAction{
					private Map<String, String> copyToClipboard;
					public ClipboardAction(){
						
					}
					public Map<String, String> getCopyToClipboard() {
						return copyToClipboard;
					}

					public void setCopyToClipboard(Map<String, String> copyToClipboard) {
						this.copyToClipboard = copyToClipboard;
					}
					
				}
				public class MapAction{
					private ShowLocation showLocation;
					private Map<String,String> requestLocationPush;
					
					public MapAction(){
						
					}
					public ShowLocation getShowLocation() {
						return showLocation;
					}

					public void setShowLocation(ShowLocation showLocation) {
						this.showLocation = showLocation;
					}
					

					public Map<String, String> getRequestLocationPush() {
						return requestLocationPush;
					}
					public void setRequestLocationPush(Map<String, String> requestLocationPush) {
						this.requestLocationPush = requestLocationPush;
					}


					class ShowLocation{
						private Map<String, String> location;
						private String fallbackUrl;
						public ShowLocation(){
							
						}
						public Map<String, String> getLocation() {
							return location;
						}

						public void setLocation(Map<String, String> location) {
							this.location = location;
						}
						public String getFallbackUrl() {
							return fallbackUrl;
						}

						public void setFallbackUrl(String fallbackUrl) {
							this.fallbackUrl = fallbackUrl;
						}

					}
				}
				
				public class CalendarAction{
					private Map<String, String> createCalendarEvent;
					public CalendarAction(){
						
					}
					public Map<String, String> getCreateCalendarEvent() {
						return createCalendarEvent;
					}

					public void setCreateCalendarEvent(Map<String, String> createCalendarEvent) {
						this.createCalendarEvent = createCalendarEvent;
					}
					
				}
				public class ComposeAction{
					private Map<String, String> composeTextMessage;
					private Map<String, String> composeRecordingMessage;
					
					public ComposeAction(){
						
					}
					public Map<String, String> getComposeTextMessage() {
						return composeTextMessage;
					}

					public void setComposeTextMessage(Map<String, String> composeTextMessage) {
						this.composeTextMessage = composeTextMessage;
					}

					public Map<String, String> getComposeRecordingMessage() {
						return composeRecordingMessage;
					}

					public void setComposeRecordingMessage(Map<String, String> composeRecordingMessage) {
						this.composeRecordingMessage = composeRecordingMessage;
					}
					
				}
			}
		}
	}
}
