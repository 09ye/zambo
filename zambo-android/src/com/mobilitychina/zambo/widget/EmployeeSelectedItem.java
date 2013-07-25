package com.mobilitychina.zambo.widget;


import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmployeeSelectedItem extends LinearLayout {

	private TextView mTxtTitle;
	private ImageView mImageCheck;
	private ImageView mImageSeperator;
	private LinearLayout mLayoutSeperator;
	public EmployeeSelectedItem(Context context) {
		super(context);
	}
	public void setEmployeeeInfo(SiemensEmpInfo employee){
		mTxtTitle.setText(employee.getPosName());
	}
	public EmployeeSelectedItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void setTitle(String title){
		mTxtTitle.setText(title);
	}
	public void setSeperator(int value){
		mLayoutSeperator.setVisibility(value);
	}

	public EmployeeSelectedItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setChecked(Boolean bool){
		if(bool){
			mImageCheck.setImageResource(R.drawable.icon_checkbox_selected);
		}else{
			mImageCheck.setImageResource(R.drawable.icon_checkbox);
		}
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mTxtTitle  = (TextView)this.findViewById(R.id.txtTitle);
		mImageCheck = (ImageView)this.findViewById(R.id.imgCheck);
		mImageSeperator = (ImageView)this.findViewById(R.id.imageSeperator);
		mLayoutSeperator = (LinearLayout)this.findViewById(R.id.layoutSeperator);
	}

}
