package com.mobilitychina.zambo.business.customer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilitychina.common.base.BaseFragment;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.record.FollowupListFragment;
import com.mobilitychina.zambo.business.record.ProjectDetailActivity;
import com.mobilitychina.zambo.business.record.ProjectListFragment;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.widget.CustomImageButton;

public class CustomerDetailFragment extends BaseFragment{
	private CustomerInfo custInfo;
	private String customerId;
	private String planId; // 计划ID

	private ProjectListFragment projectRecordListFragment;
	private FollowupListFragment followupListFragment;
	private Button btnProjectRecordList;
	private Button btnFollowupList;

	private int REQUEST_CODE_ADDPROJECT = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
       View view = inflater.inflate(R.layout.fragment_customer_detail, container, false);
		customerId = this.getActivity().getIntent().getExtras().getString("customerId");
		planId = this.getActivity().getIntent().getExtras().getString("planId");
		custInfo = CustomerInfoManager.getInstance().getCustomerById(customerId);
		if (custInfo == null) {
			getActivity().setTitle("客户信息");
		} else {
			this.getActivity().setTitle(custInfo.getCustName());
			//System.out.println("医院名称------》》"+custInfo.getCustName());
		}
		if (custInfo != null) {
			ImageView custTypeImageView = new ImageView(this.getActivity());
			if (custInfo.getCustType().equalsIgnoreCase("C1")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c1);
			} else if (custInfo.getCustType().equalsIgnoreCase("C2")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c2);
			} else if (custInfo.getCustType().equalsIgnoreCase("C3")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c3);
			} else if (custInfo.getCustType().equalsIgnoreCase("C4")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_c4);
			} else if (custInfo.getCustType().equalsIgnoreCase("S")) {
				custTypeImageView.setImageResource(R.drawable.icon_flag_s);
			}
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			custTypeImageView.setPadding(0, -3, 15, 0);
			custTypeImageView.setLayoutParams(layoutParams);
//		   ((BaseDetailActivity) this.getActivity()).getTitlebar().setRightCustomeView(custTypeImageView);
		}

		CustomImageButton btnReport = (CustomImageButton) view.findViewById(R.id.btnReport);
		btnReport.setImageResource(R.drawable.icon_report);
		btnReport.setText("汇报");
		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ReportActivity.class);
				intent.putExtra("custDetailId", String.valueOf(custInfo.getCustDetailId()));
				intent.putExtra("custName", custInfo.getCustName());
				startActivity(intent);
			
				((BaseActivity) getActivity()).sendEvent("customer_detail", "hospital_report", "", 0);
//				System.out.println("汇报项-----》》》》");
				
			}
		});
		CustomImageButton btnShare = (CustomImageButton) view.findViewById(R.id.btnShare);
		btnShare.setImageResource(R.drawable.icon_share);
		btnShare.setText("分享");
		btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra("custDetailId", String.valueOf(custInfo.getCustDetailId()));
				intent.putExtra("custName", custInfo.getCustName());
				startActivity(intent);
				
				((BaseActivity) getActivity()).sendEvent("customer_detail", "hospital_sharing", "", 0);
//				System.out.println("分享项-----》》》》");
				
			}
		});

		if (planId != null && planId.length() > 0) { // 当有计划ID时，显示按钮"新建"，表明可以新建项目
			CustomImageButton btnAddProject = (CustomImageButton) view.findViewById(R.id.btnAddProject);
			btnAddProject.setVisibility(View.VISIBLE);
			btnAddProject.setImageResource(R.drawable.icon_update);
			btnAddProject.setText("新建");
			btnAddProject.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
					intent.putExtra("detailType", ProjectDetailActivity.DETAIL_TYPE_INSERT);
					intent.putExtra("customerId", CustomerDetailFragment.this.customerId);
					intent.putExtra("planId", CustomerDetailFragment.this.planId);
					CustomerDetailFragment.this.startActivityForResult(intent, REQUEST_CODE_ADDPROJECT);
					
					((BaseActivity) getActivity()).sendEvent("customer_detail", "create_project", "", 0);
//					System.out.println("新建项目-----》》》》");
					McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PROJECT,"新建项目");
				}
			});
		}

		if (custInfo != null && custInfo.getCustAddress() != null && custInfo.getCustAddress().length() > 0) {
			((TextView) view.findViewById(R.id.tvAddress)).setText(custInfo.getCustAddress());
		} else {
			((TextView) view.findViewById(R.id.tvAddress)).setText("暂无地址信息");
		}

		if (custInfo != null && custInfo.getNextPlanVisitDate().length() >= 10) {
			 try {  
                     //SimpleDateFormat  sdf  =  new  SimpleDateFormat("yyyy-MM-dd");  
                     //String result  =  sdf.format(new Date(custInfo.getNextPlanVisitDate())); 
                     ((TextView)view.findViewById(R.id.tvNextPlanTime)).setText(custInfo.getNextPlanVisitDate().substring(0, 10));
             }  
             catch(Exception  ex)  {  
                    // LOGGER.info("date:"  +  date);  
                     ex.printStackTrace();  
             }  
			
			
		} else {
			((TextView) view.findViewById(R.id.tvNextPlanTime)).setText("暂无计划");
		}
//		projectRecordListFragment = (ProjectListFragment) getFragmentManager().findFragmentById(R.id.fragmentProjectRecordList);
		projectRecordListFragment = new ProjectListFragment();
		getFragmentManager().beginTransaction().add(R.id.fragmentProjectRecordList, projectRecordListFragment).commit();
		projectRecordListFragment.setCustomerId(customerId);
		
//		followupListFragment = (FollowupListFragment) getFragmentManager().findFragmentById(
//				R.id.fragmentFollowupList);
		followupListFragment = new FollowupListFragment();
		getFragmentManager().beginTransaction().add(R.id.fragmentFollowupList, followupListFragment).commit();
		followupListFragment.setCustomerId(customerId);
		if (planId == null|| planId.length() == 0){
			projectRecordListFragment.setUpdateable(false);
		}else{
			projectRecordListFragment.setUpdateable(true);
		}

		btnProjectRecordList = (Button) view.findViewById(R.id.btnProjectRecordList);
		btnProjectRecordList.setOnClickListener(tabListener);
		btnFollowupList = (Button) view.findViewById(R.id.btnFollowupList);
		btnFollowupList.setOnClickListener(tabListener);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ADDPROJECT) {
			if (btnProjectRecordList.isSelected()) {
				projectRecordListFragment.refresh();
			}
		}
	}

	private void onTabChanged(View v) {
		if (v == btnFollowupList) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.show(followupListFragment);
			ft.hide(projectRecordListFragment);
			ft.commit();
			btnFollowupList.setSelected(true);
			btnProjectRecordList.setSelected(false);
			followupListFragment.refresh();
			((BaseActivity) getActivity()).sendEvent("customer_detail", "customer_followupRecord", "", 0);
//System.out.println("随访-----》》》》");
			
		} else {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.hide(followupListFragment);
			ft.show(projectRecordListFragment);
			ft.commit();

			btnFollowupList.setSelected(false);
			btnProjectRecordList.setSelected(true);
			projectRecordListFragment.refresh();
			
			((BaseActivity) getActivity()).sendEvent("customer_detail", "project_list", "", 0);
//			System.out.println("列表项-----》》》》");

		}
	}

	private View.OnClickListener tabListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onTabChanged(v);
		}
	};

	public void onResume() {
		super.onResume();
		this.onTabChanged(btnProjectRecordList);
	}
}
