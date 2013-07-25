package com.mobilitychina.zambo.business.record;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.ListFragment;
import android.content.Context;
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
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.ProjectListItem;

/**
 * 客户的项目清单
 * 
 * @author chenwang
 * 
 */
public class ProjectListFragment extends ListFragment implements ITaskListener {
	private List<ProjectInfo> mProjectInfoList;
	private String customerId;
	private SoapTask mTask;
	private Adapter mAdapter;
	private boolean isError;
	private boolean mIsupdateable = false;
	public void setUpdateable( boolean b){
		mIsupdateable = b;
	}
	public void setCustomerId(String id) {
		this.customerId = id;
	}

	private void requestRecordList() {
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
		isError = false;
		mTask = SoapService.getProjectRecordListTask(this.customerId);
		mTask.setListener(this);
		mTask.start();
	}

	public void refresh() {
		mProjectInfoList.clear();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		this.requestRecordList();
	}

	@Override
	public void onTaskFailed(Task task) {
		mTask = null;
		isError = true;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		if (task == mTask) {
			SoapObject result = (SoapObject) task.getResult();
			List<ProjectInfo> projectInfoList = this.parse(result);
			if (projectInfoList != null) {
				mProjectInfoList.addAll(projectInfoList);
			}
			mTask = null;
			isError = false;
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private List<ProjectInfo> parse(SoapObject result) {
		if (result == null) {
			return null;
		}
		int n = result.getPropertyCount();
		List<ProjectInfo> projectInfoList = new ArrayList<ProjectInfo>(n);
		for (int i = 0; i < n; i++) {
			ProjectInfo project = new ProjectInfo();
			String element = result.getProperty(i).toString();
			Log.i("ProjectListFragment",element);
			project.setId(CommonUtil.getStringElement(element, "itemId"));
			project.setCustomerId(CommonUtil.getStringElement(element, "custId"));
			project.setName(CommonUtil.getStringElement(element, "itemName"));
			project.setProNumber(CommonUtil.getStringElement(element, "proNumber"));
			project.setVisitDate(CommonUtil.getStringElement(element, "visitDate"));
			project.setStatus(CommonUtil.getStringElement(element, "status"));
			project.setStatusText(CommonUtil.getStringElement(element, "statusText"));
			try {
				project.setStatusValue(Integer.parseInt(CommonUtil.getStringElement(element, "statusValue")));
			} catch (Exception e) {
				project.setStatusValue(0);
			}
			projectInfoList.add(project);
		}
		return projectInfoList;
	}

	@Override
	public void onTaskUpdateProgress(Task task, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
		mProjectInfoList = new ArrayList<ProjectInfo>();
		mAdapter = new Adapter();
		this.setListAdapter(mAdapter);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mProjectInfoList.size() > position) {
			ProjectInfo projectInfo = mProjectInfoList.get(position);
			if (projectInfo != null) {
				Intent intent = new Intent(this.getActivity(), ProjectRecordListActivity.class);
				intent.putExtra("customerId", this.customerId);
				intent.putExtra("projectInfo", projectInfo);
				intent.putExtra("updateable", mIsupdateable);
				this.getActivity().startActivity(intent);
				Statistics.sendEvent("customer_detail" , "project_itemclick","",(long) 0);
				//System.out.println("具体项目点击-------》》");
			}
		}
	}

	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mProjectInfoList.size() == 0) {
				return 1;
			} else {
				return (mProjectInfoList.size());
			}
		}

		@Override
		public ProjectInfo getItem(int position) {
			if (mProjectInfoList.size() == 0) {
				return null;
			} else {
				return mProjectInfoList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mProjectInfoList.size() == 0) {
				Context context = ProjectListFragment.this.getActivity();
				if (isError) {
					return CommonUtil.buildListRetryItemView(context, convertView, "网络错误，请稍后重试", new OnClickListener() {
						@Override
						public void onClick(View v) {
							ProjectListFragment.this.refresh();
						}
					});
				}
				if (mTask == null) {
					return CommonUtil.buildListSimpleMsgItemView(context, convertView, "暂无数据");
				} else {
					return CommonUtil.buildListLoadingItemView(context, convertView, null);
				}
			}
			ProjectListItem view;
			if (convertView == null || !(convertView instanceof ProjectListItem)) {
				view = (ProjectListItem) LayoutInflater.from(ProjectListFragment.this.getActivity()).inflate(
						R.layout.item_project_list, null);
			} else {
				view = (ProjectListItem) convertView;
			}
			view.show(this.getItem(position));
			return view;
		}

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
