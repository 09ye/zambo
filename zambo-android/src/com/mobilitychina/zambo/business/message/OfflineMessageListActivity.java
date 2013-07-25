package com.mobilitychina.zambo.business.message;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilitychina.localcontent.ContentManager.Content;
import com.mobilitychina.localcontent.ContentManager.UploadStatusListener;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.message.data.OfflineMessageType;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager.CheckInMsg;
import com.mobilitychina.zambo.service.CustomerInfoManager;

public class OfflineMessageListActivity extends BaseDetailListActivity implements UploadStatusListener {
	
	private MsgAdapter msgAdapter;
	private String msgType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_offline_message_list);
		msgType = this.getIntent().getStringExtra("type");
		if (OfflineMessageType.CHECKIN.equalsIgnoreCase(msgType)) {
			this.setTitle("我的签到");
			CheckInOfflineManager.instance().registerListener(this);
			msgAdapter = new MsgAdapter(CheckInOfflineManager.instance().getContents());
			setListAdapter(msgAdapter);
		} else {
			this.setTitle("消息队列");
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(msgAdapter != null){
			if (OfflineMessageType.CHECKIN.equalsIgnoreCase(msgType)) {
				CheckInMsg msg = (CheckInMsg) msgAdapter.getItem(position);
				if(msg.status() != Content.STATUS_UPLOADED){
					CheckInOfflineManager.instance().addContent(msg);
				}
			}
		}
	}
	
	@Override
	public void onUploadProgress(int itemId, int progress) {

	}

	@Override
	public void onUploadReload() {
		if(msgAdapter != null){
			msgAdapter.notifyDataSetChanged();
		}
	}

	class MsgAdapter extends BaseAdapter {

		private Content[] offlineMsgList;

		public MsgAdapter(Content[] messages) {
			super();
			offlineMsgList = messages;
		}

		@Override
		public int getCount() {
			if (offlineMsgList != null) {
				return offlineMsgList.length;
			}
			return 0;
		}

		@Override
		public Content getItem(int position) {
			return offlineMsgList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.offline_message_item, parent, false);
				viewHolder.sendTimeTextView = (TextView) convertView.findViewById(R.id.send_time);
				viewHolder.sendStatusTextView = (TextView) convertView.findViewById(R.id.send_status);
				viewHolder.sendToTextView = (TextView) convertView.findViewById(R.id.send_to);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.reset();
			Content content = getItem(position);
			if(content instanceof CheckInMsg){
				CheckInMsg msg = (CheckInMsg) getItem(position);
				viewHolder.sendTimeTextView.setText("发送于"+formateDateTime(msg.datetime()));
				viewHolder.sendToTextView.setText(CustomerInfoManager.getInstance().getCustomerById(msg.custId()).getCustName());
			}
			
			if(content.status() == Content.STATUS_FAILED){
				viewHolder.sendStatusTextView.setText("发送失败");
				viewHolder.sendStatusTextView.setTextColor(getResources().getColor(R.color.red));
			}else if(content.status() == Content.STATUS_UPLOADED){
				viewHolder.sendStatusTextView.setText("发送成功");
				viewHolder.sendStatusTextView.setTextColor(getResources().getColor(R.color.grey));
			}
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView sendTimeTextView;
		public TextView sendStatusTextView;
		public TextView sendToTextView;

		public void reset() {
			sendTimeTextView.setText("");
			sendStatusTextView.setText("");
			sendToTextView.setText("");
		}
	}

	private final SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
	private final SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	private String formateDateTime(String datetime){
		try{
			return df.format(df1.parse(datetime));
		}catch(Exception e){
			
		}
		return datetime;
	}
}
