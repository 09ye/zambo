package com.mobilitychina.zambo.business.customer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.service.SoapService;

/**
 * 分享
 * 
 * @author chenwang
 * 
 */
public class ShareActivity extends BaseDetailActivity implements ITaskListener {
	private EditText etContent;
	private String custDetailId;
	private SoapTask sendTask;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

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
		
		this.setTitle("分  享");
		this.getTitlebar().setRightButton("分享", new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = etContent.getText().toString();
				String tempContent = "";
				if(!TextUtils.isEmpty(custName)){
					if(!TextUtils.isEmpty(content) && content.startsWith(custName+":")){
						tempContent = content.substring(custName.length() + 1);
					}
				}
				if (content.length() == 0||tempContent.length() == 0) {
					
					sendEvent("customer_detail", "shareContent", "您还没输入任何内容", 0);
//					System.out.println("您还没输入任何内容-----》》》》");
					
					showDialog("提示", "您还没输入任何内容", null);
					return;
				}
				final String uploadContent = tempContent;
				StringBuffer sb = new StringBuffer();
				sb.append("\n分享内容：\n");
				sb.append(content);
				
				sendEvent("customer_detail", "shareContent", "是否分享", 0);
//				System.out.println("您还没输入任何内容-----》》》》");
				
				showDialog("", sb.toString(), "分享", "取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							showProgressDialog("正在处理...");
							sendTask = SoapService.insertSiemensUpload("", "", uploadContent, "", custDetailId, "2", "", "", "");
							sendTask.setListener(ShareActivity.this);
							sendTask.start();
						}
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (sendTask != null) {
			sendTask.cancel(true);
			sendTask = null;
		}
	}

	private void showErrDialog() {
		this.showDialog("提示", "网络异常，请稍后重试...", null);
	}

	@Override
	public void onTaskFailed(Task task) {
		dismissDialog();
		if (task == sendTask) {
			sendTask = null;
		}
		this.showErrDialog();
	}

	@Override
	public void onTaskFinished(Task task) {
		dismissDialog();
		if (task == sendTask) {
			sendTask = null;
			String result = task.getResult().toString();
			if (result == null) {
				this.showErrDialog();
				return;
			}
			String[] rr = result.split("@");
			if ("true".equals(rr[0])) {
				
				sendEvent("customer_detail", "shareContent", "分享成功", 0);
//				System.out.println("分享成功-----》》》》");
				
				this.showDialog("提示", "分享成功", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}
				});
			} else {
				
				sendEvent("customer_detail", "shareContent", "分享失败，请稍后重试", 0);
//				System.out.println("分享失败，请稍后重试-----》》》》");
				
				this.showDialog("提示", "分享失败，请稍后重试", null);
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
