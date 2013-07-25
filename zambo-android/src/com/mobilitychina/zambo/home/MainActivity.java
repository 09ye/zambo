package com.mobilitychina.zambo.home;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

import com.mobilitychina.common.component.tab.TabViewActivity;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.customer.CustomerFragment;
import com.mobilitychina.zambo.business.departments.data.DepartmentManager;
import com.mobilitychina.zambo.business.facility.data.FacilityManager;
import com.mobilitychina.zambo.business.jobtitle.data.JobTitleManager;
import com.mobilitychina.zambo.business.message.UserMessageFragment;
import com.mobilitychina.zambo.business.more.MoreFragment;
import com.mobilitychina.zambo.business.notifyteam.data.NotifyTeamManager;
import com.mobilitychina.zambo.business.plan.PlanCenterFragment;
import com.mobilitychina.zambo.business.product.data.ProductStatusManager;
import com.mobilitychina.zambo.business.today.TodayFragment;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.SiemensProductManager;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.service.message.BaseMsgInfo;
import com.mobilitychina.zambo.service.message.MsgDefine;
import com.mobilitychina.zambo.service.message.MsgSenderInfo;
import com.mobilitychina.zambo.service.message.NotificationService;
import com.mobilitychina.zambo.service.message.bus.BusDelegate;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.ConfigHelper;
import com.mobilitychina.zambo.util.EventListener;
import com.mobilitychina.zambo.util.SiemensStatusLogo;
import com.mobilitychina.zambo.util.Version;
import com.mobilitychina.zambo.util.VersionUpdate;

public class MainActivity extends TabViewActivity implements EventListener, BusDelegate {

	/**
	 * 监听Tab变化事件
	 */
//	private BroadcastReceiver mTabChangeReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			int tabIndex = intent.getIntExtra("tabindex", 0);
//			MainActivity.this.mTabHost.setCurrentTab(tabIndex);
//		}
//	};
	
	private final static int MESSAGE_UPDATECHECK = 1;
	private VersionUpdate updateView;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_UPDATECHECK:
				onUpdateCheck();
			}
		}
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					ConfigDefinition.INTENT_ACTION_SERVICE_SETUP)		) {//订阅
				NotificationService.instance().addBusDelegate(MainActivity.this);
				MsgSenderInfo msg = new MsgSenderInfo();
				msg.setClientId(UserInfoManager.getInstance().getPhone());
				msg.setMsgId(MsgDefine.MSG_CONFIG_UPDATE);
				NotificationService.instance().sendMsg(msg);
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CustomerInfoManager.getInstance().start();
		SiemensProductManager.getInstance().start();
		DepartmentManager.getInstance().start();
		FacilityManager.getInstance().start();
		JobTitleManager.getInstance().start();
		NotifyTeamManager.getInstance().start();
		ProductStatusManager.getInstance().start();
		if(ConfigHelper.getInstance().getIsPush()){
			this.startService(new Intent(this,NotificationService.class));
		}else{
			this.stopService(new Intent(this,NotificationService.class));
		}
		this.addTab("客户", R.layout.tab_indicator_customer, CustomerFragment.class, null);
		this.addTab("计划", R.layout.tab_indicator_plan, PlanCenterFragment.class, null);
		this.addTab("拜访", R.layout.tab_indicator_task, TodayFragment.class, null);
		this.addTab("消息", R.layout.tab_indicator_message, UserMessageFragment.class, null);
		this.addTab("更多", R.layout.tab_indicator_more, MoreFragment.class, null);

//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_TABCHANGE);
//		this.registerReceiver(mTabChangeReceiver, intentFilter);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_SERVICE_SETUP);
		this.registerReceiver(mReceiver, intentFilter);
		
		MainActivity.this.mTabHost.setCurrentTab(2);
		ConfigHelper.getInstance().addListener(this);
		//ConfigHelper.getInstance().refresh();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
		}
		ConfigHelper.getInstance().removeListener(this);
	}

	protected void setOnContentView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_tabs_bottom);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return false;
	}
    public void onResume(){
    	super.onResume();
    	//LocationInfoManager.getInstance().start();
    }
    public void onStop(){
    	super.onStop();
    	//LocationInfoManager.getInstance().stop();
    }
    public void onTabChanged(String tabName) {
    	super.onTabChanged(tabName);
    	sendEvent("main", "tab", tabName, 0);
	}
 
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(this);

		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SiemensStatusLogo.instance().unregisterNotification();
				finish();
				sendEvent("main", "quit", "true", 1);
			}

		});

		builder.setNegativeButton("取消",

		new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				sendEvent("main", "quit", "false", 1);
			}

		});

		builder.create().show();

	}

	@Override
	public void onListenerRespond(Object sender, Object args) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(MESSAGE_UPDATECHECK);
	}

	@Override
	public Boolean processMsg(BaseMsgInfo msg) {
		// TODO Auto-generated method stub
		if(msg.getMsgId() == MsgDefine.MSG_CONFIG_UPDATE_RES_SUBSCRIB){
			ConfigHelper.getInstance().refresh();
			return true;
		}else{
			return false;
		}
	}
	private synchronized void onUpdateCheck() {

		try {
			if (ConfigHelper.getInstance().getHasPushNotice()) {
				if (ConfigHelper.getInstance().getPushNotice() != null) {
					
					new AlertDialog.Builder(this)
							.setTitle("提示")
							.setMessage(
									ConfigHelper.getInstance().getPushNotice())
							.setNegativeButton("确认",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
											sendEvent("login", "pushNotice", "", 0);
										}
									}).show();

				}
				
			} else {
				PackageManager manager = this.getPackageManager();
				PackageInfo info = manager.getPackageInfo(
						this.getPackageName(), 0);
				Version nowVersion = new Version(info.versionName);
				if (ConfigHelper.getInstance().getMinVersion()
						.isNewer(nowVersion)
						|| ConfigHelper.getInstance().getNewVersion()
								.isNewer(nowVersion)) {
					if (updateView == null) {
						updateView = new VersionUpdate(this);
					} else {
						updateView.updateContentText();
					}
				}
			}

		} catch (Throwable e) {

		}
	}


}
