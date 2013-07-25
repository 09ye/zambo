package com.mobilitychina.zambo.business.more;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.ConfigHelper;
import com.mobilitychina.zambo.util.ConfigState;
import com.mobilitychina.zambo.util.Version;
import com.mobilitychina.zambo.util.VersionUpdate;

/**
 * 
 * @author zywang
 * 
 */
public class VersionActivity extends BaseDetailActivity implements OnClickListener {
	// private Button btnUpdate;
	private TextView mVersion;
	private TextView mVersionDate;
	private TextView serverVersion;
	private TextView serverVersionDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_version);
		this.setTitle("版本信息");
		this.getTitlebar().setRightButton("更新", this);
		
		mVersion = (TextView) findViewById(R.id.mVersion);
		mVersionDate = (TextView) findViewById(R.id.mVersionDate);
		serverVersion = (TextView) findViewById(R.id.serverVersion);
		serverVersionDate = (TextView) findViewById(R.id.serverVersionDate);

		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			// nowCode = info.versionCode;
			mVersion.setText("目前版本号                    " + info.versionName);
			mVersionDate.setText("目前版本发布时间        " + ConfigDefinition.DATE_APP_DISTRIBUTE);
			serverVersion
					.setText("服务器版本号                " + ConfigHelper.getInstance().getNewVersion().toString());
			serverVersionDate.setText("服务器版本发布时间    " + ConfigHelper.getInstance().getUpdateDate());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VersionActivity getInstance() {

		return this;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		sendEvent("version", "update", "", 0);
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			Version nowVersion = new Version(info.versionName);
			if (ConfigHelper.getInstance().getMinVersion().isNewer(nowVersion)
					|| ConfigHelper.getInstance().getNewVersion().isNewer(nowVersion)) {
				// if (updateView == null) {

				new VersionUpdate(getInstance());
				// }
			} else {
				if(ConfigHelper.getInstance().getState() == ConfigState.Fail)
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(getInstance()).setTitle("提示")
							.setMessage("更新配置文件失败，请稍后再试。").setPositiveButton("确认", null);
					alert.show();
				}else{
					AlertDialog.Builder alert = new AlertDialog.Builder(getInstance()).setTitle("提示")
							.setMessage("亲爱的用户，您使用的是最新版本。").setPositiveButton("确认", null);
					alert.show();
				}
				
			}

		} catch (Throwable e) {

		}
	}

}
