package com.mobilitychina.zambo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.home.NotificationActivity;

public class SiemensStatusLogo {
	
	private static SiemensStatusLogo _instance;
	
	private SiemensStatusLogo(){
		
	}
	
	public static SiemensStatusLogo instance() {
		if(_instance == null){
			_instance = new SiemensStatusLogo();
		}
		return _instance;
	}
	
	public void unregisterNotification() {
		Context context = ZamboApplication.getInstance();
		NotificationManager myNotiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (myNotiManager != null) {
			myNotiManager.cancel(R.string.isWorking);
		}
	}

	public void registerNotification() {
		Context context = ZamboApplication.getInstance();
		NotificationManager myNotiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		// 设置图标是否能被系统清理掉
		notification.icon = R.drawable.ic_launcher;
		notification.flags = notification.flags|Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		// 绑定intent，主要作用就是点击图标时能够进入程序
		Intent notificationIntent = new Intent(context, NotificationActivity.class);
		PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, context.getText(R.string.isWorking), "", contentItent);
		myNotiManager.notify(R.string.isWorking, notification);
	}

}
