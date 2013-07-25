package com.mobilitychina.zambo.business.today.data;

public class MessageInfo extends BaseMessageInfo {

	private MessageInfo messageInfo = null;

	public MessageInfo() {
		// ORM requires
	}

	public MessageInfo(int id, String mobile, String smscontent, String smstype, String sendtime, String result,
			String smsId, String status, StatusEnum statusEnum, String sendflag, String title) {

		this.id = id;
		this.mobile = mobile;
		this.smscontent = smscontent;
		this.smstype = smstype;
		this.sendtime = sendtime;
		this.result = result;
		this.smsId = smsId;
		this.status = status;
		this.statusEnum = statusEnum;
		this.sendflag = sendflag;
		this.title = title;
	}

	public MessageInfo(int id, String mobile, String smscontent, String smstype, String sendtime, String result,
			String smsId, String status, StatusEnum statusEnum, String sendflag, String title, Long userId,
			Long smsTotalId, String userName) {

		this.id = id;
		this.mobile = mobile;
		this.smscontent = smscontent;
		this.smstype = smstype;
		this.sendtime = sendtime;
		this.result = result;
		this.smsId = smsId;
		this.status = status;
		this.statusEnum = statusEnum;
		this.sendflag = sendflag;
		this.title = title;
		this.userId = userId;
		this.smsTotalId = smsTotalId;
		this.userName = userName;
	}

	public MessageInfo getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}

	
	public void updateStatusEnum() {
		// Get all orders for the shop and day

		// if no order, and status not NO-Order, set so NONE
		statusEnum = StatusEnum.NONE;
	}
	public String getSendDate() {
		return sendtime;
	}

}
