package com.mobilitychina.zambo.business.message;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.mobilitychina.common.base.BaseFragment;
import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.localcontent.ContentManager.UploadStatusListener;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.business.message.data.MessageUnReadInfo;
import com.mobilitychina.zambo.business.today.data.MessageInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.Statistics;
/**
 * 短信界面
 * @author zywang
 *
 */
public class UserMessageFragment extends BaseFragment implements OnClickListener, ITaskListener,UploadStatusListener {

	private RelativeLayout mLayOutMessage;
	private RelativeLayout mLayOutLeaderMessage;
	private RelativeLayout mLayOutOtherMessage;
	private RelativeLayout mLayOutReportMessageSendBox;
	private RelativeLayout mLayOutLeaderMessageSendBox;
	private RelativeLayout mLayOutShareMessageSendBox;
	private Button mBtnSendMessage;
	private Task mTaskUnRead;
	private TextView mTxtMeetingUnReadNum;
	private TextView mTxtLeaderUnReadNum;
	private TextView mTxtOtherUnReadNum;
	
	//消息队列
//	private RelativeLayout mCheckInRelativeLayout;
//	private TextView checkInNumTextView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_usermessage, container, false);
		mTitleBar = (BaseTitlebar) view.findViewById(R.id.title_bar);
		mTitleBar.setTitle("消息中心");
		mLayOutMessage = (RelativeLayout)view.findViewById(R.id.layOutMessage);
		mLayOutMessage.setOnClickListener(this);
		mLayOutLeaderMessage = (RelativeLayout)view.findViewById(R.id.layOutLeaderMessage);
		mLayOutLeaderMessage.setOnClickListener(this);
		mLayOutOtherMessage = (RelativeLayout)view.findViewById(R.id.layOutOtherMessage);
		mLayOutOtherMessage.setOnClickListener(this);
		
		mLayOutLeaderMessageSendBox = (RelativeLayout)view.findViewById(R.id.layOutLeaderMessageSendBox);
		mLayOutLeaderMessageSendBox.setOnClickListener(this);
		mLayOutShareMessageSendBox = (RelativeLayout)view.findViewById(R.id.layOutShareMessageSendBox);
		mLayOutShareMessageSendBox.setOnClickListener(this);
		mLayOutReportMessageSendBox = (RelativeLayout)view.findViewById(R.id.layOutReportMessageSendBox);
		mLayOutReportMessageSendBox.setOnClickListener(this);
		
		mTxtMeetingUnReadNum = (TextView)view.findViewById(R.id.txtMeetingUnReadNum);
		mTxtLeaderUnReadNum = (TextView)view.findViewById(R.id.txtLeaderUnReadNum);
		mTxtOtherUnReadNum = (TextView)view.findViewById(R.id.txtOtherUnreadNum);
		mBtnSendMessage = (Button)view.findViewById(R.id.btnSenderMessage);
		mBtnSendMessage.setOnClickListener(this);
		
		//消息队列
//		mCheckInRelativeLayout = (RelativeLayout)view.findViewById(R.id.layOutCheckIn);
//		mCheckInRelativeLayout.setOnClickListener(this);
//		checkInNumTextView = (TextView)view.findViewById(R.id.check_in_num);
//		CheckInManager.instance().registerListener(this);
		return view;
	}


	
	private BaseTitlebar mTitleBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mLayOutMessage){
			Intent intent = new Intent(this.getActivity(), UserMessageInBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGMeeting);
			startActivity(intent);
			Statistics.sendEvent("message", "meeting_notice", "",( long )0);
		}else if (v == mLayOutLeaderMessage){
			Intent intent = new Intent(this.getActivity(), UserMessageInBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGLeader);
			startActivity(intent);
			Statistics.sendEvent("message", "superior_command", "",( long )0);
		}else if (v == mLayOutOtherMessage){
			Intent intent = new Intent(this.getActivity(), UserMessageInBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGOther);
			startActivity(intent);
			Statistics.sendEvent("message", "otherMessage", "",( long )0);
		}else if (v == mBtnSendMessage){
			Intent intent = new Intent(this.getActivity(), SendMessageActivity.class);
			startActivity(intent);
			Statistics.sendEvent("message", "message_send", "",( long )0);
		}else if (v == mLayOutLeaderMessageSendBox){
			Intent intent = new Intent(this.getActivity(), UserMessageSendBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGLeader);
			startActivity(intent);
			Statistics.sendEvent("message", "myCommand", "",( long )0);
		}else if (v == mLayOutReportMessageSendBox){
			Intent intent = new Intent(this.getActivity(), UserMessageSendBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGOther);
			intent.putExtra("subtype", MessageInfo.MSGOtherSubReport);
			startActivity(intent);
			Statistics.sendEvent("message", "myReport", "",( long )0);
		}else if (v == mLayOutShareMessageSendBox){
			Intent intent = new Intent(this.getActivity(), UserMessageSendBoxActivity.class);
			intent.putExtra("type", MessageInfo.MSGOther);
			intent.putExtra("subtype", MessageInfo.MSGOtherSubShare);
			startActivity(intent);
			EasyTracker.getTracker().sendEvent("message", "myShare", "",( long )0);
		}
//		else if(v == mCheckInRelativeLayout){
//			Intent intent = new Intent(this.getActivity(), OfflineMessageListActivity.class);
//			intent.putExtra("type", OfflineMessageType.CHECKIN);
//			startActivity(intent);
//		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
//		updateOfflineMsgNumber();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		updateOfflineMsgNumber();
		if (mTaskUnRead != null) {
			return;
		}
		mTaskUnRead = SoapService.getMessageUnReadNum();
		mTaskUnRead.setListener(this);
		mTaskUnRead.start();
		if(this.isVisible() && this.getActivity() instanceof BaseActivity){
			
			((BaseActivity)(this.getActivity())).showProgressDialog("正在加载...");
		}
	}

	@Override
	public void onTaskFailed(Task arg0) {
		// TODO Auto-generated method stub
		if (this.getActivity() instanceof BaseActivity) {
			((BaseActivity) (this.getActivity())).dismissDialog();
		}
		mTaskUnRead = null;
	}
	
	@Override
	public void onTaskFinished(Task arg0) {
		// TODO Auto-generated method stub
		if(this.getActivity() instanceof BaseActivity){
			((BaseActivity)(this.getActivity())).dismissDialog();
		}
		if(arg0.getResult() != null){
			List<MessageUnReadInfo> list = RespFactory.getService().fromResp(
					MessageUnReadInfo.class, arg0.getResult());
			if(list != null){
				for (MessageUnReadInfo iterable_element : list) {
					if(iterable_element.setSmstype().equals(MessageInfo.MSGMeeting)){
						mTxtMeetingUnReadNum.setText(iterable_element.getUnreadenum());
					}else if(iterable_element.setSmstype().equals(MessageInfo.MSGLeader)){
						mTxtLeaderUnReadNum.setText(iterable_element.getUnreadenum());
					}else {
						mTxtOtherUnReadNum.setText(iterable_element.getUnreadenum());
					}
				}
			}
		}
		mTaskUnRead = null;
	}
	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUploadProgress(int itemId, int progress) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUploadReload() {
//		updateOfflineMsgNumber();
	}
	
	private void updateOfflineMsgNumber(){
//		if(checkInNumTextView != null){
//			checkInNumTextView.setText(CheckInManager.instance().getContents().length+"");
//		}
	}
	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}

}
