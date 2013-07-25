package com.mobilitychina.zambo.business.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.customer.ReportActivity;
import com.mobilitychina.zambo.business.customer.ShareActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.widget.CustomImageButton;
import com.mobilitychina.zambo.widget.ProjectRecordListItem;
import com.mobilitychina.zambo.widget.SectionListAdapter;

/**
 * 项目跟进记录
 * 
 * @author chenwang
 * 
 */
public class ProjectRecordListActivity extends BaseDetailListActivity implements ITaskListener {
	private ProjectInfo projectInfo;
	private SoapTask projectDetailTask;
	private String customerId;
	private SectionListAdapter mSectionListAdapter;

	private int REQUEST_CODE_UPDATEPROJECT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_recordlist);

		projectInfo = (ProjectInfo) this.getIntent().getExtras().get("projectInfo");

		if (projectInfo == null) {
			Toast.makeText(this, "项目信息未初始化", Toast.LENGTH_LONG);
			finish();
			return;
		}
		customerId = (String) this.getIntent().getExtras().getString("customerId");

		final CustomerInfo custInfo = CustomerInfoManager.getInstance().getCustomerById(customerId);
		if (custInfo == null) {
			this.setTitle("客户信息");
		} else {
			this.setTitle(custInfo.getCustName());
		}
		if (custInfo != null) {
			ImageView custTypeImageView = new ImageView(this);
			if (custInfo.getCustType().equalsIgnoreCase("C1")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c1);
			} else if (custInfo.getCustType().equalsIgnoreCase("C2")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c2);
			} else if (custInfo.getCustType().equalsIgnoreCase("C3")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c3);
			} else if (custInfo.getCustType().equalsIgnoreCase("C4")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c4);
			} else if (custInfo.getCustType().equalsIgnoreCase("S")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_s);
			}
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			custTypeImageView.setPadding(0, -3, 15, 0);
			custTypeImageView.setLayoutParams(layoutParams);
			this.getTitlebar().setRightCustomeView(custTypeImageView);
		}
		((TextView) this.findViewById(R.id.tvNumber)).setText(projectInfo.getProNumber());
		((TextView) this.findViewById(R.id.tvName)).setText(projectInfo.getName());

		((RelativeLayout) this.findViewById(R.id.layoutRecord)).setVisibility(View.INVISIBLE);

		CustomImageButton btnReport = (CustomImageButton) this.findViewById(R.id.btnReport);
		btnReport.setImageResource(R.drawable.icon_report);
		btnReport.setText("汇报");
		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProjectRecordListActivity.this, ReportActivity.class);
				intent.putExtra("custDetailId", String.valueOf(custInfo.getCustDetailId()));
				intent.putExtra("custName", String.valueOf(custInfo.getCustName()));
				intent.putExtra("content", "@" + projectInfo.getName());
				startActivity(intent);
				
				sendEvent("project_detail", "project_report", "", 0);
				//System.out.println("项目汇报-----》》》》");
				
			}
		});

		CustomImageButton btnShare = (CustomImageButton) this.findViewById(R.id.btnShare);
		btnShare.setImageResource(R.drawable.icon_share);
		btnShare.setText("分享");
		btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProjectRecordListActivity.this, ShareActivity.class);
				intent.putExtra("custDetailId", String.valueOf(custInfo.getCustDetailId()));
				intent.putExtra("custName", String.valueOf(custInfo.getCustName()));
				intent.putExtra("content", "@" + projectInfo.getName());
				startActivity(intent);
				
				sendEvent("project_detail", "project_sharing", "", 0);
				//System.out.println("项目分享-----》》》》");
				
			}
		});

		CustomImageButton btnUpdate = (CustomImageButton) this.findViewById(R.id.btnUpdate);
		btnUpdate.setImageResource(R.drawable.icon_update);
		btnUpdate.setText("更新");
		if(this.getIntent().getExtras().getBoolean("updateable") == false){
			btnUpdate.setVisibility(View.GONE);
		}
		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (projectInfo != null) {
					Intent intent = new Intent(ProjectRecordListActivity.this, ProjectDetailActivity.class);
					intent.putExtra("detailType", ProjectDetailActivity.DETAIL_TYPE_UPDATE);
					intent.putExtra("customerId", ProjectRecordListActivity.this.customerId);
					intent.putExtra("projectInfo", projectInfo);
					ProjectRecordListActivity.this.startActivityForResult(intent, REQUEST_CODE_UPDATEPROJECT);
					
					sendEvent("project_detail", "projectProgress_itemclick", "", 0);
					//system.out.println("projectProgress_itemclick-----》》》》");
					McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PROJECT,"更新项目;"+projectInfo.getId());
				}
			}
		});

		this.showProgressDialog("正在加载...");
		projectDetailTask = SoapService.getSiemensProjectDetailTask(customerId, projectInfo.getProNumber());
		projectDetailTask.setListener(this);
		projectDetailTask.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_UPDATEPROJECT) {
			this.showProgressDialog("正在加载...");
			projectDetailTask = SoapService.getSiemensProjectDetailTask(customerId, projectInfo.getProNumber());
			projectDetailTask.setListener(this);
			projectDetailTask.start();
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		if (projectDetailTask == task) {
			projectDetailTask = null;
			this.showDialog("提示", "网络错误，请稍后重试", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		if (projectDetailTask == task) {
			projectDetailTask = null;
			SoapObject result = (SoapObject) task.getResult();
			if (result == null) {
				this.showDialog("提示", "网络错误，请稍后重试", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				return;
			}
			int n = result.getPropertyCount();
			Map<String, List<ProjectInfo>> projectRecordMap = new HashMap<String, List<ProjectInfo>>();

			for (int i = 0; i < n; i++) {
				ProjectInfo project = new ProjectInfo();
				String element = result.getProperty(i).toString();
				Log.i("ProjectRecordList",element);
				project.setAddMenuDate(CommonUtil.getStringElement(element, "addmenu_date"));
				project.setKzrAll(CommonUtil.getStringElement(element, "kzr_all"));
				project.setProjectedBudget(CommonUtil.getStringElement(element, "project_budget"));
				project.setShareId(CommonUtil.getStringElement(element, "share_id"));
				project.setName(projectInfo.getName());
				project.setId(projectInfo.getId());
				project.setCustomerId(CommonUtil.getStringElement(element, "custId"));
				project.setProNumber(CommonUtil.getStringElement(element, "proNumber"));
				project.setVisitDate(CommonUtil.getStringElement(element, "visitDate"));
				project.setStatus(CommonUtil.getStringElement(element, "proStatus"));
				project.setStatusId(CommonUtil.getStringElement(element, "proStatusId"));
				project.setProType(CommonUtil.getStringElement(element, "proType"));
				project.setContactName(CommonUtil.getStringElement(element, "contactName"));
				project.setContactPhone(CommonUtil.getStringElement(element, "contactPhone"));
				project.setRemark(CommonUtil.getStringElement(element, "remark"));
				project.setOpptyid(CommonUtil.getStringElement(element, "opptyid"));
				project.setProBigType(CommonUtil.getStringElement(element, "proBigType"));

				String year = CommonUtil.formatYear(project.getVisitDate());
				if (year == null) {
					continue;
				}

				List<ProjectInfo> projectInfoList = projectRecordMap.get(year);
				if (projectInfoList == null) {
					projectInfoList = new ArrayList<ProjectInfo>();
					projectRecordMap.put(year, projectInfoList);
				}
				projectInfoList.add(project);

				if (i == 0) { // 保存最新一条项目记录
					this.projectInfo.setAddMenuDate(project.getAddMenuDate());
					this.projectInfo.setKzrAll(project.getKzrAll());
					this.projectInfo.setProjectedBudget(project.getProjectBudget());
					this.projectInfo.setCustomerId(project.getCustomerId());
					this.projectInfo.setProNumber(project.getProNumber());
					this.projectInfo.setVisitDate(project.getVisitDate());
					this.projectInfo.setStatus(project.getStatus());
					this.projectInfo.setStatusId(project.getStatusId());
					this.projectInfo.setProType(project.getProType());
					this.projectInfo.setContactName(project.getContactName());
					this.projectInfo.setContactPhone(project.getContactPhone());
					this.projectInfo.setRemark(project.getRemark());
					this.projectInfo.setOpptyid(project.getOpptyid());
					this.projectInfo.setProBigType(project.getProBigType());
					this.projectInfo.setShareId(project.getShareId());
				}
			}
			if (projectRecordMap.size() > 0) {
				((RelativeLayout) this.findViewById(R.id.layoutRecord)).setVisibility(View.VISIBLE);
				// 按年分组显示
				mSectionListAdapter = new SectionListAdapter(this, R.layout.timeline_list_header);
				Iterator<String> it = projectRecordMap.keySet().iterator();
				while (it.hasNext()) {
					String year = it.next();
					List<ProjectInfo> projectInfoList = projectRecordMap.get(year);
					ProjectRecordListAdapter listAdapter = new ProjectRecordListAdapter(projectInfoList);
					mSectionListAdapter.addSection(year, listAdapter);
				}
				this.setListAdapter(mSectionListAdapter);
			} else {
				((RelativeLayout) this.findViewById(R.id.layoutRecord)).setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ProjectInfo projectInfo = (ProjectInfo) mSectionListAdapter.getItem(position);
		if (projectInfo != null) {
			Intent intent = new Intent(this, ProjectDetailActivity.class);
			intent.putExtra("detailType", ProjectDetailActivity.DETAIL_TYPE_SELECT);
			intent.putExtra("customerId", this.customerId);
			intent.putExtra("projectInfo", projectInfo);
			this.startActivity(intent);
			
			sendEvent("project_detail", "projectProgress_itemclick", "", 0);
			//System.out.println("点击项目进度详情-----》》》》");
		}
	}

	class ProjectRecordListAdapter extends BaseAdapter {
		private List<ProjectInfo> projectInfos;

		public ProjectRecordListAdapter(List<ProjectInfo> projectInfos) {
			this.projectInfos = projectInfos;
		}

		@Override
		public int getCount() {
			if (projectInfos == null || projectInfos.size() == 0) {
				return 0;
			} else {
				return projectInfos.size();
			}
		}

		@Override
		public ProjectInfo getItem(int position) {
			if (projectInfos == null || projectInfos.size() == 0) {
				return null;
			} else {
				return projectInfos.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProjectRecordListItem view;
			if (convertView == null) {
				view = (ProjectRecordListItem) LayoutInflater.from(ProjectRecordListActivity.this).inflate(
						R.layout.item_projectrecord_list, null);
			} else {
				view = (ProjectRecordListItem) convertView;
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
