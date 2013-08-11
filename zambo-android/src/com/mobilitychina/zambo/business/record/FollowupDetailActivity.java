package com.mobilitychina.zambo.business.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.departments.DepartmentListActivity;
import com.mobilitychina.zambo.business.departments.data.Department;
import com.mobilitychina.zambo.business.departments.data.DepartmentManager;
import com.mobilitychina.zambo.business.facility.FacilityListActivity;
import com.mobilitychina.zambo.business.facility.data.FacilityManager;
import com.mobilitychina.zambo.business.jobtitle.JobTitleListActivity;
import com.mobilitychina.zambo.business.jobtitle.data.JobTitle;
import com.mobilitychina.zambo.business.jobtitle.data.JobTitleManager;
import com.mobilitychina.zambo.business.notifyteam.NotifyTeamListActivity;
import com.mobilitychina.zambo.business.notifyteam.data.NotifyTeam;
import com.mobilitychina.zambo.business.notifyteam.data.NotifyTeamManager;
import com.mobilitychina.zambo.business.record.data.FollowupInfo;
import com.mobilitychina.zambo.service.HttpPostService;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.widget.CountTextWatcher;
import com.mobilitychina.zambo.widget.LinkmanItem;

/**
 * 客户随访详情，支持查看模式和编辑模式
 * 
 * @author chenwang
 * 
 */
public class FollowupDetailActivity extends BaseDetailActivity implements View.OnClickListener,
		LinkmanItem.OnLinkmanItemClick, ITaskListener {

	public final static String INTENT_STRING_PLAN_ID = "planId";
	public final static String INTENT_INT_CUSTOMER_ID = "customerId";
	public final static String INTENT_BOOL_EDIT = "edit";

	public final static int REQUEST_CODE_DEPARTMENT = 0xaa01;
	public final static int REQUEST_CODE_JOBTITLE = 0xaa02;
	public final static int REQUEST_CODE_POTENCIAL = 0xaa03;
	public final static int REQUEST_CODE_NOTIFY = 0xaa04;

	private EditText report;
	private String followupId;
	private String kzr_all;
	private String planId;
	private String customerId;
	private String productdalei_all;
	
	private SoapTask getFollowupInfoTask;
	private HttpPostTask saveFollowupTask;
	private String if_send;// 是否发送通知；
	private String require_content;// 通知
	private LinearLayout linkmansView;
	// private LinkmanItem defaultLinkmanItem;
	private ArrayList<LinkmanItem> linkmanItems = new ArrayList<LinkmanItem>();

	private TextView potencialTextView;
	private CheckBox notifyTeamCheckBox;
	private TextView notifyContentTextView;

	private LinkmanItem curLinkmanItem;
	private boolean isEditable = true;

	protected FollowupDetailActivity getInstnce() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_followup);

		isEditable = this.getIntent().getExtras().getBoolean("edit", false);

		this.setTitle("随访备注");
		if (isEditable) {
			this.getTitlebar().setRightButton("提交", new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						saveFollowUp();
					} catch (Exception e) {

					}
				}
			});
		}

		report = (EditText) findViewById(R.id.report);
		report.setEnabled(isEditable);
		report.addTextChangedListener(new CountTextWatcher(report, (TextView) findViewById(R.id.threshold), 512));

		followupId = this.getIntent().getExtras().getString("followupId");
		planId = String.valueOf(this.getIntent().getExtras().getInt("planId"));
		customerId = this.getIntent().getExtras().getString("customerId");
		kzr_all = this.getIntent().getExtras().getString("kzr_all");
		productdalei_all = this.getIntent().getExtras().getString("productdalei_all");
		if_send = this.getIntent().getExtras().getString("if_send");
		require_content = this.getIntent().getExtras().getString("require_content");
		linkmansView = (LinearLayout) findViewById(R.id.linkmans);
		potencialTextView = (TextView) findViewById(R.id.potencialdemand);
		potencialTextView.setOnClickListener(this);

		notifyTeamCheckBox = (CheckBox) findViewById(R.id.notify_team);
		notifyTeamCheckBox.setEnabled(isEditable);
		if (isEditable) {
			notifyTeamCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					notifyContentTextView.setEnabled(isChecked);
				}
			});
		}

		notifyContentTextView = (TextView) findViewById(R.id.notifycontent);
		notifyContentTextView.setOnClickListener(this);

		if (this.getIntent().getExtras().getString("remark") != null) {
			report.setText(this.getIntent().getExtras().getString("remark"));

		}

		if (followupId == null) {
			this.getTitlebar().getRightButton().setEnabled(false);
			this.showProgressDialog("正在加载...");
			// getFollowupInfoTask = SoapService.getFollowupListTask(this.customerId);
			getFollowupInfoTask = SoapService.getFollowupListForCurrVisitTask(this.planId);
			getFollowupInfoTask.setListener(this);
			getFollowupInfoTask.start();
		} else {
			this.getTitlebar().getRightButton().setEnabled(true);
			dealLinkMan();
			if (isEditable) {
				createLinkManView();
			}
		}
		if (if_send != null && if_send.equalsIgnoreCase("U")) {
			notifyTeamCheckBox.setChecked(true);
		} else {
			notifyTeamCheckBox.setChecked(false);
		}
		dealProduct();
		dealNotice();
	}

	private void dealNotice() {
		if (require_content != null && require_content.length() > 0) {
			NotifyTeam nt = NotifyTeamManager.getInstance().getNotifyTeamById(require_content);
			notifyContentTextView.setText(nt.getName());
			notifyContentTextView.setTag(nt);
		}
	}

	private void dealProduct() {
		if (productdalei_all != null && productdalei_all.length() > 0) {
			String pro = FacilityManager.getInstance().getFacilityByIds(productdalei_all.replace("&~", ","));
			potencialTextView.setText(pro);
		}else{
			potencialTextView.setText("");
		}
	}

	private void dealLinkMan() {
		if (kzr_all != null && kzr_all.length() > 0) {
			linkmanItems.clear();
			linkmansView.removeAllViews();
			String[] links = kzr_all.split("&~");
			for (String link : links) {
				LinkmanItem view = createLinkManView();
				String[] args = link.split("&,");
				Department dp = DepartmentManager.getInstance().getDepartmentById(args[0]);
				if (dp != null) {
					view.setDepartment(dp.getName(), dp);
				}
				JobTitle jt = JobTitleManager.getInstance().getJobTitleById(args[1]);
				if (jt != null) {
					view.setJobTitle(jt.getName(), jt);
				}
				view.setName(args[2]);
			}
		}
	}

	@SuppressWarnings("unused")
	private void sortLinkmans(String[] links) {

		Arrays.sort(links, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				String[] args1 = lhs.split("&,");
				String[] args2 = rhs.split("&,");
				if (args1.length < 3 || args2.length < 3) {
					return 1;
				}
				return args1[2].compareTo(args2[2]);
			}
		});
	}

	private LinkmanItem createLinkManView() {

		LinkmanItem item = new LinkmanItem(this);
		item.setOnLinkmanItemClick(this);
		// item.setTag(new Integer(linkmanItems.size()));
		item.setEnabled(isEditable);
		item.setEditable(isEditable);
		linkmanItems.add(item);
		linkmansView.addView(item);
		if(isEditable){
			if (linkmanItems.size() == 1 ) {
				for (LinkmanItem iterable_element : linkmanItems) {
					iterable_element.setEnableDelete(false);
				}
			} else {
				for (LinkmanItem iterable_element : linkmanItems) {
					iterable_element.setEnableDelete(true);
				}
			}
		}
		
		LinkmanItem lastItem = linkmanItems.get(linkmanItems.size()-1);
		lastItem.setEnableDelete(false);
		return item;
	}

	private void deleteLinkManView(LinkmanItem view) {

		if (linkmanItems.size() == 1) {
			((BaseActivity) this).showDialog("提示", "请至少一个保留一个联系人", null);
		} else {
			linkmanItems.remove(view);
			linkmansView.removeView(view);
		}
		if (linkmanItems.size() == 1) {
			for (LinkmanItem iterable_element : linkmanItems) {
				iterable_element.setEnableDelete(false);
			}
		}
	}

	private void saveFollowUp() {

		/*	StringBuilder sb = new StringBuilder();
		for (LinkmanItem item : linkmanItems) {
			Department dp = (Department) item.getDepartmentTag();
			JobTitle jt = (JobTitle) item.getJobTitleTag();
			String name = item.getName();
			if (dp == null && jt == null && (name == null || name.length() == 0)) {
				continue;
			}
			if (dp == null) {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("部门不能为空").setPositiveButton("确认", null)
						.show();
				return;
			}
			if (jt == null) {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("职务不能为空").setPositiveButton("确认", null)
						.show();
				return;
			}
			if (name == null || name.length() == 0) {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("联系人姓名不能为空")
						.setPositiveButton("确认", null).show();
				return;
			}
			sb.append(dp.getId() + "&,");
			sb.append(jt.getId() + "&,");
			sb.append(name);
			sb.append("&~");
		}
		if (sb.length() == 0) {
			new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("至少需要填写一个联系人")
					.setPositiveButton("确认", null).show();
			return;
		} else {
			// sb.deleteCharAt(sb.length() - 2);
			sb.delete(sb.length() - 2, sb.length());
		}

		sb.append("&/");
		if (productdalei_all == null || productdalei_all.length() == 0) {
			new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("请至少选择一个产品大类")
					.setPositiveButton("确认", null).show();
			return;
		}
		sb.append(productdalei_all);
		sb.append("&/");
		String reportVal = report.getText().toString();
		if ("".equals(reportVal)) {
			new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("请填写拜访情况内容").setPositiveButton("确认", null)
					.show();
			return;
		}

		sb.append(reportVal);
		sb.append("&/");
		sb.append((notifyTeamCheckBox.isChecked() == true ? "U" : "N"));
		sb.append("&/");
		NotifyTeam nt = (NotifyTeam) notifyContentTextView.getTag();
		if (notifyTeamCheckBox.isChecked()) {
			if (nt == null) {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("请选择一个通知内容")
						.setPositiveButton("确认", null).show();
				return;
			}
		}
		if (nt != null) {
			sb.append(nt.getId());
		}
		sb.append("&&");*/

		showProgressDialog("正在提交...");
		saveFollowupTask =  new HttpPostTask(FollowupDetailActivity.this);
		saveFollowupTask.setUrl(HttpPostService.SOAP_URL + "visit_follow_up");
		saveFollowupTask.getTaskArgs().put("visit_plan_id", String.valueOf(planId));
		saveFollowupTask.getTaskArgs().put("cust_id", String.valueOf(customerId));
		saveFollowupTask.getTaskArgs().put("visit_plan_id", String.valueOf(planId));
		saveFollowupTask.getTaskArgs().put("memo", report.getText().toString());
		saveFollowupTask.getTaskArgs().put("client_time","'"+CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")+"'");
		saveFollowupTask.setListener(FollowupDetailActivity.this);
		saveFollowupTask.start();

	}

	protected void onDestroy() {
		super.onDestroy();
		if (getFollowupInfoTask != null) {
			getFollowupInfoTask.cancel(true);
			getFollowupInfoTask = null;
		}
		if (saveFollowupTask != null) {
			saveFollowupTask.cancel(true);
			saveFollowupTask = null;
		}
	}

	@Override
	protected void onProgressDialogCancel() {
		// TODO Auto-generated method stub
		super.onProgressDialogCancel();
		if (getFollowupInfoTask != null) {
			getFollowupInfoTask.cancel(true);
			getFollowupInfoTask = null;
		}
		if (saveFollowupTask != null) {
			saveFollowupTask.cancel(true);
			saveFollowupTask = null;
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		if (getFollowupInfoTask == task) {
			getFollowupInfoTask = null;
			new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("网络出错，请稍后重试")
					.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
		} else if (saveFollowupTask == task) {
			saveFollowupTask = null;
			new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("提交失败,请重试").setPositiveButton("确认", null)
					.show();
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		if (getFollowupInfoTask == task) {
			SoapObject result = (SoapObject) task.getResult();
			List<FollowupInfo> followupInfoList = this.parse(result);

			if (followupInfoList != null && followupInfoList.size() > 0) {
				FollowupInfo log = followupInfoList.get(0);
				followupId = log.getId();
				String remark = log.getRemark();
				if (remark != null) {
					report.setText(remark);
				}
				kzr_all = log.getKzr();
				if_send = log.getIfSend();// 是否发送通知；
				require_content = log.getRequireContent();// 通知
				productdalei_all = log.getProductdalei();
			}
			dealLinkMan();
			if (isEditable) {
				createLinkManView();
			}

			if (if_send != null && if_send.equalsIgnoreCase("U")) {
				notifyTeamCheckBox.setChecked(true);
			} else {
				notifyTeamCheckBox.setChecked(false);
			}
			dealProduct();
			dealNotice();
			this.getTitlebar().getRightButton().setEnabled(true);

			getFollowupInfoTask = null;
		} else if (saveFollowupTask == task) {
			NetObject result = ((HttpPostTask) task).getResult();
			String code = result.stringForKey("code");
			String message = result.stringForKey("message");
			if (code.equals("0")) {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage(message).setPositiveButton("确认", null).show();
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_FOLLOWUP,"提交失败;"+message);
			} else {
				new AlertDialog.Builder(getInstnce()).setTitle("提示").setMessage("随访备注提交成功")
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent();
								intent.setAction(ConfigDefinition.INTENT_ACTION_SUBMITFLUP);
								ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);

								finish();
							}
						}).show();
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_FOLLOWUP,"提交成功");
			}
			saveFollowupTask = null;
		}
	}

	private List<FollowupInfo> parse(SoapObject result) {
		if (result == null) {
			return null;
		}
		int n = result.getPropertyCount();
		List<FollowupInfo> followupList = new ArrayList<FollowupInfo>(n);
		for (int i = 0; i < n; i++) {
			FollowupInfo followup = new FollowupInfo();
			String element = result.getProperty(i).toString();
			followup.setKzr(CommonUtil.getStringElement(element, "kzr_all"));
			followup.setId(CommonUtil.getStringElement(element, "id"));
			followup.setDatelineId(CommonUtil.getStringElement(element, "datelineId"));
			followup.setType(CommonUtil.getStringElement(element, "type"));
			followup.setVisitDate(CommonUtil.getStringElement(element, "visitDate"));
			followup.setRemark(CommonUtil.getStringElement(element, "remark"));
			followup.setRequireContent(CommonUtil.getStringElement(element, "require_content"));
			followup.setIfSend(CommonUtil.getStringElement(element, "if_send"));
			followup.setProductdalei(CommonUtil.getStringElement(element, "productdalei_all"));
			followupList.add(followup);
		}
		return followupList;
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDepartmentItemClick(LinkmanItem view, TextView departmentView) {
//		curLinkmanItem = view;
//		Intent intent = new Intent(FollowupDetailActivity.this, DepartmentListActivity.class);
//		if (view.getDepartmentTag() != null) {
//			intent.putExtra("deptid", ((Department) view.getDepartmentTag()).getId());
//		}
//		startActivityForResult(intent, REQUEST_CODE_DEPARTMENT);
	}

	@Override
	public void onJobTitleItemClick(LinkmanItem view, TextView jobTitleView) {
//		curLinkmanItem = view;
//		Intent intent = new Intent(FollowupDetailActivity.this, JobTitleListActivity.class);
//		if (view.getJobTitleTag() != null) {
//			intent.putExtra("jobtitleid", ((JobTitle) view.getJobTitleTag()).getId());
//		}
//		startActivityForResult(intent, REQUEST_CODE_JOBTITLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		if (requestCode == REQUEST_CODE_DEPARTMENT) {
			String deptId = data.getExtras().getString("deptid");
			Department dept = DepartmentManager.getInstance().getDepartmentById(deptId);
			if (curLinkmanItem != null && dept != null) {
				curLinkmanItem.setDepartment(dept.getName(), dept);
			}
		}
		if (requestCode == REQUEST_CODE_JOBTITLE) {
			String jobid = data.getExtras().getString("jobtitleid");
			JobTitle jt = JobTitleManager.getInstance().getJobTitleById(jobid);
			if (curLinkmanItem != null && jt != null) {
				curLinkmanItem.setJobTitle(jt.getName(), jt);
			}
		}
		if (requestCode == REQUEST_CODE_POTENCIAL) {
			productdalei_all = data.getExtras().getString("ids");
			dealProduct();

		}
		if (requestCode == REQUEST_CODE_NOTIFY) {
			String id = data.getExtras().getString("notifyteamid");
			NotifyTeam nt = NotifyTeamManager.getInstance().getNotifyTeamById(id);
			notifyContentTextView.setTag(nt);
			notifyContentTextView.setText(nt.getName());
		}
	}

	@Override
	public void onClick(View v) {
		if (!isEditable) {
			return;
		}
//		if (v == potencialTextView) {
//			Intent intent = new Intent(FollowupDetailActivity.this, FacilityListActivity.class);
//			intent.putExtra("ids", productdalei_all);
//			startActivityForResult(intent, REQUEST_CODE_POTENCIAL);
//		}
//		if (v == notifyContentTextView) {
//			Intent intent = new Intent(FollowupDetailActivity.this, NotifyTeamListActivity.class);
//			if (notifyContentTextView.getTag() != null) {
//				intent.putExtra("notifyteamid", ((NotifyTeam) notifyContentTextView.getTag()).getId());
//			}
//			startActivityForResult(intent, REQUEST_CODE_NOTIFY);
//		}

	}

	@Override
	public void onNameEdit(LinkmanItem view, EditText jobTitleView) {
		// TODO Auto-generated method stub
		// Integer tag = (Integer)view.getTag();
//		if (view == linkmanItems.get(linkmanItems.size() - 1)) {
//			createLinkManView();
//		}
	}

	@Override
	public void onDeleteItem(LinkmanItem view, Button jobTitleView) {
		// TODO Auto-generated method stub
//		final LinkmanItem deleleLinkView = view;
//		((BaseActivity) this).showDialog("提示", "确认删除联系人么？", "确认", "取消", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				if (which == -1) {
//					FollowupDetailActivity.this.deleteLinkManView(deleleLinkView);
//				}
//			}
//		});
	}
}
