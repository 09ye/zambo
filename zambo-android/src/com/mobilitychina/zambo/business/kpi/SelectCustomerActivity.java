package com.mobilitychina.zambo.business.kpi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.customer.CustomerFragment;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.SoapService;

/**
 * 查看指定客户的拜访情况
 * 
 * @author chenwang
 * 
 */
public class SelectCustomerActivity extends BaseDetailActivity implements OnItemClickListener, ITaskListener {
	private CustomerFragment contentFragment;
	private String custType;
	private String ownerType;

	private SoapTask getCustomerListTask;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_selectcustomer);

		this.setTitle("查看客户拜访情况");
		contentFragment = (CustomerFragment) this.getFragmentManager().findFragmentById(R.id.content);
		contentFragment.hideTitlebar();
		contentFragment.getListView().setOnItemClickListener(this);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.ownerType = extras.getString("ownerType");
			this.custType = extras.getString("custType");
			if (ownerType.equalsIgnoreCase("A")) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.hide(contentFragment);
				ft.commit();
				this.showProgressDialog("正在加载...");
				getCustomerListTask = SoapService.getAllSiemensCustomersTask(ZamboApplication.getInstance()
						.getApplicationContext(), "A");
				getCustomerListTask.setListener(this);
				getCustomerListTask.start();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CustomerInfo custInfo = (CustomerInfo) contentFragment.getListAdapter().getItem(position);
		if (custInfo != null) {
			Intent intent = new Intent(this, CustomerVisitRateActivity.class);
			intent.putExtra("custId", custInfo.getId());
			intent.putExtra("custType", this.custType);
			intent.putExtra("ownerType", this.ownerType);
			intent.putExtra("custName", custInfo.getCustName());
			startActivity(intent);
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		showErrorAlert();
		getCustomerListTask = null;
	}

	@Override
	public void onTaskFinished(Task task) {
		getCustomerListTask = null;
		dismissDialog();
		SoapObject result = (SoapObject) task.getResult();
		List<CustomerInfo> custInfoList = CustomerInfoManager.getInstance().parseCustomerInfoList(result);
		if (custInfoList == null) {
			showErrorAlert();
			return;
		}
		Map<String, List<CustomerInfo>> groupCustomerMap = new HashMap<String, List<CustomerInfo>>();
		for (CustomerInfo customerInfo : custInfoList) {
			String custType = customerInfo.getCustType();
			if (custType == null || custType.length() == 0) {
				continue;
			}
			List<CustomerInfo> arr = groupCustomerMap.get(custType);
			if (arr == null) {
				arr = new ArrayList<CustomerInfo>();
				groupCustomerMap.put(custType, arr);
			}
			arr.add(customerInfo);
		}
		contentFragment.setAllCustomerList(custInfoList);
		contentFragment.setGroupCustomerList(groupCustomerMap);
		contentFragment.updateListView(CustomerFragment.ALL_CUST_TYPE);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.show(contentFragment);
		ft.commit();
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

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
