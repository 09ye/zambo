package com.mobilitychina.zambo.business.kpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;

public class SendMonthReportActivity extends BaseDetailListActivity implements ITaskListener {
	private Button btnSelectYear;
	private MonthListAdpater mMonthListAdapter;

	private int selectedYear;
	private int selectedMonth;

	private SoapTask sendTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monthreport);
		this.setTitle("发送月报");
		this.getTitlebar().setShowSeperator(false);
		this.getTitlebar().setRightButton("发送", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedMonth < 1) {
					Builder builder = new Builder(SendMonthReportActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请先选择月份");
					builder.setPositiveButton("确定", null);
					builder.show();
					return;
				} else {
					CommonUtil.showConfirmDialg(SendMonthReportActivity.this, "提示", "确定发送" + selectedYear + "年"
							+ selectedMonth + "月的月报吗？", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SendMonthReportActivity.this.showProgressDialog("正在处理...");
							sendTask = SoapService.getVistPlanMailRequestTask(selectedYear + "-" + selectedMonth);
							sendTask.setListener(SendMonthReportActivity.this);
							sendTask.start();
							
							sendEvent("monthreport", "sned", "", 0);
							
//							System.out.println("发送成功------>>>>");


						}
					}, null);
				}

			}
		});

		btnSelectYear = (Button) this.findViewById(R.id.btnSelectYear);
		btnSelectYear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				final String[] years = new String[10];
				for (int i = 0; i < years.length; i++) {
					years[i] = String.valueOf(currentYear - i);
				}

				new AlertDialog.Builder(SendMonthReportActivity.this).setTitle("选择年")
						.setItems(years, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								btnSelectYear.setText(years[which] + "年");
								refreshMonth(Integer.parseInt(years[which]));
							}
						}).create().show();
				
				sendEvent("monthreport", "yearchanged", "", 0);

//				System.out.println("年份改变------>>>>");

				
			}
		});

		this.selectedYear = Calendar.getInstance().get(Calendar.YEAR);
		btnSelectYear.setText(selectedYear + "年");
		this.selectedMonth = -1;
		mMonthListAdapter = new MonthListAdpater();
		this.setListAdapter(mMonthListAdapter);
		refreshMonth(this.selectedYear);
	}

	private void refreshMonth(int selectedYear) {
		this.selectedYear = selectedYear;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (currentYear == selectedYear) {
			mMonthListAdapter.setLimitMonth(currentMonth);
		} else {
			mMonthListAdapter.setLimitMonth(12);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		this.selectedMonth = Integer.parseInt(mMonthListAdapter.getItem(position));
		mMonthListAdapter.setSelectedIndex(position);
		
		sendEvent("monthreport", "monthchanged", "", 0);
//		System.out.println("月份改变------>>>>");

	}

	class MonthListAdpater extends BaseAdapter {
		private List<String> mMonthList;
		private int selectedIndex;

		public MonthListAdpater() {
			mMonthList = new ArrayList<String>();
		}

		public void setLimitMonth(int limitMonth) {
			mMonthList.clear();
			selectedMonth = -1;
			selectedIndex = -1;
			for (int i = 1; i <= limitMonth; i++) {
				mMonthList.add(String.valueOf(i));
			}
			this.notifyDataSetChanged();
		}

		public void setSelectedIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mMonthList.size();
		}

		@Override
		public String getItem(int position) {
			return mMonthList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				SendMonthReportActivity.this.getLayoutInflater();
				view = LayoutInflater.from(SendMonthReportActivity.this).inflate(R.layout.item_month, null);
			} else {
				view = convertView;
			}
			TextView tvMonth = (TextView) view.findViewById(R.id.tvMonth);
			ImageView ivSelected = (ImageView) view.findViewById(R.id.ivSelected);
			if (selectedIndex == position) {
				ivSelected.setVisibility(View.VISIBLE);
			} else {
				ivSelected.setVisibility(View.GONE);
			}
			tvMonth.setText(mMonthList.get(position) + " 月");
			return view;
		}

	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		this.showDialog("提示", "网络错误，请稍后重试", null);
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		String result = task.getResult().toString();
		if (result.equalsIgnoreCase("T")) {
			this.showDialog("提示", "发送成功", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		} else {
			this.showDialog("提示", "发送失败，请稍后重试", null);
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
