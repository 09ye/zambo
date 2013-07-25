package com.mobilitychina.zambo.business.kpi;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;
import com.mobilitychina.zambo.business.plan.PlanCenterActivity;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.widget.SectionListAdapter;
import com.mobilitychina.zambo.widget.WeekVisitListItem;
import com.mobilitychina.zambo.widget.WeekVisitTeamItem;

/**
 * 周拜访情况
 * 
 * @author chenwang
 * 
 */
public class WeekVisitStatisticsActivity extends BaseDetailListActivity implements ITaskListener {

	private List<SiemensEmpInfo> teamVisitList; // 团队
	private List<SiemensEmpInfo> personVisitList; // 个人

	private SectionListAdapter mSectionListAdapter;

	private SoapTask task;
	private String posId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_weekvisit_statistics);

		String posName = this.getIntent().getExtras().getString("posName", "本周客户拜访情况");
		this.setTitle(posName);
		this.posId = this.getIntent().getExtras().getString("posId");

		teamVisitList = new ArrayList<SiemensEmpInfo>();
		personVisitList = new ArrayList<SiemensEmpInfo>();

		this.showProgressDialog("正在加载");
		if (task != null) {
			task.cancel(true);
			task = null;
		}
		task = SoapService.getEmpListTask(posId);
		task.setListener(this);
		task.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (task != null) {
			task.cancel(true);
			task = null;
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		task = null;
		this.showDialog("提示", "网络出错，请稍后重试", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		Object result = task.getResult();
		if (result == null) {
			this.showDialog("提示", "网络出错，请稍后重试", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			return;
		}

		List<SiemensEmpInfo> siemensInfoList = RespFactory.getService().fromResp(SiemensEmpInfo.class, result);

		for (SiemensEmpInfo siemensEmpInfo : siemensInfoList) {
			if (siemensEmpInfo.isParent()) {
				teamVisitList.add(siemensEmpInfo);
			}
			personVisitList.add(siemensEmpInfo);
		}
		mSectionListAdapter = new SectionListAdapter(this, true);
		mSectionListAdapter.addSection("团队", new TeamListAdapter());
		mSectionListAdapter.addSection("个人", new WeekVisitAdapter());
		this.setListAdapter(mSectionListAdapter);
		mSectionListAdapter.notifyDataSetChanged();
		task = null;
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		SiemensEmpInfo siemensEmpInfo = (SiemensEmpInfo) mSectionListAdapter.getItem(position);
		if (siemensEmpInfo != null && siemensEmpInfo.isParent()) {
			Intent intent = new Intent(this, WeekVisitStatisticsActivity.class);
			intent.putExtra("posId", siemensEmpInfo.getId());
			intent.putExtra("posName", "团队" + siemensEmpInfo.getEmpName());
			this.startActivity(intent);
			sendEvent("weekvisit", "employee", "",0);		
		}
	}

	/**
	 * 团队列表
	 * 
	 * @author chenwang
	 * 
	 */
	class TeamListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return teamVisitList.size();
		}

		@Override
		public SiemensEmpInfo getItem(int position) {
			return teamVisitList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WeekVisitTeamItem view;
			if (convertView == null) {
				view = (WeekVisitTeamItem) LayoutInflater.from(WeekVisitStatisticsActivity.this).inflate(
						R.layout.item_team_list, null);
			} else {
				view = (WeekVisitTeamItem) convertView;
			}
			SiemensEmpInfo empInfo = this.getItem(position);
			view.show(empInfo);
			return view;
		}

	}

	/**
	 * 周拜访率
	 * 
	 * @author chenwang
	 * 
	 */
	class WeekVisitAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return personVisitList.size();
		}

		@Override
		public SiemensEmpInfo getItem(int position) {
			return personVisitList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WeekVisitListItem view;
			if (convertView == null) {
				view = (WeekVisitListItem) LayoutInflater.from(WeekVisitStatisticsActivity.this).inflate(
						R.layout.item_weekvisit_list, null);
			} else {
				view = (WeekVisitListItem) convertView;
			}
			SiemensEmpInfo empInfo = this.getItem(position);
			view.show(empInfo);
			return view;
		}

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}

	
}
