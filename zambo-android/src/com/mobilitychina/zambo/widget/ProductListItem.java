package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductListItem extends LinearLayout {
	private TextView tvName;
	private ImageView ivSelected;
	private ImageView ivSeperator;

	public ProductListItem(Context context) {
		super(context);
	}

	public ProductListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ProductListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvName = (TextView) this.findViewById(R.id.tvName);
		this.ivSelected = (ImageView) this.findViewById(R.id.ivSelected);
		this.ivSeperator = (ImageView) this.findViewById(R.id.ivSeperator);
	}

	public void show(ProjectInfo projectInfo, boolean isSelected) {
		this.tvName.setText(projectInfo.getName());
		if (isSelected) {
			this.ivSelected.setVisibility(View.VISIBLE);
		} else {
			this.ivSelected.setVisibility(View.GONE);
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
