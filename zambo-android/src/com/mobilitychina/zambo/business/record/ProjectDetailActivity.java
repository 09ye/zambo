package com.mobilitychina.zambo.business.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.departments.DepartmentListActivity;
import com.mobilitychina.zambo.business.departments.data.Department;
import com.mobilitychina.zambo.business.departments.data.DepartmentManager;
import com.mobilitychina.zambo.business.jobtitle.JobTitleListActivity;
import com.mobilitychina.zambo.business.jobtitle.data.JobTitle;
import com.mobilitychina.zambo.business.jobtitle.data.JobTitleManager;
import com.mobilitychina.zambo.business.product.ProductListActivity;
import com.mobilitychina.zambo.business.product.ProductStatusListActivity;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.widget.CountTextWatcher;
import com.mobilitychina.zambo.widget.LinkmanItem;
import com.mobilitychina.zambo.widget.NumberDecimalTextWatcher;

/**
 * 项目详情，包含查看模式，更新模式，新增模式 Intent 参数如下： "detailType": 查看模式，更新模式，新增模式
 * "projectInfo": 项目信息 "customerId": 客户ID "planId": 计划ID
 * 
 * @author chenwang
 * 
 */
public class ProjectDetailActivity extends BaseDetailActivity implements LinkmanItem.OnLinkmanItemClick, ITaskListener {

	public static final int DETAIL_TYPE_SELECT = 0; // 查看
	public static final int DETAIL_TYPE_UPDATE = 1; // 更新
	public static final int DETAIL_TYPE_INSERT = 2; // 新增
	
	private static final Pattern SHARE_ID = Pattern.compile("^1-[0-9|a-z|A-Z]{6}$");

	private TextView tvProjectName;
	private TextView tvProjectStatus;
	private ProjectInfo projectInfo;
	private String customerId;
	private int detailType;
	private String projectId;
	private String projectStatusId;
	private String projectNumber;
	private String planId;
	private SoapTask saveProjectTask;

	//private List<NameValuePair> projectStatusList; // 项目状态列表

	private int REQUESTCODE_PROJECT_NAME = 0xaa01;
	private int REQUESTCODE_PROJECT_STATUS = 0xaa02;
	public final static int REQUEST_CODE_DEPARTMENT = 0xaa03;
	public final static int REQUEST_CODE_JOBTITLE = 0xaa04;

	public final static int DATE_DIALOG = 1;

	private LinearLayout linkmansView;
	private ArrayList<LinkmanItem> linkmanItems = new ArrayList<LinkmanItem>();
	private LinkmanItem curLinkmanItem;
	private EditText projectBudgetEditText;
	private TextView projectTimeTextView;
	private EditText shareIdEditText;
	private EditText reportEditText;

	// date
	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_detail);
		initDate();
//		defaultLinkmanItem = (LinkmanItem) findViewById(R.id.linkman0);
//		defaultLinkmanItem.setOnLinkmanItemClick(this);
		tvProjectStatus = (TextView) findViewById(R.id.tvProjectStatus);
		tvProjectName = (TextView) findViewById(R.id.tvProjectName);

		projectBudgetEditText = (EditText) findViewById(R.id.project_budget);
		projectBudgetEditText.addTextChangedListener(new NumberDecimalTextWatcher(projectBudgetEditText, 2));
		
		projectTimeTextView = (TextView) findViewById(R.id.project_time);
		projectTimeTextView.setOnClickListener(new ItemOnClick());
		shareIdEditText = (EditText) findViewById(R.id.project_share_id);
		reportEditText = (EditText) findViewById(R.id.report);
		reportEditText.addTextChangedListener(new CountTextWatcher(reportEditText,
				(TextView) findViewById(R.id.threshold), 512));

		linkmansView  = (LinearLayout) findViewById(R.id.linkmans);
	

		detailType = this.getIntent().getExtras().getInt("detailType");
		projectInfo = (ProjectInfo) this.getIntent().getExtras().get("projectInfo");
		customerId = this.getIntent().getExtras().getString("customerId");
		planId = this.getIntent().getExtras().getString("planId");

		if (detailType == DETAIL_TYPE_SELECT) {
			this.setTitle("历史项目进度");
			tvProjectName.setEnabled(false);
			tvProjectStatus.setEnabled(false);
			
		} else {
			if (detailType == DETAIL_TYPE_UPDATE) {
				this.setTitle("更新项目");
				tvProjectName.setEnabled(false);
				this.getTitlebar().setRightButton("更新", saveClickListener);
			} else {
				tvProjectName.setOnClickListener(new ItemOnClick());
				this.setTitle("添加项目");
				this.getTitlebar().setRightButton("添加", saveClickListener);
			}
			tvProjectStatus.setOnClickListener(new ItemOnClick());
		}
		setInfo();
		
	}
	private void dealLinkMan(){
		if(projectInfo!= null && projectInfo.getKzrAll()!=null &&projectInfo.getKzrAll().length()>0){
			linkmanItems.clear();
			linkmansView.removeAllViews();
			String []links = projectInfo.getKzrAll().split("&~");
			for (String link : links) {
				LinkmanItem view = createLinkManView();
				String[] args = link.split("&,");
				Department dp = DepartmentManager.getInstance().getDepartmentById(args[0]);
				if(dp!= null ){
					view.setDepartment(dp.getName(), dp);
					}
				JobTitle jt = JobTitleManager.getInstance().getJobTitleById(args[1]);
				if(jt != null){
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
	
	private OnClickListener saveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			StringBuilder sb = new StringBuilder();
			for (LinkmanItem item : linkmanItems) {
				Department dp = (Department) item.getDepartmentTag();
				JobTitle jt = (JobTitle) item.getJobTitleTag();
				String name = item.getName();
				if (dp == null && jt == null
						&& (name == null || name.length() == 0)) {
					continue;
				}
				if (dp == null) {
					new AlertDialog.Builder(ProjectDetailActivity.this)
							.setTitle("提示").setMessage("部门不能为空")
							.setPositiveButton("确认", null).show();
					return;
				}
				if (jt == null) {
					new AlertDialog.Builder(ProjectDetailActivity.this)
							.setTitle("提示").setMessage("职务不能为空")
							.setPositiveButton("确认", null).show();
					return;
				}
				if (name == null || name.length() == 0) {
					new AlertDialog.Builder(ProjectDetailActivity.this)
							.setTitle("提示").setMessage("联系人姓名不能为空")
							.setPositiveButton("确认", null).show();
					return;
				}
				sb.append(dp.getId() + "&,");
				sb.append(jt.getId() + "&,");
				sb.append(name);
				sb.append("&~");
			}
			if (sb.length() == 0) {
				new AlertDialog.Builder(ProjectDetailActivity.this)
						.setTitle("提示").setMessage("至少需要填写一个联系人")
						.setPositiveButton("确认", null).show();
				return;
			} else {
				sb.delete(sb.length()-2, sb.length());
			}
			sb.append("&/");
			//String type = "1";
			
			
			StringBuilder sbRequest = new StringBuilder();
			if(projectId == null || projectId.length() == 0){
				new AlertDialog.Builder(ProjectDetailActivity.this)
				.setTitle("提示").setMessage("未选择产品")
				.setPositiveButton("确认", null).show();
				return;
			}
			if(projectStatusId == null || projectStatusId.length() == 0){
				new AlertDialog.Builder(ProjectDetailActivity.this)
				.setTitle("提示").setMessage("请选择项目状态")
				.setPositiveButton("确认", null).show();
				return;
			}
			String shareId = null;
			if(!TextUtils.isEmpty(shareIdEditText.getText())){
				shareId ="1-"+shareIdEditText.getText().toString();
				if(!SHARE_ID.matcher(shareId).matches()){
					new AlertDialog.Builder(ProjectDetailActivity.this)
					.setTitle("提示").setMessage("sh@reID必须是以1-开头，后面接6位的字母或者数字")
					.setPositiveButton("确认", null).show();
					return;
				}
			}
			
			if(reportEditText.getText() == null || reportEditText.getText().length() == 0){
				new AlertDialog.Builder(ProjectDetailActivity.this)
				.setTitle("提示").setMessage("拜访情况不能为空")
				.setPositiveButton("确认", null).show();
				return;
			}
			sbRequest.append(projectId + "&/");
			sbRequest.append(projectStatusId + "&/");
			sbRequest.append(sb.toString());
			sbRequest.append(projectBudgetEditText.getText() + "&/");
			sbRequest.append(projectTimeTextView.getText() + "&/");
			sbRequest.append(((TextUtils.isEmpty(shareId) ? "" : shareId) + "&/"));
			sbRequest.append(reportEditText.getText() + "&/");
			if (detailType == DETAIL_TYPE_UPDATE) {
				sbRequest.append( projectNumber + "&&");
			} else {
				sbRequest.append("-1&&");
			}
			ProjectDetailActivity.this.showProgressDialog("正在处理...");
			saveProjectTask = SoapService.insertSiemensUpload("", sbRequest.toString(), "",
					planId, customerId, "1", "", "", "");
			saveProjectTask.setListener(ProjectDetailActivity.this);
			saveProjectTask.start();
		}
	};

	public void onDestroy() {
		super.onDestroy();

		if (saveProjectTask != null) {
			saveProjectTask.cancel(true);
			saveProjectTask = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == REQUESTCODE_PROJECT_NAME) {
			projectId = data.getExtras().getString("projectId");
			String productName = data.getExtras().getString("projectName");
			tvProjectName.setText(productName);
		}
		if (requestCode == REQUESTCODE_PROJECT_STATUS) {
			projectStatusId = data.getExtras().getString("ProductStatusid");
			String statusName = data.getExtras().getString("ProductStatusname");
			tvProjectStatus.setText(statusName);
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
	}

	public void setInfo() {
		if (projectInfo != null) {
			projectId = projectInfo.getId();
			projectStatusId = projectInfo.getStatusId();
			tvProjectName.setText(projectInfo.getName());
			tvProjectStatus.setText(projectInfo.getStatus());
			String shareId = projectInfo.getShareId();
			if(!TextUtils.isEmpty(shareId) && shareId.startsWith("1-")){
				shareId = shareId.substring(2);
			}
			shareIdEditText.setText(shareId);
			projectBudgetEditText.setText(projectInfo.getProjectBudget());
			projectNumber = projectInfo.getProNumber();
			if(projectInfo.getAddMenuDate()!= null && projectInfo.getAddMenuDate().length()>10){
				projectTimeTextView.setText(projectInfo.getAddMenuDate().substring(0, 10));
			}
			
			reportEditText.setText(projectInfo.getRemark());
			if(detailType == DETAIL_TYPE_SELECT){
				tvProjectName.setEnabled(false);
				tvProjectStatus.setEnabled(false);
				tvProjectStatus.setEnabled(false);
				projectBudgetEditText.setEnabled(false);
				projectBudgetEditText.setEnabled(false);
				reportEditText.setEnabled(false);
				projectTimeTextView.setEnabled(false);
				shareIdEditText.setEnabled(false);
			}
		}
		dealLinkMan();
		if(detailType != DETAIL_TYPE_SELECT){
			//if(linkmanItems.size() == 0){
				createLinkManView();
			//}
		}
	}


	private class ItemOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tvProjectName: 
				Intent it = new Intent(ProjectDetailActivity.this, ProductListActivity.class);
				if(projectInfo != null){
					it.putExtra("projectid",projectInfo.getId());
				}
				startActivityForResult(it, REQUESTCODE_PROJECT_NAME);
				break;
			case R.id.tvProjectStatus:
				Intent intent = new Intent(ProjectDetailActivity.this, ProductStatusListActivity.class);
				if(projectInfo != null){
					intent.putExtra("ProductStatusid",projectStatusId);
				}
				startActivityForResult(intent, REQUESTCODE_PROJECT_STATUS);
				break;
			case R.id.project_time:
				showDialog(DATE_DIALOG);
				break;
			default:
				break;
			}

		}
	}

	@Override
	public void onTaskFailed(Task task) {
		this.dismissDialog();
		if (saveProjectTask == task) {
			saveProjectTask = null;
			this.showDialog("提示", "网络错误，请稍后重试", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		} 
	}

	@Override
	public void onTaskFinished(Task task) {
		this.dismissDialog();
		if (saveProjectTask == task) {
			Object result = (Object) task.getResult();
			saveProjectTask = null;
			boolean success = false;
			String msg = "";
			if (result != null) {
				String[] rr = result.toString().split("@");
				success = rr[0].equalsIgnoreCase("0");
				if(rr.length > 1){
					msg = rr[1];
				}else{
					msg = "服务器内部错误";
				}
			}
			if (!success) {
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PROJECT,"提交失败;"+msg);
				this.showDialog("提示",msg, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				return;
			} else {
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PROJECT,"提交成功");
				Intent intent = new Intent();
				intent.setAction(ConfigDefinition.INTENT_ACTION_SUBMITITEM);
				ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);

				this.showDialog("提示", "操作成功", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						setResult(0);
						finish();
					}
				});
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

	@Override
	public void onDepartmentItemClick(LinkmanItem view, TextView departmentView) {
		curLinkmanItem = view;
		Intent intent = new Intent(ProjectDetailActivity.this, DepartmentListActivity.class);
		if(view.getDepartmentTag()!= null){
			intent.putExtra("deptid",((Department) view.getDepartmentTag()).getId());
		}
		startActivityForResult(intent, REQUEST_CODE_DEPARTMENT);
	}

	@Override
	public void onJobTitleItemClick(LinkmanItem view, TextView jobTitleView) {
		curLinkmanItem = view;
		Intent intent = new Intent(ProjectDetailActivity.this, JobTitleListActivity.class);
		if(view.getJobTitleTag()!= null){
			intent.putExtra("jobtitleid",((JobTitle) view.getJobTitleTag()).getId());
		}
		startActivityForResult(intent, REQUEST_CODE_JOBTITLE);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			setProjectTime();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		final DatePickerDialog dlg = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		dlg.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mYear = dlg.getDatePicker().getYear();
				mMonth = dlg.getDatePicker().getMonth();
				mDay = dlg.getDatePicker().getDayOfMonth();
				setProjectTime();
			}

		});
		return dlg;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
	}

	private void setProjectTime() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(mYear).append("-").append((mMonth + 1)).append("-").append(mDay);
		projectTimeTextView.setText(buffer.toString());
	}

	private void initDate() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public void onNameEdit(LinkmanItem view, EditText jobTitleView) {
		// TODO Auto-generated method stub
		if(view == linkmanItems.get(linkmanItems.size()-1)){
			createLinkManView();
		}
	}

	@Override
	public void onDeleteItem(LinkmanItem view, Button jobTitleView) {
		// TODO Auto-generated method stub
		final LinkmanItem deleleLinkView = view;
		((BaseActivity)this).showDialog("提示", "确认删除联系人么？", "确认","取消",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == -1){
					ProjectDetailActivity.this.deleteLinkManView(deleleLinkView);
				}

			}
		});
	}
	private LinkmanItem createLinkManView() {
		
		LinkmanItem item = new LinkmanItem(this);
		item.setOnLinkmanItemClick(this);
		//item.setTag(new Integer(linkmanItems.size()));
		if(detailType == DETAIL_TYPE_SELECT){
			item.setEnabled(false);
			item.setEditable(false);
		}
		linkmanItems.add(item);
		linkmansView.addView(item);
		if(detailType != DETAIL_TYPE_SELECT){
			if(linkmanItems.size()  == 1){
				for (LinkmanItem iterable_element : linkmanItems) {
					iterable_element.setEnableDelete(false);
				}
			}else{
				for (LinkmanItem iterable_element : linkmanItems) {
					iterable_element.setEnableDelete(true);
				}
			}
		}
		LinkmanItem lastItem = linkmanItems.get(linkmanItems.size()-1);
		lastItem.setEnableDelete(false);
		return item;
	}
	private void deleteLinkManView(LinkmanItem view ) {
	
		if(linkmanItems.size() == 1){
			((BaseActivity)this).showDialog("提示", "请至少一个保留一个联系人", null);
		}else{
			linkmanItems.remove(view);
			linkmansView.removeView(view);
		}
		if(linkmanItems.size()  == 1){
			for (LinkmanItem iterable_element : linkmanItems) {
				iterable_element.setEnableDelete(false);
			}
		}else{
			for (LinkmanItem iterable_element : linkmanItems) {
				iterable_element.setEnableDelete(true);
			}
		}
		LinkmanItem lastItem = linkmanItems.get(linkmanItems.size()-1);
		lastItem.setEnableDelete(false);
		
	}
	
}
