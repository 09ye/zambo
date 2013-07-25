package com.mobilitychina.zambo.business.customer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.service.SoapService;

/**
 * 汇报
 * 
 * @author chenwang
 * 
 */
public class ReportActivity extends BaseDetailActivity implements ITaskListener {
	private SoapTask getReportEmpTask;
	private SoapTask sendTask;

	private List<NameValuePair> reportEmpList; // （名称，手机号）
	private List<CheckBox> reportEmpCheckboxList;

	private LinearLayout layoutEmp;
	private EditText etContent;
//	private TextView custName;
	
	private String custDetailId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_report);

		this.layoutEmp = (LinearLayout) this.findViewById(R.id.layoutEmp);
		this.etContent = (EditText) this.findViewById(R.id.etContent);		
		custDetailId = this.getIntent().getExtras().getString("custDetailId");
		this.etContent.setText(this.getIntent().getExtras().getString("content", ""));

		System.out.println("所传输过来的内容为--——--?>>>>>"+this.getIntent().getExtras().getString("content", ""));
		System.out.println("将要汇报的医院名称------>>"+this.getIntent().getExtras().getString("custName"));
		etContent.setText(this.getIntent().getExtras().getString("custName")+":"+this.getIntent().getExtras().getString("content", ""));

		final String custName = this.getIntent().getExtras().getString("custName");
		if (!TextUtils.isEmpty(custName)) {
			etContent.setText(custName + ":" + this.getIntent().getExtras().getString("content", ""));
		}
		
		this.setTitle("汇  报");

		this.getTitlebar().setRightButton("发送", new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = etContent.getText().toString();
				String tempContent = "";
				if(!TextUtils.isEmpty(custName)){
					if(!TextUtils.isEmpty(content) && content.startsWith(custName+":")){
						tempContent = content.substring(custName.length() + 1);
					}
				}
				if (content.length() == 0 || tempContent.length() == 0) {
					sendEvent("customer_detail", "reportContent_sending", "您还没输入任何汇报内容", 0);
//					System.out.println("还没输入内容-----》》》》");
					showDialog("提示", "您还没输入任何汇报内容", null);
					return;
				}
				final String uploadConent = tempContent;
				final List<NameValuePair> selectedEmpList = new ArrayList<NameValuePair>();
				for (int i = 0; i < reportEmpList.size(); i++) {
					CheckBox cb = reportEmpCheckboxList.get(i);
					if (cb.isChecked()) {
						selectedEmpList.add(reportEmpList.get(i));
					}
				}
				if (selectedEmpList.size() == 0) {
					showDialog("提示", "请至少选择一个汇报对象", null);
					sendEvent("customer_detail", "reportContent_sending", "请至少选择一个汇报对象", 0);
//					System.out.println("还没输入内容-----》》》》");
					return;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("汇报对象：\n");
				for (NameValuePair nameValuePair : selectedEmpList) {
					sb.append(nameValuePair.getName());
					sb.append("\n");
				}
				sb.append("\n汇报内容：\n");
				sb.append(content);
				showDialog("", sb.toString(), "发送", "取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE) {
							return;
						}
						
						StringBuffer sb = new StringBuffer();
						for (NameValuePair nameValuePair : selectedEmpList) {
							sb.append(nameValuePair.getValue());
							sb.append(",");
						}
						sb.deleteCharAt(sb.length() - 1);

						showProgressDialog("正在发送...");
						sendTask = SoapService.insertSiemensUpload(uploadConent, "", "", "", custDetailId, "2",
								sb.toString(), "", "");
						sendTask.setListener(ReportActivity.this);
						sendTask.start();
						//System.out.println("所发送的消息------->>>>"+content+"\n"+custDetailId);
					}
				});

			}
		});

		
		this.showProgressDialog("正在获取汇报对象...");
		getReportEmpTask = SoapService.getReportEmpsTask();
		getReportEmpTask.setListener(this);
		getReportEmpTask.start();
	}

	private void updateUI() {
		if (reportEmpCheckboxList == null) {
			reportEmpCheckboxList = new ArrayList<CheckBox>();
		}
		for (NameValuePair pair : reportEmpList) {
			CheckBox cb = new CheckBox(this);
			cb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			cb.setText(pair.getName());
			cb.setButtonDrawable(R.drawable.check_btn);
			reportEmpCheckboxList.add(cb);

			layoutEmp.addView(cb);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (sendTask != null) {
			sendTask.cancel(true);
			sendTask = null;
		}
		if (getReportEmpTask != null) {
			getReportEmpTask.cancel(true);
			getReportEmpTask = null;
		}
	}

	private void showErrDialog() {
		this.showDialog("提示", "网络异常，请稍后重试...", null);
	}

	@Override
	public void onTaskFailed(Task task) {
		dismissDialog();
		if (task == getReportEmpTask) {
			getReportEmpTask = null;
		} else if (task == sendTask) {
			sendTask = null;
		}
		this.showErrDialog();
	}

	@Override
	public void onTaskFinished(Task task) {
		dismissDialog();
		if (task == getReportEmpTask) {
			getReportEmpTask = null;
			String result = task.getResult().toString();
			if (result == null) {
				this.showErrDialog();
				return;
			}
			String[] str = result.split("/");
			if (reportEmpList == null) {
				reportEmpList = new ArrayList<NameValuePair>();
			}

			for (String string : str) {
				Log.i("ReportActivity",string);
				String[] temp = string.split("&");
				if(temp.length>1){
					reportEmpList.add(new BasicNameValuePair(temp[0], temp[1]));
				}
			}
			updateUI();
		} else if (task == sendTask) {
			sendTask = null;
			String result = task.getResult().toString();
			if (result == null) {
				this.showErrDialog();
				return;
			}
			String[] rr = result.split("@");
			if ("true".equals(rr[0])) {
				this.showDialog("提示", "发送成功", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						sendEvent("customer_detail", "reportContent_sending", "发送成功", 0);
//						System.out.println("发送成功-----》》》》");
					}
				});
			} else {
				this.showDialog("提示", "发送失败，请稍后重试", null);
				sendEvent("customer_detail", "reportContent_sending", "发送失败，请稍后重试", 0);
//				System.out.println("请稍后再试-----》》》》");
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
