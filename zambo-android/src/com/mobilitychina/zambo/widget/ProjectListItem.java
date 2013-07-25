package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;

/**
 * 客户项目列表Item
 * 
 * @author chenwang
 * 
 */
public class ProjectListItem extends RelativeLayout {
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvStatus;
	private TextView tvStatusPercent;
	private ProgressBar progressVisitNum;

	public ProjectListItem(Context context) {
		super(context);
	}

	public ProjectListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ProjectListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvNumber = (TextView) this.findViewById(R.id.tvNumber);
		this.tvName = (TextView) this.findViewById(R.id.tvName);
		this.tvStatus = (TextView) this.findViewById(R.id.tvStatus);
		this.tvStatusPercent = (TextView) this.findViewById(R.id.tvStatusPercent);
		this.progressVisitNum = (ProgressBar) this.findViewById(R.id.progressVisitNum);

	}

	public void show(ProjectInfo projectInfo) {
		this.tvNumber.setText(projectInfo.getProNumber());
		this.tvName.setText(projectInfo.getName());
		this.tvStatus.setText(projectInfo.getStatusText());
		this.tvStatusPercent.setText(projectInfo.getStatusValue() + "%");
		this.progressVisitNum.setProgress(projectInfo.getStatusValue() / 100.f);
	}
}
