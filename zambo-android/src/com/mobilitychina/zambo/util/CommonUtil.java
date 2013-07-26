package com.mobilitychina.zambo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.widget.LoadingItem;
import com.mobilitychina.zambo.widget.RetryItem;
import com.mobilitychina.zambo.widget.SimpleMessageItem;

public class CommonUtil {

	public static AlertDialog showConfirmDialg(Context context, String title, String content,
			DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no) {
		AlertDialog ad = new AlertDialog.Builder(context).setTitle(title).setMessage(content)
				.setPositiveButton("确定", yes).setNegativeButton("取消", no).setCancelable(false).show();

		return ad;
	}

	public static AlertDialog showChooiceDialg(Context context, String title, String content, String b1Text,
			String b2Text, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no) {

		AlertDialog ad = new AlertDialog.Builder(context).setTitle(title).setMessage(content)
				.setPositiveButton(b1Text, yes).setNegativeButton(b2Text, no).setCancelable(false).show();
		return ad;
	}

	public static AlertDialog showChooiceDialg1(Context context, String title, String content, String b1Text,
			String b2Text, DialogInterface.OnClickListener op1, DialogInterface.OnClickListener op2) {

		AlertDialog ad = new AlertDialog.Builder(context).setTitle(title).setMessage(content)
				.setPositiveButton(b1Text, op1).setNeutralButton(b2Text, op2).setNegativeButton("取消", null)
				.setCancelable(false).show();

		return ad;
	}

	public static ProgressDialog showWaitingDialg(Context context, String title, String msg) {
		ProgressDialog progress = new ProgressDialog(context);
		progress.setTitle(title);
		progress.setMessage(msg);
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();
		return progress;
	}

	public static boolean hasShortCut(Context context) {
		if (context == null) {
			return false;
		}

		String url = "content://com.android.launcher2.settings/favorites?notify=true";

		try {
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(Uri.parse(url), new String[] { "title", "iconResource" }, "title=?",
					new String[] { context.getString(R.string.app_name) }, null);
			boolean ret = false;
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = true;
				}
				cursor.close();
			}

			return ret;
		} catch (Exception ex) {
			return true;
		}
	}

	public static int isMainServer(Context context) {
		return context.getSharedPreferences(ConfigDefinition.PREFS_DATA, 0).getInt("switchMainServer", 0);
	}

	public static void switchServer(Context context, int isMainServer) {
		SharedPreferences.Editor editor = context.getSharedPreferences(ConfigDefinition.PREFS_DATA, 0).edit();
		editor.putInt("switchMainServer", isMainServer);
		editor.commit();
		SoapService.switchServer(isMainServer);
	}
//	public static boolean isMainService(){
//		
//		SharedPreferences share = SiemensApplication.getInstance().getApplicationContext().getSharedPreferences(ConfigDefinition.PREFS_DATA, 0);
//		return share.getBoolean("switchMainServer", true);
//	}

	public static int getScreenWidthPixels(Activity activity) {
		if (activity == null) {
			return 0;
		}

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeightPixels(Activity activity) {
		if (activity == null) {
			return 0;
		}
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public static void gotoHome(Context context) {
		Intent intent = new Intent();
		intent.setAction("GotoHome");
		context.sendBroadcast(intent);
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
				Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();// 获取机器码
	}

	/**
	 * 构造加载的列表item
	 * 
	 * @param context
	 *            context
	 * @param convertView
	 *            List的复用View
	 * @param loadingText
	 *            加载提示,当为null时，显示默认文案"正在加载..."
	 * @return
	 */
	public static View buildListLoadingItemView(Context context, View convertView, String loadingText) {
		LoadingItem view;
		if (convertView == null || !(convertView instanceof LoadingItem)) {
			view = (LoadingItem) LayoutInflater.from(context).inflate(R.layout.item_loading, null);

		} else {
			view = (LoadingItem) convertView;
		}
		if (loadingText != null) {
			view.setLoadingText(loadingText);
		}
		return view;
	}

	/**
	 * 构造简单文本提示的列表Item
	 * 
	 * @param context
	 * @param convertView
	 *            List的复用View
	 * @param text
	 *            提示文案，当为null时，显示默认文案"暂无数据"
	 * @return
	 */
	public static View buildListSimpleMsgItemView(Context context, View convertView, String text) {
		SimpleMessageItem view;
		if (convertView == null || !(convertView instanceof SimpleMessageItem)) {
			view = (SimpleMessageItem) LayoutInflater.from(context).inflate(R.layout.item_simplemsg, null);

		} else {
			view = (SimpleMessageItem) convertView;
		}
		if (text != null) {
			view.setSimpleMessage(text);
		}
		return view;
	}

	/**
	 * 构造重试的列表Item
	 * 
	 * @param context
	 * @param convertView
	 *            List的复用View
	 * @param text
	 *            错误提示
	 * @param retryListener
	 *            重试listener
	 * @return
	 */
	public static View buildListRetryItemView(Context context, View convertView, String text,
			View.OnClickListener retryListener) {
		RetryItem view;
		if (convertView == null || !(convertView instanceof RetryItem)) {
			view = (RetryItem) LayoutInflater.from(context).inflate(R.layout.item_retry, null);

		} else {
			view = (RetryItem) convertView;
		}
		if (text != null) {
			view.setErrorMessage(text);
		}
		if (retryListener != null) {
			view.setRetryOnClickListener(retryListener);
		}
		return view;
	}

	/**
	 * 获取Soap对象属性中的某个字段
	 * 
	 * @param element
	 * @param tag
	 * @return
	 */
	public static String getStringElement(String element, String tag) {
		tag += "=";
		String result = "";
		if (element.indexOf(tag) > 0) {
			if(element.indexOf(";", element.indexOf(tag))>0){
				result = element.substring(element.indexOf(tag) + tag.length(), element.indexOf(";", element.indexOf(tag)));
			}else if (element.indexOf("|", element.indexOf(tag))>0){
				result = element.substring(element.indexOf(tag) + tag.length(), element.indexOf("|", element.indexOf(tag)));
			}
		}
		if (result.equalsIgnoreCase("null")) {
			result = "";
		}else if (result.equalsIgnoreCase(("anyType{}"))){
			result = "";
		}
		return result;
	}

	/**
	 * 将数字格式化为指定长度的字符串，不足补零
	 * 
	 * @param number
	 * @return
	 */
	public static String formatNumber(int number, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append("0");
		}
		java.text.DecimalFormat format = new java.text.DecimalFormat(sb.toString());
		return format.format(number);
	}

	/**
	 * 获取年，若当前年，格式为“今年”
	 * 
	 * @param date
	 *            格式为yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatYear(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
			SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.CHINA);
			String year = format.format(date);
			if (year.equalsIgnoreCase(format.format(new Date()))) { // 当前年，格式为“今年”
				year = "今年";
			} else {
				year += "年";
			}
			return year;

		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取月天
	 * 
	 * @param date
	 *            格式为yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatMonthDay(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
			SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
			return format.format(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 格式当天
	 * 
	 * @param format
	 * @return
	 */
	public static String formatToday(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
		return dateFormat.format(new Date());
	}

	public static String getCurrentTime(String format) {
		try {
			if (!TextUtils.isEmpty(format)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
				return dateFormat.format(new Date());
			}
		} catch (Exception e) {

		}
		return "";
	}
	public static long getUnUseMemory(Context mContext) {
        long MEM_UNUSED;
	// 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	// 创建ActivityManager.MemoryInfo对象  

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

	// 取得剩余的内存空间 

        MEM_UNUSED = mi.availMem ;
        return MEM_UNUSED;
    }

	public static String getNetWorkInfo(Context mContext) {
		ConnectivityManager connectionManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取网络的状态信息，有下面三种方式
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		// NetworkInfo 有一下方法
		if(networkInfo!= null){
			StringBuilder sb = new StringBuilder();
			sb.append("网络状态:" + networkInfo.getDetailedState());
			sb.append("网络ID:" + networkInfo.getType());
			sb.append("网络类型:" + networkInfo.getTypeName());
			sb.append("附加信息:" + (networkInfo.getExtraInfo() == null ? "" : networkInfo.getExtraInfo()));
			sb.append("失败原因:" + (networkInfo.getReason() == null ? "": networkInfo.getReason()));
			sb.append("网络可用:" + networkInfo.isAvailable());
			return sb.toString();
		}else{
			return "网络不可用";
		}
	}
	public static String getCellInfo(Context mContext) {
		try {	
			TelephonyManager mTelephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);  
	
			// 返回值MCC + MNC  
			String operator = mTelephonyManager.getNetworkOperator();  
			int mcc = Integer.parseInt(operator.substring(0, 3));  
			int mnc = Integer.parseInt(operator.substring(3));  
			int lac;
			int cellId;
			// 中国移动和中国联通获取LAC、CID的方式  
			if(mTelephonyManager.getCellLocation() instanceof GsmCellLocation){
				GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();  
				 lac = location.getLac();  
				 cellId = location.getCid();  
			}else{
				CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation(); 
		    	lac = location1.getNetworkId(); 
		    	cellId = location1.getBaseStationId(); 
		    	cellId /= 16; 
			}
			
			return ("MCC:" + mcc + "t MNC:" + mnc + "t LAC:" + lac + "t CID : " + cellId);  
			// 中国电信获取LAC、CID的方式  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static String getNearbyCellInfo(Context mContext) {
	 // 获取邻区基站信息 
		TelephonyManager mTelephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);  

		List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();  
		StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "n");  
		for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环  
			sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC  
			sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID  
        	sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "n"); // 获取邻区基站信号强度  
		}  

		return( "获取邻区基站信息:" + sb.toString());  
	}
	
}
	
