package com.mobilitychina.zambo.app;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;


import com.google.analytics.tracking.android.EasyTracker;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.SiemensStatusLogo;
import com.mobilitychina.zambo.util.Statistics;

public class BaseActivity extends Activity {
	/**
	 * 标识测试环境
	 */
	private ImageView testMarkImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.shouldShowTestMark()) {
			if (!CommonUtil.isMainServer(this)) {
				this.setTestInfoHidden(false);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (testMarkImageView != null && testMarkImageView.getParent() != null) {
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(testMarkImageView);
			testMarkImageView = null;
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}

	protected boolean shouldShowTestMark() {
		return true;
	}

	protected Dialog dialog;

	public void dismissDialog() {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

	protected void onProgressDialogCancel() {

	}

	public void showProgressDialog(String title,String button,int which,DialogInterface.OnClickListener listener) {
		dismissDialog();
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onProgressDialogCancel();
			}
		});
		dlg.setButton(Dialog.BUTTON_NEGATIVE, button, listener);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return false;
			}
		});
		dlg.setMessage(title);
		
		dialog = dlg;
		dlg.show();
	}
	public void showProgressDialog(String title,String button,int which,DialogInterface.OnClickListener listener,Boolean canCancel) {
		dismissDialog();
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onProgressDialogCancel();
			}
		});
		dlg.setButton(Dialog.BUTTON_NEGATIVE, button, listener);
		dlg.setCancelable(canCancel);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return false;
			}
		});
		dlg.setMessage(title);
		dialog = dlg;
		dlg.show();
	}
	public void showProgressDialog(String title,Boolean canCancel) {
		dismissDialog();
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(canCancel);
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onProgressDialogCancel();
			}
		});
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return false;
			}
		});
		dlg.setMessage(title);
		dialog = dlg;
		dlg.show();
	}
	public void showProgressDialog(String title) {
		dismissDialog();
		ProgressDialog dlg = new ProgressDialog(this);
		//dlg.setCancelable(false);
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onProgressDialogCancel();
			}
		});
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return false;
			}
		});
		dlg.setMessage(title);
		dialog = dlg;
		dlg.show();
	}
	public void editDialog(String title){
		if(dialog != null){
			if(dialog instanceof ProgressDialog){
				((ProgressDialog)dialog).setMessage(title);
			}
		}
	}

	public void showDialog(int titleResId, int messageResId, DialogInterface.OnClickListener listener) {
		Builder builder = new Builder(this);
		builder.setTitle(titleResId);
		builder.setMessage(messageResId);
		builder.setPositiveButton("确定", listener);
		builder.setCancelable(false);
		builder.show();
	}

	public void showDialog(String title, String message, DialogInterface.OnClickListener listener) {
		Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("确定", listener);
		builder.setCancelable(false);
		builder.show();
	}
	public void showDialog(String title, String message, String posMessage,String negMessage,  DialogInterface.OnClickListener listener) {
		Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(posMessage, listener);
		builder.setNegativeButton(negMessage, listener);
		builder.setCancelable(false);
		builder.show();
	}
	private void addViewToWindow(View view, boolean touchEnabled) {
		if (view == null) {
			return;
		}
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		if (touchEnabled) {
			params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		} else {
			params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		}
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.alpha = 100;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.x = (CommonUtil.getScreenWidthPixels(this) - 292) / 2;
		params.y = (CommonUtil.getScreenHeightPixels(this) - 268) / 2 - 40;

		if (view.getParent() == null) {
			wm.addView(view, params);
		} else {
			wm.updateViewLayout(view, params);
		}
	}

	protected void setTestInfoHidden(boolean hidden) {
		if (hidden) {
			if (testMarkImageView != null && testMarkImageView.getParent() != null) {
				WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
				wm.removeView(testMarkImageView);
				testMarkImageView = null;
			}
		} else {
			if (testMarkImageView == null) {
				testMarkImageView = new ImageView(this);
				testMarkImageView.setImageResource(R.drawable.test_info);
				testMarkImageView
						.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				this.addViewToWindow(testMarkImageView, false);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Statistics.activityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Statistics.activityStop(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SiemensStatusLogo.instance().registerNotification();
	}
	
	public void sendEvent(String category, String action, String label, long value) {

		try{
			EasyTracker.getTracker().sendEvent(category, action, label, value);
		}catch(Exception e){
			
		}
	}
}
