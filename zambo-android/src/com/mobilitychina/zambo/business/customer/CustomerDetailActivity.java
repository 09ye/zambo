package com.mobilitychina.zambo.business.customer;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.record.FollowupListFragment;
import com.mobilitychina.zambo.business.record.ProjectListFragment;
import com.mobilitychina.zambo.service.CustomerInfoManager;
// 计划I
public class CustomerDetailActivity extends BaseDetailActivity implements CustomerDetailFragment.OnSelectedListener{
	private CustomerInfo custInfo;
	private String customerId;
	private String planId;
	private ProjectListFragment projectRecordListFragment;
	private FollowupListFragment followupListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_customer_detail);
		customerId = this.getIntent().getExtras().getString("customerId");
		planId = this.getIntent().getExtras().getString("planId");
		custInfo = CustomerInfoManager.getInstance().getCustomerById(customerId);
		if (custInfo == null) {
			this.setTitle("客户信息");
		} else {
			this.setTitle(custInfo.getCustName());
			//System.out.println("医院名称------》》"+custInfo.getCustName());
		}
		projectRecordListFragment = (ProjectListFragment) getFragmentManager().findFragmentById(R.id.fragmentProjectRecordList);
		if(projectRecordListFragment==null){
			projectRecordListFragment = new ProjectListFragment();
			getFragmentManager().beginTransaction().add(R.id.fragmentProjectRecordList, projectRecordListFragment).commit();
		}
		projectRecordListFragment.setCustomerId(customerId);
		followupListFragment = (FollowupListFragment) getFragmentManager().findFragmentById(
				R.id.fragmentFollowupList);
		if(followupListFragment==null){
			followupListFragment = new FollowupListFragment();
			getFragmentManager().beginTransaction().add(R.id.fragmentFollowupList, followupListFragment).commit();
		}
		followupListFragment.setCustomerId(customerId);
		if (planId == null|| planId.length() == 0){
			projectRecordListFragment.setUpdateable(false);
		}else{
			projectRecordListFragment.setUpdateable(true);
		}
	}

	@Override
	public void onSelected(int i) {
		// TODO Auto-generated method stub
		switch(i){
		case 1:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.show(followupListFragment);
			ft.hide(projectRecordListFragment);
			ft.commit();
			followupListFragment.refresh();
			sendEvent("customer_detail", "customer_followupRecord", "", 0);
			break;
		case 2:
			FragmentTransaction ft2 = getFragmentManager().beginTransaction();
			ft2.hide(followupListFragment);
			ft2.show(projectRecordListFragment);
			ft2.commit();
			projectRecordListFragment.refresh();
			sendEvent("customer_detail", "project_list", "", 0);
			break;
		case 3:
			projectRecordListFragment.refresh();
			break;
		}
		
	}
	
}
