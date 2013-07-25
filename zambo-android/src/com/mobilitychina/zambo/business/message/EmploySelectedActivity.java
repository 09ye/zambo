package com.mobilitychina.zambo.business.message;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;


import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.EmployeeSelectedItem;


public class EmploySelectedActivity extends BaseDetailListActivity implements OnClickListener, ITaskListener, OnItemClickListener {

	public static final String INTENT_SELECTED_EMPLOYEE = "employee";
	public static final String INTENT_SELECTED_TEAM = "team";
	private Task mTaskTeam;
	private Task mTaskEmployee;
	private Button mBtnTeam;
	private Button mBtnEmployee;
	private EmployeeAdapter mEmployeeAdatper;
	private TeamAdapter mTeamAdapter;
	private ArrayList<SiemensEmpInfo> mTeamList;
	private ArrayList<SiemensEmpInfo> mTeamSelectedList = new ArrayList<SiemensEmpInfo>();
	private ArrayList<SiemensEmpInfo> mEmployeeList;
	private ArrayList<SiemensEmpInfo> mEmployeeSelectedList = new ArrayList<SiemensEmpInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_employselected);
		this.setTitle("选择收件人");
		this.getTitlebar().setRightButton("确定",this);
		Intent intent = this.getIntent();
		mEmployeeSelectedList = (ArrayList<SiemensEmpInfo>)intent.getSerializableExtra(EmploySelectedActivity.INTENT_SELECTED_EMPLOYEE);
		mTeamSelectedList = (ArrayList<SiemensEmpInfo>)intent.getSerializableExtra(EmploySelectedActivity.INTENT_SELECTED_TEAM);
		mBtnTeam = (Button)this.findViewById(R.id.btnTeam);
		mBtnTeam.setOnClickListener(this);
		mBtnEmployee = (Button)this.findViewById(R.id.btnEmployee);
		mBtnEmployee.setOnClickListener(this);
		mBtnTeam.setSelected(true);
		mTaskTeam = SoapService.getLowerTeamTask("2");
		mTaskTeam.setListener(this);
		mTaskTeam.start();
		mTaskEmployee = SoapService.getLowerUser();
		mTaskEmployee.setListener(this);
		mTaskEmployee.start();
		this.showProgressDialog("请求数据");
		this.getListView().setOnItemClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mBtnTeam){
			mBtnTeam.setSelected(true);
			mBtnEmployee.setSelected(false);
			this.setListAdapter(mTeamAdapter);
			Statistics.sendEvent("employee", "team", "",( long )0);
		}else if (v == mBtnEmployee){
			mBtnTeam.setSelected(false);
			mBtnEmployee.setSelected(true);
			this.setListAdapter(mEmployeeAdatper);
			Statistics.sendEvent("employee", "person", "",( long )0);
		}else if (v == this.getTitlebar().getRightButton()){
			Intent inter = new Intent();
			inter.putExtra(INTENT_SELECTED_EMPLOYEE, this.mEmployeeSelectedList);
			inter.putExtra(INTENT_SELECTED_TEAM, this.mTeamSelectedList);
			this.setResult(RESULT_OK, inter);
			Statistics.sendEvent("employee", "submit", "",( long )0);
			this.finish();
		}
	}
	@Override
	public void onTaskFailed(Task arg0) {
		// TODO Auto-generated method stub
		this.dismissDialog();
	}
	@Override
	public void onTaskFinished(Task arg0) {
		// TODO Auto-generated method stub
		this.dismissDialog();
		if(arg0 == mTaskEmployee){
			mEmployeeList =  new ArrayList<SiemensEmpInfo>(RespFactory.getService().fromResp(
					SiemensEmpInfo.class, arg0.getResult())) ;
			mEmployeeAdatper = new EmployeeAdapter();
			//this.setListAdapter(mEmployeeAdatper);
		}else if (arg0 == mTaskTeam){
			mTeamList =  new ArrayList<SiemensEmpInfo>(RespFactory.getService().fromResp(
					SiemensEmpInfo.class, arg0.getResult()));
			mTeamAdapter = new TeamAdapter();
			this.setListAdapter(mTeamAdapter);
			//anyType{SiemensUsers=anyType{deviceToken=null; email=null; isParent=N; isTeamParent=383; level=1; mac=null; mobile=13100000002; parentPosId=381; passWd=null; posId=383; posName=springB1; pwdUpdate=null; type=null; userId=346; userName=springB198; }; SiemensUsers=anyType{deviceToken=null; email=null; isParent=N; isTeamParent=385; level=2; mac=null; mobile=13100000004; parentPosId=383; passWd=null; posId=385; posName=springC1; pwdUpdate=null; type=null; userId=347; userName=springC1; }; SiemensUsers=anyType{deviceToken=null; email=null; isParent=N; isTeamParent=null; level=1; mac=null; mobile=13100000002; parentPosId=382; passWd=null; posId=384; posName=springB2; pwdUpdate=null; type=null; userId=346; userName=springB198; }; }
		}
	}
	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	private class TeamAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTeamList.size();
		}

		@Override
		public SiemensEmpInfo getItem(int position) {
			// TODO Auto-generated method stub
			return mTeamList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			EmployeeSelectedItem view ;
			SiemensEmpInfo curInfo =this.getItem(arg0);
			if (convertView == null) {
				view = (EmployeeSelectedItem) LayoutInflater.from(EmploySelectedActivity.this).inflate(
						R.layout.item_employee_selected, null);
			} else {
				view = (EmployeeSelectedItem) convertView;
			}
			view.setTitle(curInfo.getPosName()+"'s team");
			if(Integer.parseInt(curInfo.getLevel()) > 1){
				view.setTitle("  --"+this.getItem(arg0).getPosName()+"'s team");
			
			}
			if(mTeamSelectedList.contains(curInfo)){
				view.setChecked(true);
			}else{
				view.setChecked(false);
			}
			if(arg0 < this.getCount()-1){
				SiemensEmpInfo info = this.getItem(arg0 + 1);
				if(info.getParentPos().equals(curInfo.getParentPos())||
					info.getParentPos().equalsIgnoreCase(curInfo.getId())	){
					view.setSeperator(View.GONE);
				}else{
					view.setSeperator(View.VISIBLE);
				}
			}
			//view.setEmployeeeInfo(this.getItem(arg0));
			return view;
		}
		
	}
	private class EmployeeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mEmployeeList.size();
		}

		@Override
		public SiemensEmpInfo getItem(int arg0) {
			// TODO Auto-generated method stub
			return mEmployeeList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			EmployeeSelectedItem view ;
			if (convertView == null) {
				view = (EmployeeSelectedItem) LayoutInflater.from(EmploySelectedActivity.this).inflate(
						R.layout.item_employee_selected, null);
			} else {
				view = (EmployeeSelectedItem) convertView;
			}
			if(mEmployeeSelectedList.contains(this.getItem(arg0))){
				view.setChecked(true);
			}else{
				view.setChecked(false);
			}
			view.setEmployeeeInfo(this.getItem(arg0));
			return view;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(this.getListAdapter() == mTeamAdapter){
			SiemensEmpInfo info = mTeamList.get(arg2);
			if(mTeamSelectedList.contains(info)){
				mTeamSelectedList.remove(info);
				for (SiemensEmpInfo iterable : mTeamList) {
					if(iterable.getParentPos().equalsIgnoreCase(info.getId())){
						mTeamSelectedList.remove(iterable);
					}
				}
			}else{
				mTeamSelectedList.add(info);
				for (SiemensEmpInfo iterable : mTeamList) {
					if(iterable.getParentPos().equalsIgnoreCase(info.getId())){
						mTeamSelectedList.add(iterable);
					}
				}
			}
			mTeamAdapter.notifyDataSetChanged();
		}else{
			SiemensEmpInfo info = mEmployeeList.get(arg2);
			if(mEmployeeSelectedList.contains(info)){
				mEmployeeSelectedList.remove(info);
			}else{
				mEmployeeSelectedList.add(info);
			}
			mEmployeeAdatper.notifyDataSetChanged();
		}
	}
	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
