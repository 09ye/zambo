package com.mobilitychina.zambo.business.customer;

import android.os.Bundle;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseListActivity;

/**
 * 客户列表
 * 
 * @author chenwang
 * 
 */
public class CustomerListActivity extends BaseListActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_customerlist);
	}

}
