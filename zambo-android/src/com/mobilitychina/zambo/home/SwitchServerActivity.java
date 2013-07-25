package com.mobilitychina.zambo.home;

import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mobilitychina.log.McLogger;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.MsLogType;

public class SwitchServerActivity extends BaseDetailActivity implements OnClickListener {
	private boolean isManualSwitchServer = true; // 标明是否为手动选择切换，解决当取消选择时，恢复之前选择的RadioButton

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.switch_server);

		this.setTitle("设置");
		this.getTitlebar().setRightButton("上传信息", this);
		RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.switchServerRadioGroup);
		radioGroup.check(CommonUtil.isMainServer(this) ? R.id.mainServerRadio : R.id.testServerRadio);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final RadioGroup group, final int checkedId) {
				// TODO Auto-generated method stub
				if (!isManualSwitchServer) {
					isManualSwitchServer = true;
					return;
				}
				RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
				CommonUtil.showChooiceDialg(SwitchServerActivity.this, "提示", "您确定要切换至<" + radioButton.getText() + ">？",
						"确定", "取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								if (checkedId == R.id.mainServerRadio) {
									CommonUtil.switchServer(SwitchServerActivity.this, true);
								} else {
									CommonUtil.switchServer(SwitchServerActivity.this, false);
								}

								{ // 清除数据
								// new CustInfo().reSetTable(); //退出时清空客户数据
								// AddProject.deleteAllAddProject(); //清空项目数据
								// CustType.deleteAllCustType();//清空客户类型
								// new
								// SiemensEmpInfo().deleteAllSiemensEmpInfo();
								// new MessageInfo().deleteAllMessageInfo();
								}
								finish();
							}

						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();

								isManualSwitchServer = false;
								RadioGroup radioGroup = (RadioGroup) SwitchServerActivity.this
										.findViewById(R.id.switchServerRadioGroup);
								if (CommonUtil.isMainServer(SwitchServerActivity.this)) {
									((RadioButton) radioGroup.findViewById(R.id.mainServerRadio)).setChecked(true);
								} else {
									((RadioButton) radioGroup.findViewById(R.id.testServerRadio)).setChecked(true);
								}
							}

						});
			}
		});
	}

	protected boolean shouldShowTestMark() {
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method st
		this.showDialog("提示", "是否上传手机环境设置信息,帮助改善软件功能?", "确认", "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
						if (which == Dialog.BUTTON_POSITIVE) {	
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,CommonUtil.getNetWorkInfo(SwitchServerActivity.this));
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"CellInfo:"+CommonUtil.getCellInfo(SwitchServerActivity.this));
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"CellNearbyInfo:"+CommonUtil.getNearbyCellInfo(SwitchServerActivity.this));
							WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
							WifiInfo wifiInfo = wifiManager.getConnectionInfo();
							  
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"WifiInfo:"+wifiInfo.toString());
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"SSID:"+wifiInfo.getSSID());
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"Android剩余内存:"+CommonUtil.getUnUseMemory(SwitchServerActivity.this)/1024/1024+"M");
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"Android版本信息:"+android.os.Build.MODEL + "," 
								                + android.os.Build.VERSION.SDK + "," 
								                + android.os.Build.VERSION.RELEASE);
							
							Intent mainIntent = new Intent(Intent.ACTION_MAIN,
									null);
							mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							PackageManager mPackageManager = getPackageManager();
							List<ResolveInfo> mAllApps = mPackageManager
									.queryIntentActivities(mainIntent, 0);
							// 按报名排序
							Collections.sort(mAllApps,
									new ResolveInfo.DisplayNameComparator(
											mPackageManager));
							StringBuilder sb = new StringBuilder();
							
							for (ResolveInfo resolveInfo : mAllApps) {
								sb.append(resolveInfo.activityInfo.packageName +",");
							}
							McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_APPLICATION, sb.toString(),true);
							SwitchServerActivity.this
									.showDialog("提示","上传成功!",null);
						}
			}
		});
		}
}
