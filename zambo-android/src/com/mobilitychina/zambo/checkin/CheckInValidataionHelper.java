package com.mobilitychina.zambo.checkin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.text.TextUtils;

import com.mobilitychina.zambo.business.plan.data.PlanInfo;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager.CheckInMsg;
import com.mobilitychina.zambo.util.ConfigHelper;

public class CheckInValidataionHelper {
	public static boolean validateCheckInAndReport(List<PlanInfo> list) {
	
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Iterator<PlanInfo> iterator = list.iterator(); iterator.hasNext();) {
			PlanInfo planInfo = (PlanInfo) iterator.next();
			if(planInfo.getSignDate()!=null && planInfo.getSignDate().length() > 0 ){
				try {
					
					Date signDate = sdf.parse(planInfo.getSignDate());
					if ((signDate.getMonth() == date.getMonth())&& (signDate.getDate() == date.getDate())) {// 同一天签到
						if (planInfo.getVisitDate() != null
								&& planInfo.getVisitDate().length() > 0) {
							Date visitDate = sdf.parse(planInfo.getVisitDate());
							if ((visitDate.getMonth() != date.getMonth())
									|| (visitDate.getDate() != date.getDate())) {
								return false;
							}
						} else {
							return false;
						}
					} 
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			}
		}
		
		return true;
	}
	public static boolean validate(Activity activity, CheckInMsg checkInMsg) {
		if(!ConfigHelper.getInstance().isValidateCheckin()){
			return true;
		}
		
		int checkInStamp = ConfigHelper.getInstance().getCheckInTime();
		checkInStamp = checkInStamp > 0 ? checkInStamp : 120;
		CheckInMsg lastCheckInMsg = CheckInDBHelper.instance().queryByCustId(checkInMsg.custId());
		if (lastCheckInMsg == null) {
			return true;
		}
		Date lastDate = formateDate(lastCheckInMsg.datetime(), "yyyy-MM-dd HH:mm:ss");
		Calendar lastCalendar = Calendar.getInstance();
		lastCalendar.clear();
		lastCalendar.setTime(lastDate);
		lastCalendar.add(Calendar.MINUTE, checkInStamp);

		Date date = formateDate(checkInMsg.datetime(), "yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);

		if (!isSameDay(checkInMsg.datetime(), lastCheckInMsg.datetime())) {
			return true;
		}

		if (lastCalendar.after(calendar)) {
			showDialog(activity, "现在距您上次签到时间较短，系统暂不允许您进行签到，请稍后再试.");
			return false;
		}
		if (!validateNum(activity, lastCheckInMsg)) {
			return false;
		}
		return true;
	}

	private static boolean validateNum(Activity activity, CheckInMsg checkInMsg) {
		int limited = ConfigHelper.getInstance().getCheckInCount();
		limited = limited > 0 ? limited : 3;
		if (checkInMsg.checkInNumPerDay() > limited) {
			showDialog(activity, "您今天的签到次数已达到限制的最大值。");
			return false;
		}
		return true;
	}

	private static void showDialog(Activity activity, String message) {
		Builder builder = new Builder(activity);
		builder.setTitle("提示");
		builder.setMessage(message);
		builder.setPositiveButton("确定", null);
		builder.setCancelable(false);
		builder.show();
	}

	private static Date formateDate(String date, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
			return dateFormat.parse(date);
		} catch (Exception e) {

		}
		return null;
	}

	private static boolean isSameDay(String date1, String date2) {
		if (TextUtils.isEmpty(date1) || TextUtils.isEmpty(date2)) {
			return false;
		}
		return date1.substring(0, 10).equals(date2.substring(0, 10));
	}
}
