package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;

public class CommonListItem extends LinearLayout {
	
	private TextView tvName;
	private ImageView ivSelected;
	private ImageView ivSeperator;

	public CommonListItem(Context context) {
		super(context);
	}

	public CommonListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CommonListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvName = (TextView) this.findViewById(R.id.tvName);
		this.ivSelected = (ImageView) this.findViewById(R.id.ivSelected);
		this.ivSeperator = (ImageView) this.findViewById(R.id.ivSeperator);
	}
	
	private boolean isMultiSelect;
	public void setMultiSelect() {
		isMultiSelect = true;
		this.ivSelected.setImageResource(R.drawable.check_btn);
		this.ivSelected.setVisibility(View.VISIBLE);
	}

	public void show(String name, boolean isSelected) {
		this.tvName.setText(name);
		if(isMultiSelect){
			this.ivSelected.setSelected(isSelected);
		}else {
			if (isSelected) {
				this.ivSelected.setVisibility(View.VISIBLE);
			} else {
				this.ivSelected.setVisibility(View.GONE);
			}
		}
	}

	public void showSeperator(boolean show) {
		if (show) {
			this.ivSeperator.setVisibility(View.VISIBLE);
		} else {
			this.ivSeperator.setVisibility(View.INVISIBLE);
		}
	}

}
