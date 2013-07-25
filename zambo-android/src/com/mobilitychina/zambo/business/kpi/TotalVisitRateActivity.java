package com.mobilitychina.zambo.business.kpi;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerType;
import com.mobilitychina.zambo.business.kpi.KpiFragment.OnKpiListener;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.service.CustomerInfoManager.CustomerLoadStatus;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.widget.CustomPopupWindow;

/**
 * 总的客户累计拜访率
 * 
 * @author chenwang
 * 
 */
public class TotalVisitRateActivity extends BaseDetailActivity implements OnClickListener, ITaskListener {
	private BroadcastReceiver mReceiver;

	private Button btnMyKpi; // 我的拜访情况
	private Button btnTeamKpi; // 团队拜访情况
	private Button btnCoverRate; // 客户覆盖率
	private Button btnCustomerKpi; // 指定客户的拜访情况
	private KpiFragment webViewKpi;
	private ImageButton btnSelectType;
	private String custType;
	private String ownerType;
	private String custId;
	private String custName;

	private SoapTask getKpiCountTask;
	private SoapTask getCustTypeTask;

	private List<CustomerType> myCustTypeList;
	private List<CustomerType> teamCustTypeList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_totalvisitrate);
		this.setTitle("年度累计拜访情况");

		btnMyKpi = (Button) this.findViewById(R.id.btnMyKpi);
		btnMyKpi.setOnClickListener(this);
		btnTeamKpi = (Button) this.findViewById(R.id.btnTeamKpi);
		btnTeamKpi.setOnClickListener(this);
		btnCoverRate = (Button) this.findViewById(R.id.btnCoverRate);
		btnCoverRate.setOnClickListener(this);
		btnCoverRate.setVisibility(View.GONE);
		btnCustomerKpi = (Button) this.findViewById(R.id.btnCustomerKpi);
		btnCustomerKpi.setOnClickListener(this);
		btnCustomerKpi.setVisibility(View.GONE);

		webViewKpi = (KpiFragment) this.getFragmentManager().findFragmentById(R.id.webViewKpi);

		btnSelectType = new ImageButton(this);
		btnSelectType.setImageResource(R.drawable.btn_dropdownlist);
		btnSelectType.setBackgroundColor(0x00ffffff);
		LayoutParams layoutParams = new LayoutParams(65, 65);
		btnSelectType.setPadding(0, 0, 30, 0);
		btnSelectType.setLayoutParams(layoutParams);
		this.getTitlebar().setRightCustomeView(btnSelectType);

		btnSelectType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<CustomerType> customerTypeList = null;
				if (ownerType.equalsIgnoreCase("I")) {
					customerTypeList = myCustTypeList;
				} else {
					customerTypeList = teamCustTypeList;
				}
				if (customerTypeList != null) {
					pickCustomerType(customerTypeList);
				} else {
					showProgressDialog("正在请求客户类型...");
					getCustTypeTask = SoapService.getSiemensCustType(ownerType);
					getCustTypeTask.setListener(TotalVisitRateActivity.this);
					getCustTypeTask.start();
					
				}
			}
		});
		if (CustomerInfoManager.getInstance().getCustomerTypeStatus() == CustomerLoadStatus.LOADING) {
			showProgressDialog("正在加载...");
			if (mReceiver == null) {
				mReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						if (CustomerInfoManager.getInstance().getCustomerTypeStatus() == CustomerLoadStatus.FAILED) {
							showDialog("提示", "网络错误，请稍后重试", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});
							return;
						}
						onCustTypeLoadComplete();
					}
				};
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERTYPECOMPLETE);
				this.registerReceiver(mReceiver, filter);
			}
		} else {
			this.onCustTypeLoadComplete();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
		}
		if (getKpiCountTask != null) {
			getKpiCountTask.cancel(true);
			getKpiCountTask = null;
		}
		if (getCustTypeTask != null) {
			getCustTypeTask.cancel(true);
			getCustTypeTask = null;
		}
	}

	private void pickCustomerType(final List<CustomerType> customerTypeList) {
		List<String> data = new ArrayList<String>();
		data.add("年度累计拜访情况");
		for (CustomerType custType : customerTypeList) {
			data.add(custType.getDescription() + "(" + custType.getCustomerCount() + "个)");
		}
		CustomPopupWindow popup = new CustomPopupWindow(TotalVisitRateActivity.this, data);
		popup.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
			@Override
			public void onItemClick(int position, String data) {
				if (position == 0) {
					refresh("-1", ownerType);
				} else {
					CustomerType selectedCustType = customerTypeList.get(position - 1);
					refresh(selectedCustType.getName(), ownerType);
					
					sendEvent("yearvisit", "customerchanged", "", 0);
//					System.out.println("客户切换------>>>>");
					
				}
				setTitle(data);
			}
		});
		popup.show(btnSelectType, 360, 0);
	}

	private void onCustTypeLoadComplete() {
		CustomerType totalType = null;
		myCustTypeList = CustomerInfoManager.getInstance().getCustomerTypeList();
		for (CustomerType ct : myCustTypeList) {
			if (ct.isAllType()) {
				totalType = ct;
			}
		}
		if (totalType == null || totalType.getCustomerCount() > 0) {
			this.btnMyKpi.setVisibility(View.VISIBLE);
		} else {
			this.btnMyKpi.setVisibility(View.GONE);
		}
		if (UserInfoManager.getInstance().isLeader()) {
			this.btnTeamKpi.setVisibility(View.VISIBLE);
		} else {
			this.btnTeamKpi.setVisibility(View.GONE);
		}
		refresh("-1", "I");
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
		if (ownerType.equalsIgnoreCase("I")) {
			this.btnMyKpi.setSelected(true);
			this.btnTeamKpi.setSelected(false);
		} else {
			this.btnMyKpi.setSelected(false);
			this.btnTeamKpi.setSelected(true);
		}
		if (this.custType.equalsIgnoreCase("-1")) {
			this.btnMyKpi.setVisibility(View.VISIBLE);
			this.btnTeamKpi.setVisibility(View.VISIBLE);
		} else {
			this.btnMyKpi.setVisibility(View.GONE);
			this.btnTeamKpi.setVisibility(View.GONE);
		}

		this.showProgressDialog("正在加载...");
		getKpiCountTask = SoapService.getKpiCountMess(this.getCustType(), this.getCustId(), this.getOwnerType());
		getKpiCountTask.setListener(this);
		getKpiCountTask.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnMyKpi:

			sendEvent("yearvisit", "myvisit", "", 0);
//			System.out.println("个人拜访------>>>>");

			refresh(custType, "I");
			break;
		case R.id.btnTeamKpi:
			
			sendEvent("yearvisit", "teamvisit", "", 0);
//			System.out.println("团队拜访------>>>>");

			refresh(custType, "A");
			break;
		case R.id.btnCoverRate: {
			Intent intent = new Intent(this, CoverRateActivity.class);
			intent.putExtra("custType", this.custType);
			intent.putExtra("ownerType", this.ownerType);
			this.startActivity(intent);
			
			sendEvent("yearvisit", "coveragevisit", "", 0);
//			System.out.println("覆盖率------>>>>");

		}
			break;
		case R.id.btnCustomerKpi: {
			Intent intent = new Intent(this, SelectCustomerActivity.class);
			intent.putExtra("custType", this.custType);
			intent.putExtra("ownerType", this.ownerType);
			this.startActivity(intent);
			
			sendEvent("yearvisit", "specialvisit", "", 0);
//			System.out.println("指定拜访------>>>>");
			
		}
			break;
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
		if (task == getKpiCountTask) {
			getKpiCountTask = null;
		} else if (task == getCustTypeTask) {
			getCustTypeTask = null;
		}
		this.showErrorAlert();
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

			// if ("-1".equals(custId) || custId == null) {
			// if (custType.equalsIgnoreCase("-1")) {
			// title = "全部类型";
			// } else {
			// title = custType + "类";
			// }
			// } else {
			// title = custName;
			// }

			this.webViewKpi.setOnKpiListener(new OnKpiListener() {
				@Override
				public void onKpiLoadComplete() {
					dismissDialog();
					btnCoverRate.setVisibility(View.VISIBLE);
					btnCustomerKpi.setVisibility(View.VISIBLE);
				}

				@Override
				public void onKpiLoadFailed() {
					showErrorAlert();
				}

			});
			this.webViewKpi.showKpi(expect, plan, actual, title);
		} else if (getCustTypeTask == task) {
			dismissDialog();
			List<CustomerType> customerTypeList = CustomerInfoManager.getInstance().parseCustomerType(
					(SoapObject) task.getResult());
			if (customerTypeList == null) {
				this.showErrorAlert();
				return;
			}
			if (ownerType.equalsIgnoreCase("I")) {
				if (myCustTypeList == null) {
					myCustTypeList = new ArrayList<CustomerType>();
				}
				myCustTypeList.clear();
				myCustTypeList.addAll(customerTypeList);
				pickCustomerType(myCustTypeList);
			} else {
				if (teamCustTypeList == null) {
					teamCustTypeList = new ArrayList<CustomerType>();
				}
				teamCustTypeList.clear();
				teamCustTypeList.addAll(customerTypeList);
				pickCustomerType(teamCustTypeList);
			}

		}
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
