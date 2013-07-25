package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.util.CommonUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 项目进度记录列表Item
 * 
 * @author chenwang
 * 
 */
public class ProjectRecordListItem extends RelativeLayout {
	private TextView tvDate;
	private TextView tvStatus;
	private TextView tvStatusPercent;
	private TextView tvRemark;

	public ProjectRecordListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ProjectRecordListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ProjectRecordListItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		this.tvDate = (TextView) this.findViewById(R.id.tvDate);
		this.tvStatus = (TextView) this.findViewById(R.id.tvStatus);
		this.tvStatusPercent = (TextView) this.findViewById(R.id.tvStatusPercent);
		this.tvRemark = (TextView) this.findViewById(R.id.tvRemark);
	}

	public void show(ProjectInfo projectInfo) {
		this.tvDate.setText(CommonUtil.formatMonthDay(projectInfo.getVisitDate()));
		this.tvStatus.setText(projectInfo.getStatus());
		if (projectInfo.getRemark() != null && projectInfo.getRemark().length() > 0) {
			this.tvRemark.setText(projectInfo.getRemark());
		} else {
			this.tvRemark.setText("暂无备注");
		}
	}
}
