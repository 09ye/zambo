package com.mobilitychina.zambo.business.plan;

import android.os.Bundle;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;


public class PlanCenterActivity extends BaseDetailActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_plan_center);

		this.setTitle(this.getIntent().getExtras().getString(PlanCenterFragment.INTENT_EMP_NAME, "TA") + "的计划");
	}
}
