package com.mobilitychina.zambo.business.message;

import java.util.List;

import android.os.Bundle;
import android.widget.TextView;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.today.data.MessageInfo;
import com.mobilitychina.zambo.business.today.data.BaseMessageInfo.StatusEnum;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;

public class UserMessageSenderReplayActivity extends BaseDetailActivity
		implements ITaskListener {
	public static final String INTENT_SMSID = "Intent_SmsId";
	public static final String INTENT_SMSSENDTIME = "Intent_SmsSendtime";
	public static final String INTENT_SMSCONTENT = "Intent_SmsContent";
	
	private Task mTaskRelyTask;
	private TextView mTxtSenderTime;
	private TextView mTxtRecive;
	private TextView mTxtContent;
	private TextView mTxtUnRead;
	private TextView mTxtReaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_usermessage_sendreply);
		this.setTitle("查看信息");
		mTxtSenderTime = (TextView) this.findViewById(R.id.txtSendTime);
		mTxtRecive = (TextView) this.findViewById(R.id.txtRecive);
		mTxtContent = (TextView) this.findViewById(R.id.txtContent);
		mTxtUnRead = (TextView) this.findViewById(R.id.txtUnRead);
		mTxtReaded = (TextView) this.findViewById(R.id.txtReaded);
		mTxtSenderTime.setText( this.getIntent().getExtras().getString(INTENT_SMSSENDTIME, ""));
		mTxtContent.setText( this.getIntent().getExtras().getString(INTENT_SMSCONTENT, ""));
		this.init(this.getIntent().getExtras().getString(INTENT_SMSID, ""));
	}

	private void init(String smsId) {
		mTaskRelyTask = SoapService.getSendWaitMessReplyTask(smsId);
		mTaskRelyTask.setListener(this);
		mTaskRelyTask.start();
	}

	@Override
	public void onTaskFailed(Task arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task arg0) {
		// TODO Auto-generated method stub
		if (arg0.getResult() != null) {
			List<MessageInfo> list = RespFactory.getService().fromResp(
					MessageInfo.class, arg0.getResult());
			if (list != null) {
				StringBuffer sbUnRead = new StringBuffer();
				StringBuffer sbReaded = new StringBuffer();
				for (MessageInfo messageInfo : list) {
					if (messageInfo.getStatusEnum() == StatusEnum.REPLYED) {
						sbReaded.append(messageInfo.getUserName() + ",");
					} else if (messageInfo.getStatusEnum() == StatusEnum.UN_REPLY) {
						sbUnRead.append(messageInfo.getUserName() + ",");
					}
				}
				if(sbReaded.length()>0){
					sbReaded.delete(sbReaded.length()-1, sbReaded.length());
				}
				if(sbUnRead.length()>0){
					sbUnRead.delete(sbUnRead.length()-1, sbUnRead.length());
				}
				String receiveall;
				if(sbReaded.length()>0 && sbUnRead.length()>0){
					receiveall = sbUnRead.toString() + "," +  sbReaded.toString();
				}else{
					receiveall = sbUnRead.toString()  +  sbReaded.toString();
				}
				this.mTxtUnRead.setText(sbUnRead.toString());
				this.mTxtReaded.setText(sbReaded.toString());
				this.mTxtRecive.setText(receiveall);
			}

		}
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
