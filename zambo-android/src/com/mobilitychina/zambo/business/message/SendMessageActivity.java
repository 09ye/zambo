package com.mobilitychina.zambo.business.message;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.Statistics;

/**
 * 下达指令
 * @author zywang
 *
 */
public class SendMessageActivity extends BaseDetailActivity implements OnClickListener, ITaskListener{
	private Button mBtnEmployee;
	private ArrayList<SiemensEmpInfo> mTeamSelectedList = new ArrayList<SiemensEmpInfo>();
	private ArrayList<SiemensEmpInfo> mEmployeeSelectedList = new ArrayList<SiemensEmpInfo>();
	private EditText mEditContent;
	private EditText mEditRecever;
	private Task mTaskToLowerLevelV2Task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_sendmessage);
		this.setTitle("下达指令");
		this.getTitlebar().setRightButton("发送", this);
		mBtnEmployee = (Button)this.findViewById(R.id.btnEmployee);
		mBtnEmployee.setOnClickListener(this);
		mEditRecever = (EditText)this.findViewById(R.id.editTextRecever);
		mEditContent = (EditText)this.findViewById(R.id.editTextContent);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(mBtnEmployee == arg0){
			Intent t = new Intent(this,EmploySelectedActivity.class);
			t.putExtra(EmploySelectedActivity.INTENT_SELECTED_EMPLOYEE, mEmployeeSelectedList);
			t.putExtra(EmploySelectedActivity.INTENT_SELECTED_TEAM, mTeamSelectedList);
			this.startActivityForResult(t,1);
		}else if (arg0 == this.getTitlebar().getRightButton()){
			if(this.mEditRecever.getText().length()==0){
				this.showDialog("提示", "请选择收件人", null);
			}else if(this.mEditContent.getText().length()==0){
				this.showDialog("提示", "请输入指令内容", null);
			}else{
				String teamPosId = "";
				String userPosId = "";
				for (SiemensEmpInfo info : mTeamSelectedList) {
					teamPosId = 
							(teamPosId.length() == 0 ? "":
							teamPosId + ",")
							+ info.getId();
				}
				for (SiemensEmpInfo info :mEmployeeSelectedList ) {
					userPosId = 
							(userPosId.length() == 0 ? "":
								userPosId + ",")
							+ info.getId();
				}
				mTaskToLowerLevelV2Task = SoapService.instructionToLowerLevelV2(this.mEditContent.getText().toString(), teamPosId, userPosId);
				mTaskToLowerLevelV2Task.setListener(this);
				mTaskToLowerLevelV2Task.start();
				this.showProgressDialog("正在发送");
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			mEmployeeSelectedList = (ArrayList<SiemensEmpInfo>) data
					.getSerializableExtra(EmploySelectedActivity.INTENT_SELECTED_EMPLOYEE);
			mTeamSelectedList = (ArrayList<SiemensEmpInfo>) data
					.getSerializableExtra(EmploySelectedActivity.INTENT_SELECTED_TEAM);
			this.mEditRecever.setText("");
			for (SiemensEmpInfo info : mEmployeeSelectedList) {
				this.mEditRecever.setText(
						(this.mEditRecever.getText().length() == 0 ? "":
						this.mEditRecever.getText() + ",")
						+ info.getPosName());
			}
			for (SiemensEmpInfo info : mTeamSelectedList) {
				this.mEditRecever.setText((this.mEditRecever.getText().length() == 0 ? "":
					this.mEditRecever.getText() + ",")
						+ info.getPosName() + "'s team");
			}
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
		if(arg0.getResult() != null){
			if(arg0.getResult().toString().equalsIgnoreCase("1")){
				this.showDialog("提示", "发送已完成", null);
				this.mEditContent.setText("");
				this.mEditRecever.setText("");
				Statistics.sendEvent("message_send", "send", "",( long )0);
			}else{
				this.showDialog("提示", "发送失败,请稍后再试。", null);
			}
		}
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
