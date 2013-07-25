package com.mobilitychina.zambo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.webkit.URLUtil;

public class VersionUpdate {
	private final static int TAG_CONNECT_INDEX = 1;
	private final static int TAG_DOWNLOAD_PERCENT = 2;
	private final static int TAG_CONNECT_SUCCESS = 3;
	private final static String KEY_PERCENT = "KEY_PERCENT";
	private final static String KEY_URL_INDEX = "KEY_URL_INDEX";
	private Boolean isNeedQuit;//升级处理完成后是否退出应用程序；
	private Activity activity = null;
	private static final String TAG = "VersionUpdate";
	private String fileEx = "";
	private String fileNa = "";
	private ArrayList<String> updateUrls = ConfigHelper.getInstance().getUpdateUrl();
	private ProgressDialog dialog;
	private UpdateTask updateTask;
	private AlertDialog alert ;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case TAG_DOWNLOAD_PERCENT:
				int index = msg.getData().getInt(KEY_PERCENT);
				if (index >= 100) {
					dialog.cancel();
				}
				dialog.setProgress(index);
				break;
			case TAG_CONNECT_INDEX:
				String urlIndex = msg.getData().getString(KEY_URL_INDEX);
				dialog.setMessage("正在尝试链接第" + urlIndex + "个下载地址");
				dialog.setIndeterminate(true);
				break;
			case TAG_CONNECT_SUCCESS:
				dialog.setMessage("开始下载，请耐心等待...");
				dialog.setIndeterminate(false);
				break;

			}

			super.handleMessage(msg);
		}

	};

	public VersionUpdate(Activity activity) {
		this.activity = activity;
		showUpdateDialog();
	}

	void startActivity(Intent intent) {
		this.activity.startActivity(intent);
	}
	
	public void updateContentText(){
		if (alert != null) {
			alert.setMessage("升级提示:"
					+ ConfigHelper.getInstance().getUpdateContent() + "\n是否更新?");
			alert.show();
		}
	}
	public void showUpdateDialog() {
		// @SuppressWarnings("unused")
		PackageManager manager = this.activity.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(
					this.activity.getPackageName(), 0);
			Version nowVersion = new Version(info.versionName);
			if (ConfigHelper.getInstance().getMinVersion().isNewer(nowVersion)) {
				// 强制升级
				this.isNeedQuit= true;
				this.alert = new AlertDialog.Builder(this.activity)
						.setTitle("提示")
						.setMessage(
								"升级提示:"
										+ ConfigHelper.getInstance()
												.getUpdateContent() + "\n是否更新?")
						.setPositiveButton("更新",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downloadTheFile();
										showWaitDialog();
									}
								})
						.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
										quitApplication();
									}

									
								}).setCancelable(false).show();
			} else if (ConfigHelper.getInstance().getNewVersion()
					.isNewer(nowVersion)) {
				// 更新升级
				this.isNeedQuit = false;
				alert = new AlertDialog.Builder(this.activity)
						.setTitle("提示")
						.setMessage(
								"升级提示:"
										+ ConfigHelper.getInstance()
												.getUpdateContent() + "\n是否更新?")
						.setPositiveButton("更新",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downloadTheFile();
										showWaitDialog();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	private void quitApplication() {
		Intent intent = new Intent(
				Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		android.os.Process.killProcess(Process
				.myPid());
	}
	public void showWaitDialog() {
		dialog = new ProgressDialog(activity);
		dialog.setMessage("正在尝试链接服务器");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setMax(100);
		dialog.show();
	}

	// 下载文件
	private Boolean downloadTheFile() {

		if (updateTask == null) {
			updateTask = new UpdateTask();
		}

		updateTask.execute();

		return true;
	}

	/**
	 * 下载单个文件
	 * 
	 * @param strURL
	 * @return
	 */
	private Boolean doDownLoadOneFile(String strURL) {
		InputStream is = null;
		try {
			URL myURL = new URL(strURL);
			URLConnection conn = myURL.openConnection();
			conn.connect();
			is = conn.getInputStream();
			if (is == null) {
				throw new RuntimeException("stream is null");
			}
			handler.sendEmptyMessage(TAG_CONNECT_SUCCESS);//链接成功
			File myTempFile = File.createTempFile(fileNa, "." + fileEx);
			//currentTempFilePath = myTempFile.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(myTempFile);
			int contentLength = conn.getContentLength();
			int offset = 0;
			int percent = 0;
			byte buf[] = new byte[128];
			do {
				int numread = is.read(buf);
				offset += numread;
				int index = (int) (offset * 100.0 / contentLength);
				if (index != percent) {
					percent = index;
					Message msgPercent = new Message();
					msgPercent.what = TAG_DOWNLOAD_PERCENT;
					Bundle bundlePercent = new Bundle();
					bundlePercent.putInt(KEY_PERCENT, percent);
					msgPercent.setData(bundlePercent);
					handler.sendMessage(msgPercent);//更新百分比
				}
				if (numread <= 0) {
					break;
				}
				fos.write(buf, 0, numread);
			} while (true);
			Log.i(TAG, "getDataSource() Download  ok...");
			dialog.cancel();
			dialog.dismiss();
			fos.flush();
			openFile(myTempFile);
			return true;// 升级成功，退出
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
			return false;

		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
				Log.e(TAG, "getDataSource() error: " + ex.getMessage(), ex);
				return false;
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean doDownloadTheFile() throws Exception {
		Log.i(TAG, "getDataSource()");
		String strURL;
		int i = 0;
		for (; i < this.updateUrls.size(); i++) {
			strURL = this.updateUrls.get(i);
			fileEx = strURL.substring(strURL.lastIndexOf(".") + 1,
					strURL.length()).toLowerCase();
			fileNa = strURL.substring(strURL.lastIndexOf("/") + 1,
					strURL.lastIndexOf("."));
			if (!URLUtil.isNetworkUrl(strURL)) {
				Log.i(TAG, "getDataSource() It's a wrong URL!");
			} else {

				Message msg = new Message();
				msg.what = TAG_CONNECT_INDEX;
				Bundle bundle = new Bundle();
				bundle.putString(KEY_URL_INDEX, (i + 1) + "");
				msg.setData(bundle);
				handler.sendMessage(msg);
				if(doDownLoadOneFile(strURL)){
					break;
				}
			}
		}
		if (i < this.updateUrls.size()) {
			return true;
		} else {
			return false;
		}
	}

	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		activity.startActivity(intent);
	}

	public void delFile(String path) {
		Log.i(TAG, "The TempFile(" + path + ") was deleted.");
		File myFile = new File(path);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	public class UpdateTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				dialog.cancel();
				dialog.dismiss();
			}
			if(isNeedQuit){
				quitApplication();
			}
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				return doDownloadTheFile();
			} catch (Exception e) {
				return false;
			}
		}

	}
}