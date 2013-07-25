package com.mobilitychina.zambo.service.message;

import com.mobilitychina.zambo.service.message.bus.BusDelegate;
import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class BaseMsgInfo {
	//消息头
	@KSoapRespField(name = "mClientId")
	private String mClientId;
	public void  setClientId(String id ){
		mClientId = id;
	}
	public String getClient(){
		return mClientId;
	}
	@KSoapRespField(name = "mMsgId")
	private int mMsgId;
	public int getMsgId(){
		return mMsgId;
	}
	public void setMsgId(int msgId) {
		mMsgId = msgId;
	}
	@KSoapRespField(name = "mMsgContent")
	private String mMsgContent;
    //消息体
	public String getMsgContent(){
		return mMsgContent;
	}
	public void setMsgContent(String msg) {
		mMsgContent = msg;
	}
	private BusDelegate mDelegate;
	public void setDelegate(BusDelegate delegate){
		mDelegate = delegate;
	}
	public BusDelegate getDelegate(){
		return mDelegate;
	}
}