package com.mobilitychina.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.mobilitychina.app.MobilityChinaApplication;

public class Environment {
	private final static Environment _instance =  new Environment();
	private String _version = "";
	private String _token = "";
	private String _clientID ="";
	public void setClientID (String clientId ) {
		_clientID = clientId;
	}
	public String getClientID()
	{
		return _clientID;
	}
	public String getVersion()
	{
		return _version;
	}
	public void setVersion(String version){
		_version = version;
	}
	public String getToken(){
		return _token;
	}
	public void setToken(String token){
		_token = token;
	}
	public static  Environment getInstance(){
		return _instance;
	}
	//private static PackageInfo packageInfo;

	private static ConnectivityManager connectivityManager;

	private Environment() {
		_clientID = getIMEI(MobilityChinaApplication.instance());
	}
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
				Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();// 获取机器码
	}
//	private static PackageInfo pkgInfo() {
//		if (packageInfo == null) {
//			try {
//				Context context = MobilityChinaApplication.instance();
//				packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//			} catch (NameNotFoundException e) {
//			}
//		}
//		return packageInfo;
//	}
//
//	public static String version() {
//		return pkgInfo().versionName;
//	}
//
	private static ConnectivityManager connectivityManager() {
		if (connectivityManager == null) {
			try {
				Context context = MobilityChinaApplication.instance();
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			} catch (Exception e) {
				Log.w("network",
						"cannot get connectivity manager, maybe the permission is missing in AndroidManifest.xml?", e);
			}
		}
		return connectivityManager;
	}

	public static String getNetworkInfo() {
		ConnectivityManager connectivityManager = connectivityManager();
		if (connectivityManager == null)
			return "unknown";
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null)
			return "unknown";
		switch (activeNetInfo.getType()) {
		case ConnectivityManager.TYPE_WIFI:
			return "wifi";
		case ConnectivityManager.TYPE_MOBILE:
			return "mobile(" + activeNetInfo.getSubtypeName() + "," + activeNetInfo.getExtraInfo() + ")";
		default:
			return activeNetInfo.getTypeName();
		}
	}

	public static boolean hasNetwork() {
		Context context = MobilityChinaApplication.instance();
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mgr.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		return true;
	}

}
