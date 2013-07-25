package com.mobilitychina.zambo.business.more;

import android.os.Bundle;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.util.CommonUtil;

/**
 * 
 * @author wchen
 * 
 */
public class MyInfoActivity extends BaseDetailActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_myinfo);
		this.setTitle("我的信息");

		((TextView) findViewById(R.id.tvUserName)).setText("用户名：" + UserInfoManager.getInstance().getName());
		((TextView) findViewById(R.id.tvIMEI)).setText("设备号：" + CommonUtil.getIMEI(this));
	}

}
