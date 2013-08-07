package com.mobilitychina.zambo.business.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.home.MainActivity;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.UserInfoManager;

/**
 * 修改密码 分两种情况： 1. 每一次登录时进入修改密码，当密码修改结束后进入主界面。 2. 进入主界面后修改密码，则修改完成后直接关闭当前界面即可
 * 
 * @author chenwang
 * 
 */
public class ModifyPasswordActivity extends BaseDetailActivity implements ITaskListener {
	private Button okBtn;
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;

	private SoapTask modifyPasswordTask;

	private boolean firstLogin;
	private TextView oldpwtitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		this.setTitle("修改密码");

		if (this.getIntent().getExtras() != null) {
			firstLogin = this.getIntent().getExtras().getBoolean("firstLogin");
		}
		oldPassword = (EditText) findViewById(R.id.old_password);
		newPassword = (EditText) findViewById(R.id.new_password);
		confirmPassword = (EditText) findViewById(R.id.confirm_password);
		okBtn = (Button) findViewById(R.id.modify_password_commit);
		oldpwtitle = (TextView) findViewById(R.id.old_pw_title);

		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendEvent("passwordchange", "passwordchange", "", 0);
				updateButtonLook(true);
				modifyPassword();
			}
		});

		if (firstLogin) {
			oldPassword.setText(UserInfoManager.getInstance().getPassword());
			oldPassword.setVisibility(View.GONE);
			oldpwtitle.setVisibility(View.GONE);
		} else {
			oldPassword.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (modifyPasswordTask != null) {
			modifyPasswordTask.cancel(true);
			modifyPasswordTask = null;
		}
	}

	private void modifyPassword() {
		String old_password = oldPassword.getText().toString();
		String new_password = newPassword.getText().toString().trim();
		String confirm_password = confirmPassword.getText().toString().trim();
		if ("T".equals(firstLogin)) {
			if (new_password == null || "".equals(new_password)) {

				Toast.makeText(ModifyPasswordActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
				// okBtn.setBackgroundResource(R.drawable.common_btn);
				return;
			} else if (confirm_password == null || "".equals(confirm_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "确认密码不能为空", Toast.LENGTH_LONG).show();
				return;
			} else if (!new_password.equals(confirm_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
				newPassword.setText("");
				confirmPassword.setText("");
				return;
			}

		} else {
			if (old_password == null || "".equals(old_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "旧密码不能为空", Toast.LENGTH_LONG).show();
				oldPassword.requestFocus();
				return;
			} else if (new_password == null || "".equals(new_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
				newPassword.requestFocus();
				return;
			} else if (confirm_password == null || "".equals(confirm_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "确认密码不能为空", Toast.LENGTH_LONG).show();
				confirmPassword.requestFocus();
				return;
			} else if (!new_password.equals(confirm_password)) {
				Toast.makeText(ModifyPasswordActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
				newPassword.setText("");
				confirmPassword.setText("");
				newPassword.requestFocus();
				return;
			}
		}

		if (new_password.length() < 6 || confirm_password.length() < 6) {
			Toast.makeText(ModifyPasswordActivity.this, "密码长度不得小于6位", Toast.LENGTH_LONG).show();
			newPassword.requestFocus();
			return;
		}

		if (new_password.equals(UserInfoManager.getInstance().getPassword())) {
			Toast.makeText(ModifyPasswordActivity.this, "密码不能与旧密码一样", Toast.LENGTH_LONG).show();
			newPassword.setText("");
			confirmPassword.setText("");
			newPassword.requestFocus();
			return;
		}

		if (!checkPassword(new_password)) {
			Toast.makeText(ModifyPasswordActivity.this, "密码过于简单", Toast.LENGTH_LONG).show();
			newPassword.setText("");
			confirmPassword.setText("");
			newPassword.requestFocus();
			return;
		}

		this.showProgressDialog("正在处理...");
		modifyPasswordTask = SoapService.getModifyPasswordTask(this, UserInfoManager.getInstance().getUserId(),
				old_password, new_password);
		modifyPasswordTask.setListener(this);
		modifyPasswordTask.start();
	}

	protected void onProgressDialogCancel() {
		this.updateButtonLook(false);
		if (modifyPasswordTask != null) {
			modifyPasswordTask.cancel(true);
			modifyPasswordTask = null;
		}
	}

	private void updateButtonLook(boolean keepSelected) {

	}

	// 判断密码是否一样的字符的情况
	private boolean checkPassword(String pw) {

		int count = pw.length();
		char c1 = pw.charAt(0);
		int samecount = 0;
		for (int i = 1; i < count; i++) {
			if (c1 == pw.charAt(i)) {
				samecount++;
			}
		}
		if (samecount == count - 1) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		Toast.makeText(ModifyPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
		UserInfoManager.getInstance().setPassword(newPassword.getText().toString().trim());
		if (firstLogin) {
			Intent intent = new Intent(ModifyPasswordActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		} else {
			finish();
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		Toast.makeText(ModifyPasswordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
		updateButtonLook(false);
	}

	@Override
	public void onTaskUpdateProgress(Task task, int count, int total) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}
