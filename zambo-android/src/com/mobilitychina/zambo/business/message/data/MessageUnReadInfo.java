package com.mobilitychina.zambo.business.message.data;

import com.mobilitychina.zambo.business.today.data.MessageInfo;
import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class MessageUnReadInfo {
	@KSoapRespField(name = "smstype")
	protected String smstype = MessageInfo.MSGMeeting ;
	@KSoapRespField(name = "unreadenum")
	protected String unreadenum = "0";
	
	public void setUnreadenum(String vaule){
		unreadenum = vaule;
	}
	public void setSmstype(String vaule){
		smstype = vaule;
	}
	public String getUnreadenum(){
		return unreadenum ;
	}
	public String setSmstype(){
		return smstype;
	}
}
