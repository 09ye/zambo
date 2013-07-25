package com.mobilitychina.app;

import com.mobilitychina.util.Log;

import android.app.Application;

public class MobilityChinaApplication extends Application {
	
	private static MobilityChinaApplication _instance;
	
	public void onCreate() {
		super.onCreate();
		if ((getApplicationInfo().flags & 2) != 0) {
			Log.LEVEL = Log.VERBOSE;
		}else{
			Log.LEVEL = Integer.MAX_VALUE;
		}
		
		_instance = this;
	}
	
	
	public static MobilityChinaApplication instance() {
		return _instance;
	}

}
