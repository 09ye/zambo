package com.mobilitychina.zambo.business.today.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class BaseMessageInfo {

	public static String INBOX = "IN";
	public static String SENDBOX = "S";

	public enum StatusEnum {
		NONE, UN_REPLY, REPLYED, ERROR;
	}

	public enum LoadMessStatus {
		NONE, LOAD_S, ERROR;
	}

	public static final String MSGMeeting = "HY";
	public static final String MSGLeader = "ZL";
	public static final String MSGOther = "QT";
	public static final String MSGOtherSubShare= "FX";
	public static final String MSGOtherSubReport = "HB";
	@KSoapRespField(name = "id")
	protected int id;

	@KSoapRespField(name = "mobile")
	protected String mobile;

	@KSoapRespField(name = "smscontent")
	protected String smscontent;

	@KSoapRespField(name = "smstype")
	protected String smstype;

	@KSoapRespField(name = "sendtime")
	protected String sendtime;

	protected String smsId;

	@KSoapRespField(name = "result")
	protected String result;

	protected Date lastUpdatedDate;

	protected StatusEnum statusEnum;

	@KSoapRespField(name = "status")
	protected String status;

	@KSoapRespField(name = "sendflag")
	protected String sendflag;

	@KSoapRespField(name = "title")
	protected String title;

	/**
	 * 指令发送人
	 */
	@KSoapRespField(name = "userId")
	protected long userId;
	/**
	 * 用于区分 具体哪次 指令
	 */

	@KSoapRespField(name = "smsTotalId")
	protected long smsTotalId;

	@KSoapRespField(name = "userName")
	protected String userName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StatusEnum getStatusEnum() {
		if ("N".equals(status)) {
			statusEnum = StatusEnum.UN_REPLY;
		} else if ("Y".equals(status)) {
			statusEnum = StatusEnum.REPLYED;
		} else {
			statusEnum = StatusEnum.NONE;
		}
		return statusEnum;
	}

	public void setStatusEnum(StatusEnum statusEnum) {
		if ("N".equals(status)) {
			statusEnum = StatusEnum.UN_REPLY;
		} else if ("Y".equals(status)) {
			statusEnum = StatusEnum.REPLYED;
		} else {
			statusEnum = StatusEnum.NONE;
		}

		this.statusEnum = statusEnum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmscontent() {
		return smscontent;
	}

	public void setSmscontent(String smscontent) {
		this.smscontent = smscontent;
	}

	public String getSmstype() {
		return smstype;
	}

	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSendflag() {
		return sendflag;
	}

	public void setSendflag(String sendflag) {
		this.sendflag = sendflag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getSmsTotalId() {
		return smsTotalId;
	}

	public void setSmsTotalId(long smsTotalId) {
		this.smsTotalId = smsTotalId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static List<MessageInfo> getMessByPhone(String flag, String types,
			List<MessageInfo> list) {
		String[] strarr = types.split(",");
		String type2 = "Y,N";
		String[] type2arr = type2.split(",");
		ArrayList<MessageInfo> msgs = new ArrayList<MessageInfo>();
		if (list != null) {
			if (flag.endsWith(SENDBOX)) {
				for (MessageInfo messageInfo : list) {
					if (messageInfo.getStatus().endsWith(SENDBOX)) {
						msgs.add(messageInfo);
					}
				}
			} else {
				for (MessageInfo messageInfo : list) {
					if (!messageInfo.getStatus().endsWith(SENDBOX)) {
						msgs.add(messageInfo);
					}
				}
			}
		}
		return msgs;
	}

}
