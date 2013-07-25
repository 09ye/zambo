package com.mobilitychina.zambo.service.message.bus;

import java.util.ArrayList;

import com.mobilitychina.zambo.service.message.BaseMsgInfo;
import com.mobilitychina.zambo.service.message.MsgDefine;

import android.os.AsyncTask;
import android.util.Log;

public class BaseBus extends AsyncTask<Void, Void, Void> {

	// 委托
	private ArrayList<BusDelegate> mBusDelegate = new ArrayList<BusDelegate>();
	// 消息处理队列
	private ArrayList<BaseMsgInfo> mlist = new ArrayList<BaseMsgInfo>();

	// 处理消息
	public  void addMsg(BaseMsgInfo msg) {
		mlist.add(msg);
	}

	public  void addDeleagte(BusDelegate delegate) {
		if(!mBusDelegate.contains(delegate)){
			mBusDelegate.add(delegate);
		}
	}
	public  void removeDeleagte(BusDelegate delegate) {
		
			mBusDelegate.remove(delegate);
		
	}
	protected void doProcess(BaseMsgInfo msg){
		//#debug
		if(msg.getMsgId() == MsgDefine.MSG_CONFIG_UPDATE_RES_SUBSCRIB){
			Log.w("i", "更新");
		}
		else if(msg.getMsgId() == MsgDefine.MSG_CUSTEMER_UPDATE_RES_SUBSCRIB){
			Log.w("i", "客户更新");
		}
	}
	@Override
	protected  Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		while (true) {
			if (mlist.size() > 0) {
				BaseMsgInfo msg = mlist.remove(0);
				doProcess(msg);
				Log.e("msgId", String.valueOf(msg.getMsgId()));
				Log.e("msgContent", String.valueOf(msg.getMsgContent()));
				for (BusDelegate delegate : mBusDelegate) {
					if (delegate.processMsg(msg)) {
						break;
					}
				}
			}
			try {
				if (mlist.size() > 0) {
					Thread.sleep(100);
				} else {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// return null;
	}

}
