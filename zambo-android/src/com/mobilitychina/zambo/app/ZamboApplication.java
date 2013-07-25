package com.mobilitychina.zambo.app;


import android.app.NotificationManager;

import com.mobilitychina.app.MobilityChinaApplication;
import com.mobilitychina.util.Log;

public class ZamboApplication extends MobilityChinaApplication {

	private static ZamboApplication _instance;
	protected NotificationManager myNotiManager;
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if ((getApplicationInfo().flags & 2) != 0) {
			Log.LEVEL = Log.VERBOSE;
		}else{
			Log.LEVEL = Integer.MAX_VALUE;
		}
		_instance = this;
		
//		MainUILooperDelegate.setUncaughtExceptionHandler(CrashHandler.getInstance());
//		MainUILooperDelegate.install();

	}

	public static ZamboApplication getInstance() {
		return _instance;
	}
}
