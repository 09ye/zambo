package com.mobilitychina.zambo.business.kpi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.service.SoapService;

/**
 * 拜访覆盖率
 * 
 * @author wchen
 * 
 */
public class CoverRateActivity extends BaseDetailActivity implements ITaskListener {
	private String custType;
	private String ownerType;

	private SoapTask getCoverRateTsak;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coverrate);

		Intent intent = getIntent();
		custType = intent.getStringExtra("custType");
		ownerType = intent.getStringExtra("ownerType");
		if (ownerType == null || ownerType.length() == 0) {
			ownerType = "I";
		}

		if (ownerType.equalsIgnoreCase("I")) {
			this.setTitle("我的拜访覆盖率");
		} else {
			this.setTitle("团队拜访覆盖率");
		}
		this.showProgressDialog("正在加载...");
		getCoverRateTsak = SoapService.getCoverRate(custType, ownerType);
		getCoverRateTsak.setListener(this);
		getCoverRateTsak.start();
	}

	public void onDestroy() {
		super.onDestroy();
		if (getCoverRateTsak != null) {
			getCoverRateTsak.cancel(true);
			getCoverRateTsak = null;
		}
	}

	private void showErrorAlert() {
		dismissDialog();
		this.showDialog("提示", "网络出错，请稍后重试", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		getCoverRateTsak = null;
		this.showErrorAlert();
	}

	@Override
	public void onTaskFinished(Task task) {
		getCoverRateTsak = null;
		Object result = task.getResult();
		if (result == null) {
			Toast toast = Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 300);
			toast.show();
			return;
		}

		String[] splitArr = result.toString().split(",");
		if (splitArr.length < 2) {
			this.showErrorAlert();
			return;
		}
		int finish = Integer.parseInt(splitArr[0]);
		int unfinish = Integer.parseInt(splitArr[1]);
		String title = "";
		if (custType.equalsIgnoreCase("-1")) {
			title = "全部类型";
		} else {
			title = custType + "类";
		}
		String data = "window.chartdata = { " + "title: '" + title + "'," + "finish: " + finish + "," + "unfinish: "
				+ unfinish + "," + "};";
		File jsFile = new File("/data/data/com.mobilitychina.siemens/data02.js");
		if (jsFile.exists()) {
			jsFile.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(jsFile);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			this.showErrorAlert();
			return;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}

		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dismissDialog();
			}
		});
		webView.setBackgroundColor(0x00000000);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearCache(true);
		webView.loadUrl("file:///android_asset/chart02.html");
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
