package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.message.data.SiemensEmpInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 周拜访团队列表item
 * 
 * @author chenwang
 * 
 */
public class WeekVisitTeamItem extends LinearLayout {
	private TextView tvName;
	private TextView tvTips;
	private ImageView ivSeperator;

	public WeekVisitTeamItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public WeekVisitTeamItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WeekVisitTeamItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		this.tvName = (TextView) this.findViewById(R.id.tvName);
		this.tvTips = (TextView) this.findViewById(R.id.tvTips);
		this.ivSeperator = (ImageView) this.findViewById(R.id.ivSeperator);
	}

	public void show(SiemensEmpInfo empInfo) {
		this.tvName.setText(empInfo.getEmpName() + "(" + empInfo.getPosName() + ")");
		this.tvTips.setText("查看团队" + empInfo.getPosName() + "下属情况");
	}

	public void showSeperator(boolean show) {
		if (show) {
			this.ivSeperator.setVisibility(View.VISIBLE);
		} else {
			this.ivSeperator.setVisibility(View.INVISIBLE);
		}
	}
}
