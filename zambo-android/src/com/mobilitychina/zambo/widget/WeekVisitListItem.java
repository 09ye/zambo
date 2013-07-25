package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;
import com.mobilitychina.zambo.business.plan.PlanCenterActivity;
import com.mobilitychina.zambo.business.plan.PlanCenterFragment;
import com.mobilitychina.zambo.util.Statistics;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 周拜访成员列表item
 * 
 * @author chenwang
 * 
 */
public class WeekVisitListItem extends LinearLayout {
	private TextView tvName;
	private ProgressBar progressVisitNum;
	private TextView tvVisitRate;
	private ImageView ivSeperator;

	public WeekVisitListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public WeekVisitListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WeekVisitListItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		this.tvName = (TextView) this.findViewById(R.id.tvName);
		this.progressVisitNum = (ProgressBar) this.findViewById(R.id.progressVisitNum);
		this.tvVisitRate = (TextView) this.findViewById(R.id.tvVisitRate);
		this.ivSeperator = (ImageView) this.findViewById(R.id.ivSeperator);
	}

	public void show(final SiemensEmpInfo empInfo) {
		this.tvName.setText(empInfo.getEmpName() + "(" + empInfo.getPosName() + ")");
		this.tvVisitRate.setText(empInfo.getVisitedNum() + "/" + empInfo.getVisitNum());
		this.progressVisitNum.setProgress(1.0f * empInfo.getVisitedNum() / empInfo.getVisitNum());
		final String empId = empInfo.getEmpId();
		final String empName = empInfo.getEmpName();((Button) this.findViewById(R.id.btnOpenPlan))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看计划
						Intent intent = new Intent(WeekVisitListItem.this
								.getContext(), PlanCenterActivity.class);
						intent.putExtra(PlanCenterFragment.INTENT_EMP_NAME,
								empName);
						intent.putExtra(PlanCenterFragment.INTENT_EMP_ID, empId);
						WeekVisitListItem.this.getContext().startActivity(
								intent);
						Statistics.sendEvent("weekvisit", "displayplan", "", (long) 0);
					}
				});
		
	}

	public void showSeperator(boolean show) {
		if (show) {
			this.ivSeperator.setVisibility(View.VISIBLE);
		} else {
			this.ivSeperator.setVisibility(View.INVISIBLE);
		}
	}
}
