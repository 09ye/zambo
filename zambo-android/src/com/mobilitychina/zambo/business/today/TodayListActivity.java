package com.mobilitychina.zambo.business.today;

import android.os.Bundle;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseListActivity;

public class TodayListActivity extends BaseListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_todaylist);
		this.setTitle("今日任务");
	}
}
