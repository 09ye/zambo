package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;

public class LinkmanItem extends LinearLayout implements OnClickListener, TextWatcher {

	private OnLinkmanItemClick onLinkmanItemClick;

	private TextView departmentView;
	private TextView jobTitleView;
	private EditText nameEditText;
	private Button deleteButton;
	private boolean isEditable = true;

	public LinkmanItem(Context context) {
		this(context, null, 0);
	}

	public LinkmanItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LinkmanItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate(context, R.layout.linkman_item, this);
		setupView();
	}

	private void setupView() {
		if (isInEditMode()) {
			return;
		}
		departmentView = (TextView) this.findViewById(R.id.department);
		jobTitleView = (TextView) this.findViewById(R.id.jobtitle);
		nameEditText = (EditText) this.findViewById(R.id.name);
		deleteButton = (Button)this.findViewById(R.id.btnDelete);
		nameEditText.addTextChangedListener(this);
		departmentView.setOnClickListener(this);
		jobTitleView.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.setEnableDelete(enabled);
	}
	public void setEnableDelete(boolean enabled){
		if (enabled) {
			this.deleteButton.setBackgroundResource(R.drawable.delete_button);
		} else {
			this.deleteButton.setBackgroundResource(R.drawable.delete_button_disable);
		}
		this.deleteButton.setEnabled(enabled);
	}
	public String getDepartment() {
		return departmentView.getText().toString();
	}
	
	public Object getDepartmentTag(){
		return departmentView.getTag();
	}
	
	public void setDepartment(String dept,Object tag) {
		departmentView.setText(dept);
		departmentView.setTag(tag);
	}

	public String getJobTitle() {
		return jobTitleView.getText().toString();
	}
	
	public Object getJobTitleTag(){
		return jobTitleView.getTag();
	}
	
	public void setJobTitle(String jobTitle,Object tag) {
		jobTitleView.setText(jobTitle);
		jobTitleView.setTag(tag);
	}

	public String getName() {
		return nameEditText.getText().toString();
	}
	
	public void setName(String name) {
		 nameEditText.setText(name);
				
	}
	@Override
	public void onClick(View v) {
		if(!isEditable){
			return;
		}
		if (onLinkmanItemClick == null) {
			return;
		}
		if (v == departmentView) {
			onLinkmanItemClick.onDepartmentItemClick(this, departmentView);
		}
		if (v == jobTitleView) {
			onLinkmanItemClick.onJobTitleItemClick(this, jobTitleView);
		}
		if(v == deleteButton){
			onLinkmanItemClick.onDeleteItem(this, deleteButton);
		}
	}

	public void setOnLinkmanItemClick(OnLinkmanItemClick listener) {
		this.onLinkmanItemClick = listener;
	}
	
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		nameEditText.setEnabled(isEditable);
	}

	public static interface OnLinkmanItemClick {
		void onDepartmentItemClick(LinkmanItem view, TextView departmentView);
		void onJobTitleItemClick(LinkmanItem view, TextView jobTitleView);
		void onNameEdit(LinkmanItem view, EditText jobTitleView);
		void onDeleteItem(LinkmanItem view, Button jobTitleView);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		if(nameEditText.isFocused()){
			this.onLinkmanItemClick.onNameEdit(this, nameEditText);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

}
