package com.mobilitychina.zambo.business.more;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.business.kpi.SendMonthReportActivity;
import com.mobilitychina.zambo.business.kpi.TotalVisitRateActivity;
import com.mobilitychina.zambo.business.kpi.WeekVisitStatisticsActivity;
import com.mobilitychina.zambo.home.LoginActivity;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.SectionListAdapter;

public class MoreFragment extends ListFragment {
	private final String WeekVisitTag = "周拜访完成情况";
	private final String YearTotalVisitTag = "年度累计客户拜访情况";
	private final String SendMonthReportTag = "发送月报";
	private final String MyInfoTag = "我的信息";
	private final String ModifyPasswordTag = "修改登录密码";
	private final String VersionInfoTag = "版本信息";
	private final String HelpTag = "帮助";

	private List<NameValuePair> headers;
	private SectionListAdapter mSectionListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		((BaseTitlebar) view.findViewById(R.id.title_bar)).setTitle("更多功能");

		((Button) view.findViewById(R.id.btnLogout)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setMessage("确定退出登录吗?");
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Statistics.sendEvent("plan", "quit", "", (long) 0); 
						NotificationManager myNotiManager = (NotificationManager) MoreFragment.this.getActivity().getSystemService(MoreFragment.this.getActivity().NOTIFICATION_SERVICE);
						myNotiManager.cancelAll();
						android.os.Process.killProcess(android.os.Process.myPid());
						//这句话是根据程序在系统进程内的ID进行强制结束当前程序进程，用这个就可以完全退出整个程序
					}
				});

				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
				builder.create().show();

			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		headers = new ArrayList<NameValuePair>();
		headers.add(new BasicNameValuePair("我的绩效", "" + R.drawable.icon_more_kpi));
		headers.add(new BasicNameValuePair("设置与帮助", "" + R.drawable.icon_more_setting));

		mSectionListAdapter = new SectionListAdapter(getActivity(), new HeaderAdapter());
		List<String> datas = new ArrayList<String>();
		datas.add(WeekVisitTag);
		datas.add(YearTotalVisitTag);
		datas.add(SendMonthReportTag);
		mSectionListAdapter.addSection("我的绩效", new MoreListAdapter(datas));
		datas = new ArrayList<String>();
		datas.add(MyInfoTag);
		datas.add(ModifyPasswordTag);
		datas.add(VersionInfoTag);
		mSectionListAdapter.addSection("设置与帮助", new MoreListAdapter(datas));

		this.setListAdapter(mSectionListAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		String tag = (String) mSectionListAdapter.getItem(position);
		if (tag.equalsIgnoreCase(WeekVisitTag)) {
			Intent intent = new Intent(this.getActivity(), WeekVisitStatisticsActivity.class);
			intent.putExtra("posId", "-1");
			this.startActivity(intent);
			 Statistics.sendEvent("plan", "weekvisit", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(YearTotalVisitTag)) {
			Intent intent = new Intent(this.getActivity(), TotalVisitRateActivity.class);
			startActivity(intent);
			 Statistics.sendEvent("plan", "yearvisit", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(SendMonthReportTag)) {
			Intent intent = new Intent(this.getActivity(), SendMonthReportActivity.class);
			startActivity(intent);
			 Statistics.sendEvent("plan", "monthreport", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(MyInfoTag)) {
			Intent intent = new Intent(this.getActivity(), MyInfoActivity.class);
			startActivity(intent);
			 Statistics.sendEvent("plan", "myinfo", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(ModifyPasswordTag)) {
			Intent intent = new Intent(this.getActivity(), ModifyPasswordActivity.class);
			startActivity(intent);
			Statistics.sendEvent("plan", "passwordchange", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(VersionInfoTag)) {
			Intent intent = new Intent(this.getActivity(), VersionActivity.class);
			this.startActivity(intent);
			 Statistics.sendEvent("plan", "version", "", (long) 0); 
		} else if (tag.equalsIgnoreCase(HelpTag)) {

		}
	}

	/**
	 * 段的标题
	 * 
	 * @author chenwang
	 * 
	 */
	class HeaderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return headers.size();
		}

		@Override
		public NameValuePair getItem(int position) {
			// TODO Auto-generated method stub
			return headers.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(MoreFragment.this.getActivity()).inflate(R.layout.item_list_header, null);
			} else {
				view = convertView;
			}
			NameValuePair data = this.getItem(position);
			((TextView) view.findViewById(R.id.tvTitle)).setText(data.getName());
			((ImageView) view.findViewById(R.id.ivIcon)).setImageResource(Integer.parseInt(data.getValue()));

			return view;
		}
	}

	/**
	 * 列表项
	 * 
	 * @author chenwang
	 * 
	 */
	class MoreListAdapter extends BaseAdapter {
		private List<String> datas;

		public MoreListAdapter(List<String> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.datas.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return this.datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(MoreFragment.this.getActivity()).inflate(R.layout.item_more_list, null);
			} else {
				view = convertView;
			}
			((TextView) view.findViewById(R.id.tvTitle)).setText(this.getItem(position));
			return view;
		}

	}
}
