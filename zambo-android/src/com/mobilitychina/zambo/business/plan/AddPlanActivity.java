package com.mobilitychina.zambo.business.plan;

import java.util.List;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.business.customer.CustomerFragment;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.service.HttpPostService;
import com.mobilitychina.zambo.util.ConfigDefinition;

/**
 * 添加临时或正常计划， 用type来标识
 * 
 * @author chenwang
 * 
 */
public class AddPlanActivity extends BaseDetailActivity implements ITaskListener {
	public static final String AddPlanTypeNormal = "F"; // 正常计划
	public static final String AddPlanTypeTemp = "T"; // 临时计划

	private String type;
	private String planDate;
	
	private CustomerFragment contentFragment;

	private HttpPostTask addPlanTask;
	
	private BaseTitlebar baseTitleBar;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_addplan);

		contentFragment = (CustomerFragment) this.getFragmentManager().findFragmentById(R.id.content);
		contentFragment.hideTitlebar();
		contentFragment.setMultiSelect(true);

		type = this.getIntent().getExtras().getString("type", AddPlanTypeTemp);
		planDate = this.getIntent().getExtras().getString("planDate");
		if (type.equalsIgnoreCase(AddPlanTypeNormal)) {
			this.setTitle("添加正常计划");
		} else {
			this.setTitle("添加临时计划");
		}
		this.getTitlebar().setRightButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<CustomerInfo> customerInfoList = contentFragment.getSelectedCustomerList();
				if (customerInfoList.size() == 0) {
					showDialog("提示", "您还未选择一个客户", null);
					return;
				}
				showProgressDialog("正在处理...");
				StringBuffer sb = new StringBuffer();
				for (CustomerInfo customerInfo : customerInfoList) {
					sb.append(customerInfo.getId());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				String custIds = sb.toString();
				addPlanTask = new HttpPostTask(AddPlanActivity.this);
				addPlanTask.setUrl(HttpPostService.SOAP_URL + "create_visit_plan");
				addPlanTask.getTaskArgs().put("emp_id", "3");
				addPlanTask.getTaskArgs().put("cust_id", custIds);
				addPlanTask.getTaskArgs().put("plan_status", "A");
				addPlanTask.getTaskArgs().put("plan_type", type);
				addPlanTask.getTaskArgs().put("plan_visit_date", planDate);
				addPlanTask.setListener(AddPlanActivity.this);
				addPlanTask.start();
			}
		});
	}

	
	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		addPlanTask = null;
		this.showDialog("提示", "创建计划失败，请稍后重试", null);
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		NetObject result = ((HttpPostTask)task).getResult();
		String code = result.stringForKey("code");
//		String message = result.stringForKey("message").replaceAll("[^\u4E00-\u9FA5]", "");
		String message = result.stringForKey("message");
		Log.i("HttpPostTask", "添加计划"+result.toString());
		if (code.equalsIgnoreCase("0")) { //添加成功
			this.showDialog("提示", "创建计划成功", new DialogInterface.OnClickListener() { //点击确定后返回前一个页面
				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<CustomerInfo> customerInfoList = contentFragment.getSelectedCustomerList();
					for (CustomerInfo customerInfo : customerInfoList) {
						customerInfo.setNextPlanVisitDate(planDate);
					}
					Intent intent = new Intent();
					intent.setAction(ConfigDefinition.INTENT_ACTION_ADDPLANCOMPLETE);
					sendBroadcast(intent);
					finish();
				}
			});
		} else {
//			if(message==null)
//				message="操作失败";
			this.showDialog("提示", message, null);
		}
		addPlanTask = null;
	}

	@Override
	public void onTaskUpdateProgress(Task task, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDestroy()
	{
		if(addPlanTask != null){
			addPlanTask.setListener(null);
		}
		super.onDestroy();
		
	}
}
