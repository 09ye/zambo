package com.mobilitychina.zambo.business.customer;

import android.os.Bundle;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.service.CustomerInfoManager;
// 计划I
public class CustomerDetailActivity extends BaseDetailActivity {
	private CustomerInfo custInfo;
	private String customerId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_customer_detail);
		customerId = this.getIntent().getExtras().getString("customerId");
		custInfo = CustomerInfoManager.getInstance().getCustomerById(customerId);
		if (custInfo == null) {
			this.setTitle("客户信息");
		} else {
			this.setTitle(custInfo.getCustName());
			//System.out.println("医院名称------》》"+custInfo.getCustName());
		}
	}
	
}
