/**
 * 
 */
package com.mobilitychina.zambo.business.message;

import java.util.LinkedList;
import java.util.List;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
//import android.widget.TextView;
import android.widget.Toast;


import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.today.data.BaseMessageInfo;
import com.mobilitychina.zambo.business.today.data.MessageInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.MessageItemView;

/**
 * @author 我的消息
 */
public class UserMessageInBoxActivity extends BaseDetailListActivity implements
		ITaskListener {

	protected static final String TAG = "IMessageActivity";
	// UI Elements
	//private TextView txtEmpty;
	//private ImageView imgLoading;
	private String messId;
	private String messResult;
	private MsgAdapter msgAdpter;
	private SoapTask mTaskMessageList;
	private SoapTask mTaskReadAll;
	private List<MessageInfo> mMessageList;
	private String smsType;//短信类型
	private Task mTaskRelay ;
	private Task mTaskDelete ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_usermessage_inbox);
		String msg = "收件箱";
		smsType = this.getIntent().getStringExtra("type");
		if(smsType.equals(MessageInfo.MSGLeader)){
			msg += "(上级指令)";
		}else if (smsType.equals(MessageInfo.MSGMeeting)){
			msg += "(会议通知)";
		}else {
			msg += "(其它消息)";
			mTaskReadAll = SoapService.getRecordSendWaitMessbyTypeTask(MessageInfo.MSGOther);
			mTaskReadAll.setListener(this);
			mTaskReadAll.start();
		}
		this.setTitle(msg);
		//txtEmpty = (TextView) findViewById(R.id.txtEmpty);
		//imgLoading = (ImageView) findViewById(R.id.imgLoading);
		this.init();
		this.showProgressDialog("正在获取收件箱信息");
	}

	private void init() {
		//txtEmpty.setVisibility(View.GONE);
		//if (mTaskMessageList == null) {
	    mTaskMessageList = SoapService.getMessageTask("",smsType,false);
		mTaskMessageList.setListener(this);
			
		//}
		mTaskMessageList.start();
		
	}

	private void recordMess() {
		mTaskRelay = SoapService.getSenderMessageReplayInfoTask(messId, messResult);
		mTaskRelay.setListener(this);
		mTaskRelay.start();
		this.showProgressDialog("正在回复信息");
	}


	private class MsgAdapter extends BaseAdapter {
		private List<MessageInfo> messages;
		private LayoutInflater mInflater;
		private LinkedList<MessageInfo> _expandMsgList;
		private MsgAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);

			messages = BaseMessageInfo.getMessByPhone("IN", "",
					mMessageList);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (messages != null) {
				return messages.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getItemNo(int position) {
			long i = position + 1;
			return i;
		}

		public void setExpand(MessageInfo msg) {
			if (_expandMsgList == null) {
				_expandMsgList = new LinkedList<MessageInfo>();
			}
			if (_expandMsgList.contains(msg)) {
				_expandMsgList.remove(msg);
			} else {
				_expandMsgList.addLast(msg);
			}
			this.notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageItemView holder = null;
			if (convertView == null) {
				holder = (MessageItemView) mInflater.inflate(
						R.layout.message_item, null);
			} else {
				holder = (MessageItemView) convertView;
			}

			String listNo = "";
			listNo = getItemNo(position) + "/" + getCount();
			holder.listsize.setText(listNo);

			final MessageInfo msg = (MessageInfo) getItem(position);
			String dateStr = "";
			if (msg.getSendtime() != null) {
				dateStr = msg.getSendtime().substring(5, 16);
			}
			holder.smsTotalId.setText(String.valueOf(msg.getSmsTotalId()));
			holder.smsTotalId.setVisibility(View.GONE);

			holder.setExpand(_expandMsgList != null
					&& _expandMsgList.contains(msg));
			holder.info.setText(msg.getSmscontent());

			holder.source.setText(msg.getTitle());
			//holder.source.setText("提督");
			holder.source.setTextColor(R.color.text_dark);
			if ("HY".equals(msg.getSmstype())) {// 消息类型：会议 需要回复TY（同意） ， JJ（拒绝）
				holder.repokBtn.setVisibility(View.VISIBLE);
				holder.refuseBtn.setVisibility(View.VISIBLE);
				holder.title.setText(dateStr);
				holder.repread.setVisibility(View.GONE);
				holder.btn_see_reply.setVisibility(View.GONE);

				if ("Y".equals(msg.getStatus())) {// 已读
					if ("TY".equals(msg.getResult())) {
						holder.refuseBtn.setVisibility(View.INVISIBLE);
						holder.refuseBtn.setWidth(0);
						holder.refuseBtn.setPadding(0, 0, 0, 0);
					} else {
						holder.repokBtn.setVisibility(View.INVISIBLE);
						holder.repokBtn.setWidth(0);
						holder.repokBtn.setPadding(0, 0, 0, 0);
						
					}

				} else if ("N".equals(msg.getStatus())) {// 未读
					holder.repokBtn.setEnabled(true);
					holder.refuseBtn.setEnabled(true);

					holder.repokBtn
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									if ("N".equals(msg.getStatus())) {

										Builder builder = new Builder(
												UserMessageInBoxActivity.this);
										builder.setTitle("确认");
										builder.setMessage("确定参加此会议么？");
										builder.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

														messId = String.valueOf(msg
																.getId());
														messResult = "TY";
														recordMess();
														Statistics.sendEvent("meeting_notice", "commitJoin", "YES",( long )0);
													}
												});
										builder.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

														dialog.cancel();
														
													}
												});
										builder.show();

									} else {
										Toast.makeText(
												UserMessageInBoxActivity.this,
												"此通知已回复", Toast.LENGTH_SHORT)
												.show();
									}

								}
							});

					holder.refuseBtn
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if ("N".equals(msg.getStatus())) {
										Builder builder = new Builder(
												UserMessageInBoxActivity.this);
										builder.setTitle("确认");
										builder.setMessage("确定不参加此会议么？");
										builder.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

														messId = String.valueOf(msg
																.getId());
														messResult = "JJ";
														recordMess();
														Statistics.sendEvent("meeting_notice", "commitJoin", "NO",( long )0);
													}
												});
										builder.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

														dialog.cancel();

													}
												});
										builder.show();

									} else {
										Toast.makeText(
												UserMessageInBoxActivity.this,
												"此通知已回复", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				} else {
					holder.repokBtn.setVisibility(View.GONE);
					holder.refuseBtn.setVisibility(View.GONE);
				}

			} else { // 指令与消息 需要回复是否已读
				holder.repokBtn.setVisibility(View.GONE);
				holder.refuseBtn.setVisibility(View.GONE);
				holder.repread.setVisibility(View.VISIBLE);
				if ("ZL".equals(msg.getSmstype())) {
					holder.title.setText(dateStr );
					holder.btn_see_reply.setVisibility(View.VISIBLE);
					holder.repread.setVisibility(View.GONE);

					if ("S".equals(msg.getStatus())) {
						holder.btn_see_reply.setVisibility(View.VISIBLE);
						holder.repread.setVisibility(View.GONE);

					} else {
						holder.btn_see_reply.setVisibility(View.GONE);
						holder.repread.setVisibility(View.VISIBLE);

						if ("N".equals(msg.getStatus())) {
							holder.repread.setText("确认已读");
							holder.repread.setEnabled(true);
							holder.repread
									.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											if ("N".equals(msg.getStatus())) {

												messId = String.valueOf(msg
														.getId());
												messResult = "TY";
												recordMess();
												Statistics.sendEvent("superior_command", "commitRead", "YES",( long )0);
											} else {
												Toast.makeText(
														UserMessageInBoxActivity.this,
														"此消息已回复",
														Toast.LENGTH_SHORT)
														.show();
											}

										}
									});
						} else {
							holder.repread.setText("已读");
							holder.repread.setEnabled(false);
						}

					}

					final String smsTotId = (String) holder.smsTotalId
							.getText();
					holder.btn_see_reply
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									//parentView = v;
								//	showReply(smsTotId);

								}
							});

				} else {
					holder.title.setText(dateStr);
					holder.btn_see_reply.setVisibility(View.GONE);
					holder.repread.setVisibility(View.GONE);
					if ("N".equals(msg.getStatus())) {
						holder.source.setTextColor(Color.BLACK);
						//holder.source.setTextSize(R.dimen.text_medium);
					}
				}

			}
			holder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MsgAdapter.this.setExpand(msg);
					Statistics.sendEvent("message", "unfold", "",( long )0);

				}
			});
			holder.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					new AlertDialog.Builder(UserMessageInBoxActivity.this)
							.setTitle("确认删除？")
							.setPositiveButton("删除",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											UserMessageInBoxActivity.this.showProgressDialog("正在删除短信");
											mTaskDelete = SoapService.getDeleteSendMessage(String.valueOf(msg.getId()));
											mTaskDelete.setListener(UserMessageInBoxActivity.this);
											mTaskDelete.start();
											Statistics.sendEvent("message", "message_delete", "Y",( long )0);

										}

									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}

									}).show();

					return true;
				}
			});
			return holder;

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
		if(arg0 == mTaskDelete){
			if (("s").equalsIgnoreCase(arg0.getResult().toString())){
			init();
			}
			if(dialog != null){
				dialog.dismiss();
			}
		}else if(arg0 == mTaskRelay){
			if (("true").equals(arg0.getResult().toString())){
				this.init();
				
			}
		}else if (arg0 == mTaskMessageList){
			mMessageList = RespFactory.getService().fromResp(MessageInfo.class,
				arg0.getResult());
			this.reRoladList();
			this.dismissDialog();
		}
	}

	private void reRoladList() {
		msgAdpter = new MsgAdapter(UserMessageInBoxActivity.this);
		if (msgAdpter != null) {
			setListAdapter(msgAdpter);
		}
//		txtEmpty.setText(R.string.messagelist_no_message);
//		txtEmpty.setVisibility(View.VISIBLE);
		//imgLoading.setVisibility(View.GONE);
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
