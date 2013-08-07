package com.mobilitychina.zambo.home;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

import com.mobilitychina.crash.CrashHandler;
import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.util.Environment;
import com.mobilitychina.util.Log;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.business.more.ModifyPasswordActivity;
import com.mobilitychina.zambo.service.HttpPostService;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.ConfigHelper;
import com.mobilitychina.zambo.util.DownloadHelper;
import com.mobilitychina.zambo.util.EventListener;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.util.SiemensStatusLogo;
import com.mobilitychina.zambo.util.Version;
import com.mobilitychina.zambo.util.VersionUpdate;

/**
 * 登录界面
 * 
 * @author chenwang
 * 
 */
public class LoginActivity extends BaseActivity implements ITaskListener,
		OnKeyListener, EventListener {
	private static final String TAG = "LoginActivity";

	private Button loginBtn;
	private EditText etSalescode;
	private EditText etPassword;
	private Button btnHelp;
	private VersionUpdate updateView;
	//private NotificationManager myNotiManager;
	private final static int MESSAGE_UPDATECHECK = 1;
	private final static int REQUEST_CODE_SWITCHSERVER = 0x01;
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

	private HttpPostTask loginTask;

	protected boolean shouldCustomTitle() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConfigHelper.getInstance().addListener(this);
		ConfigHelper.getInstance().refresh();
		McLogger.getInstance().setUrlHost(ConfigDefinition.URL_LOG);
		CrashHandler.getInstance().init(this,ConfigDefinition.URL_CRASH);
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(
					this.getPackageName(), 0);

			Environment.getInstance().setVersion(info.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Environment.getInstance().setVersion(String.valueOf(-1));

		}
		
		UserInfoManager.getInstance().sync(this, false);

		this.setContentView(R.layout.activity_login);
		btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DownloadHelper helper = 	new DownloadHelper(LoginActivity.this);
				helper.start("http://eiip.mobilitychina.com/cvms_2_0_Viedo.mp4");
				sendEvent("login", "help", "", 0);
				
			}
		});
		etSalescode = (EditText) findViewById(R.id.salescode);
		etPassword = (EditText) findViewById(R.id.password);
		etPassword.setOnKeyListener(this);

		((Button) findViewById(R.id.btnPassForget))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendEvent("login", "forgetpassword", "", 0);
						Builder builder = new Builder(LoginActivity.this);
						builder.setTitle("提示");
						builder.setMessage("您可通过客服电话重置您的密码,是否拨打您的客户服务电话?");
						builder.setPositiveButton("拨打",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												Intent.ACTION_CALL,
												Uri.parse("tel:021-38891777"));
										// 通知activtity处理传入的call服务
										sendEvent("login", "call", "", 0);
										startActivity(intent);
									}
								});
						builder.setNegativeButton("取消", null);

						builder.show();
					}
				});

		etSalescode.setText(UserInfoManager.getInstance().getUserId());
		etPassword.setText(UserInfoManager.getInstance().getPassword());

		loginBtn = (Button) findViewById(R.id.commit);
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});

		((Button) this.findViewById(R.id.settingButton))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(LoginActivity.this,
								SwitchServerActivity.class);
						startActivityForResult(intent,
								REQUEST_CODE_SWITCHSERVER);
						sendEvent("login", "setting", "", 0);
					}
				});
		// this.onUpdateCheck();
		this.addShortcut();
		this.initServer();
//		int  i =0;
//		int y = 3/i;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_SWITCHSERVER) {
			
			this.initServer();
		}
	}

	protected boolean shouldShowTestMark() {
		return false;
	}

	private void initServer() {
		// 初始化WebService的服务器
		CommonUtil.switchServer(this, CommonUtil.isMainServer(this));
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ConfigHelper.getInstance().removeListener(this);
	}

	private void addShortcut() {
		SharedPreferences settings = this.getSharedPreferences(
				ConfigDefinition.PREFS_DATA, 0);
		if (!settings.getBoolean("shortcutOpened", false)
				&& !CommonUtil.hasShortCut(this)) {
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.app_name));
			shortcut.putExtra("duplicate", false);
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			i.setComponent(new ComponentName("com.mobilitychina.siemens",
					"com.mobilitychina.siemens.home.LoginActivity"));
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);

			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(this, R.drawable.ic_launcher);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			sendBroadcast(shortcut);

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("shortcutOpened", true);
			editor.commit();
		}
	}

	private void login() {
		updateButtonLook(true);
		cancelLogin();
		tryLogin();
	}

	private void tryLogin() {
		String password = etPassword.getText().toString();
		String phone = etSalescode.getText().toString();

		if ((phone == null) || phone.trim().length() == 0) {
			showErrorMessage(R.string.login_err_nophone);
			etSalescode.requestFocus();
			return;
		}

		if ((password == null) || password.trim().length() == 0) {
			showErrorMessage(R.string.login_err_nopassword);
			etPassword.requestFocus();
			return;
		}

		etPassword.clearFocus();
		etSalescode.clearFocus();
		Environment.getInstance().setLoginId(phone);
		Environment.getInstance().setPassword(password);
		this.showProgressDialog("正在登录...");
		McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOGIN,"开始登陆");
		loginTask = new HttpPostTask(this);
		loginTask.setUrl(HttpPostService.SOAP_URL+"login");
		loginTask.getTaskArgs().put("imei", CommonUtil.getIMEI(this));
		loginTask.setListener(this);
		loginTask.start();
	}

	protected void onProgressDialogCancel() {
		this.cancelLogin();
	}

	private void cancelLogin() {
		this.dismissDialog();
		if (loginTask != null) {
			loginTask.cancel(true);
			loginTask = null;
		}
	}

	private void updateButtonLook(boolean keepSelected) {
		// if (keepSelected) {
		// loginBtn.setBackgroundResource(R.drawable.common_btn_clicked);
		// } else {
		// loginBtn.setBackgroundResource(R.drawable.login_commit_btn_t);
		// }
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (v == etPassword && keyCode == KeyEvent.KEYCODE_ENTER
				&& event.getAction() == KeyEvent.ACTION_UP) {
			this.updateButtonLook(true);
			this.cancelLogin();
			this.tryLogin();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			SiemensStatusLogo.instance().unregisterNotification();
			android.os.Process.killProcess(android.os.Process.myPid());
			sendEvent("login", "quit", "", 0);
			return true;
		}
		return false;
	}
	
	public void showErrorMessage(int strResId) {
		Builder builder = new Builder(LoginActivity.this);
		builder.setTitle(R.string.login_err_title);
		builder.setMessage(strResId);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						updateButtonLook(false);
						dialog.cancel();
					}
				});
		builder.show();
	}
	public void showErrorMessage(String strResId) {
		Builder builder = new Builder(LoginActivity.this);
		builder.setTitle(R.string.login_err_title);
		builder.setMessage(strResId);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateButtonLook(false);
				dialog.cancel();
			}
		});
		builder.show();
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		Log.e(task.getError().toString());
		this.showErrorMessage(R.string.err_network);
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		NetObject result = ((HttpPostTask) task).getResult();
		if(result==null)
			return;
	    Log.i("HttpPostTask","login message:" +result.toString());
	  
		String code = result.stringForKey("code");
		String message = result.stringForKey("message");
		if(code.equals("-2")){
			Log.d(TAG, "Login fail check password and phone ..");
			showErrorMessage(R.string.login_err_identity);
			sendEvent("login", "login", "账号密码错误", 0);
			return;
		}
		if(Integer.valueOf(code)>0){
			Log.d(TAG, "Login fail check password and phone ..");
			if(message==null||message.equals(" "))
			showErrorMessage("操作失败");
			showErrorMessage(message);
			return;
		}
		/*String[] loginResultArray = result.toString().split("&");
		if (loginResultArray.length < 3) {
			Log.d(TAG, "Login faile with result name&posId&loginFlag&isLeader:"
					+ result);
			this.showErrorMessage(R.string.err_network);
			sendEvent("login", "login", "网络错误", 0);
			return;
		}
		String loginFlag = loginResultArray[2];
		McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOGIN,loginFlag);
		if ("1".equals(loginFlag)) {
			Log.d(TAG, "Login fail you phone no registration ..");
			this.showErrorMessage(R.string.login_err_no_register_identity);
			sendEvent("login", "login", "设备号不匹配", 0);
			return;
		}

		if ("2".equals(loginFlag) || "3".equals(loginFlag)) {
			Log.d(TAG, "Login fail check password and phone ..");
			showErrorMessage(R.string.login_err_identity);
			sendEvent("login", "login", "账号密码错误", 0);
			return;
		}
		if ("5".equals(loginFlag)) {
			Log.d(TAG, "Login your phone's deviceId already in use ..");
			showErrorMessage(R.string.login_err_deviceid_in_use);
			sendEvent("login", "login", "设备已绑定其他手机号码", 0);
			return;
		}
		if ("8".equals(loginFlag)) {
			Log.d(TAG, "Login your phone's version is overdue");
			showErrorMessage(R.string.login_err_overdue);
			sendEvent("login", "login", "版本号过期", 0);
			return;
		}
		Log.d(TAG, "Login successful!");
		sendEvent("login", "login", "successful", 0);
		String name = loginResultArray[0];
		String posId = loginResultArray[1];
		String isLeader = loginResultArray[3];*/
		Environment.getInstance().setClientID(etSalescode.getText().toString());//id
		
		UserInfoManager.getInstance()
				.setUserId(etSalescode.getText().toString());
		UserInfoManager.getInstance().setPassword(
				etPassword.getText().toString());
//		UserInfoManager.getInstance().setName(name);
//		UserInfoManager.getInstance().setPosId(posId);
//		UserInfoManager.getInstance().setLeader(isLeader.equalsIgnoreCase("Y"));
		UserInfoManager.getInstance().sync(this, true);
		UserInfoManager.getInstance().print();
//		
//		if ("2".equals(code)) { // 第一次登录，进入修改密码
//			Log.d(TAG, "the fisrt login please modify your password..");
//			Intent intent = new Intent(LoginActivity.this,
//					ModifyPasswordActivity.class);
//			intent.putExtra("firstLogin", true);
//			startActivity(intent);
//			sendEvent("login", "firstLogin", "", 0);
//			finish();
//		} else {
//			Intent intent = new Intent(this, MainActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			intent.putExtra("empId", "");
//			intent.putExtra("flag", "2");
//			intent.putExtra("firstLogin", "F");
//			startActivity(intent);
//			this.finish();
//		}
		
		if(code.equals("0")){
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("empId", "");
			intent.putExtra("flag", "2");
			intent.putExtra("firstLogin", "F");
			startActivity(intent);
			this.finish();
		}
		
		//McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOGIN,loginFlag);
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
	}

	@Override
	public void onListenerRespond(Object sender, Object args) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(MESSAGE_UPDATECHECK);
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
				if (ConfigHelper.getInstance().getIsMaintenanceMode()) {
					// password.setEnabled(false);
					loginBtn.setEnabled(false);
					etPassword.setEnabled(false);
					etSalescode.setEnabled(false);
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

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}

}
