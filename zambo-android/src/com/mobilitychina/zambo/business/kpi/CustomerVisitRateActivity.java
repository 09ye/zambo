package com.mobilitychina.zambo.business.kpi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.kpi.KpiFragment.OnKpiListener;
import com.mobilitychina.zambo.service.SoapService;

/**
 * 指定客户的拜访情况
 * 
 * @author chenwang
 * 
 */
public class CustomerVisitRateActivity extends BaseDetailActivity implements ITaskListener {
	private String custType;
	private String ownerType;
	private String custId;
	private String custName;

	private SoapTask getKpiCountTask;
	private KpiFragment webViewKpi;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_customer_visit_rate);
		this.setTitle("指定客户拜访情况");

		webViewKpi = (KpiFragment) this.getFragmentManager().findFragmentById(R.id.webViewKpi);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.custId = extras.getString("custId");
			this.ownerType = extras.getString("ownerType");
			this.custType = extras.getString("custType");
			this.custName = extras.getString("custName");

			refresh(this.custType, this.ownerType);
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (getKpiCountTask != null) {
			getKpiCountTask.cancel(true);
			getKpiCountTask = null;
		}
	}

	public String getCustType() {
		if (custType == null) {
			return "-1";
		}
		return custType;
	}

	public String getOwnerType() {
		if (ownerType == null) {
			return "I";
		}
		return ownerType;
	}

	public String getCustId() {
		if (custId == null) {
			return "-1";
		}
		return custId;
	}

	private void refresh(String custType, String ownerType) {
		this.custType = custType;
		this.ownerType = ownerType;
		this.showProgressDialog("正在加载...");
		getKpiCountTask = SoapService.getKpiCountMess(this.getCustType(), this.getCustId(), this.getOwnerType());
		getKpiCountTask.setListener(this);
		getKpiCountTask.start();
	}

	@Override
	public void onTaskFinished(Task task) {
		if (task == getKpiCountTask) {
			getKpiCountTask = null;
			Object result = task.getResult();
			if (result == null) {
				Toast toast = Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 300);
				toast.show();
				return;
			}
			String[] splitArr = result.toString().split(",");
			if (splitArr.length < 3) {
				this.showErrorAlert();
				return;
			}
			int expect = Integer.parseInt(splitArr[0]);
			int plan = Integer.parseInt(splitArr[1]);
			int actual = Integer.parseInt(splitArr[2]);
			String title = "";
			if ("-1".equals(custId) || custId == null) {
				if (custType.equalsIgnoreCase("-1")) {
					title = "全部类型";
				} else {
					title = custType + "类";
				}
			} else {
				title = custName;
			}

			this.webViewKpi.setOnKpiListener(new OnKpiListener() {
				@Override
				public void onKpiLoadComplete() {
					dismissDialog();
				}

				@Override
				public void onKpiLoadFailed() {
					showErrorAlert();
				}

			});
			this.webViewKpi.showKpi(expect, plan, actual, title);
		}
	}

	@Override
	public void onTaskFailed(Task arg0) {
		this.dismissDialog();
		getKpiCountTask = null;
		this.showErrorAlert();
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {

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
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
