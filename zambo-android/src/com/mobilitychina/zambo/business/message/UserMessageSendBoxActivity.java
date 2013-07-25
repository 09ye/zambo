package com.mobilitychina.zambo.business.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;


import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.app.BaseListActivity;
import com.mobilitychina.zambo.business.today.data.MessageInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.SendboxItem;

public class UserMessageSendBoxActivity extends BaseDetailListActivity implements ITaskListener, OnItemClickListener{

	private String smsType;
	private String subSmsType;//子类型
	private Task mTaskMessageList;
	private MsgAdapter mAdapter;
	private List<MessageInfo> mMessageList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_usermessage_senderbox);
		String msg = "发邮箱";
		smsType = this.getIntent().getStringExtra("type");
		subSmsType = this.getIntent().getExtras().getString("subtype", "");
		if(smsType.equals(MessageInfo.MSGLeader)){
			msg += "(我的指令)";
		}else if (smsType.equals(MessageInfo.MSGOther)&&subSmsType.equals(MessageInfo.MSGOtherSubShare)){
			msg += "(我的分享)";
		}else {
			msg += "(我的汇报)";
		}
		this.setTitle(msg);
		this.init();
		this.getListView().setOnItemClickListener(this);
	}
	private void init(){
		this.showProgressDialog("正在请求发件箱数据");
		if (mTaskMessageList == null) {
			mTaskMessageList = SoapService.getMessageTask("S",subSmsType.equalsIgnoreCase("")?smsType:subSmsType ,false);
			mTaskMessageList.setListener(this);
			mTaskMessageList.start();
		} 
	}
	@Override
	public void onTaskFailed(Task arg0) {
		// TODO Auto-generated method stub
		this.dismissDialog();
	}
	@Override
	public void onTaskFinished(Task arg0) {
		// TODO Auto-generated method stub
		this.dismissDialog();
		mMessageList =  RespFactory.getService().fromResp(MessageInfo.class,
				arg0.getResult());
		if(mMessageList == null){
			mMessageList = new ArrayList<MessageInfo>();
		}
		mAdapter = new MsgAdapter();
		this.setListAdapter(mAdapter);
	}
	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	private class MsgAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mMessageList.size();
		}

		@Override
		public MessageInfo getItem(int position) {
			// TODO Auto-generated method stub
			return mMessageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			SendboxItem view;
			if (convertView == null) {
				view = (SendboxItem) LayoutInflater.from(UserMessageSendBoxActivity.this).inflate(
						R.layout.item_sendbox, null);
			} else {
				view = (SendboxItem) convertView;
			}
			view.setMessageValue(getItem(position));
			return view;
		}
	
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if(smsType.equals(MessageInfo.MSGLeader)){
			MessageInfo info =mMessageList.get(position);
			Intent intent = new Intent(this,UserMessageSenderReplayActivity.class);
			intent.putExtra(UserMessageSenderReplayActivity.INTENT_SMSID, String.valueOf(info.getSmsTotalId()));
			intent.putExtra(UserMessageSenderReplayActivity.INTENT_SMSSENDTIME, info.getSendDate());
			intent.putExtra(UserMessageSenderReplayActivity.INTENT_SMSCONTENT, info.getSmscontent());
			this.startActivity(intent);
			Statistics.sendEvent("message_myCommand", "detail_message", "",( long )0);
		}
	}
	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
