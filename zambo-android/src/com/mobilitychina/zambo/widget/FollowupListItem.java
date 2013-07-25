package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.FollowupInfo;

/**
 * 客户随访记录Item
 * @author chenwang
 *
 */
public class FollowupListItem  extends LinearLayout {
	private TextView tvTime;
	private TextView tvRemark;

	public FollowupListItem(Context context) {
		super(context);
	}

	public FollowupListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FollowupListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvTime = (TextView) this.findViewById(R.id.tvTime);
		this.tvRemark = (TextView) this.findViewById(R.id.tvRemark);
	}

	public void show(FollowupInfo followupInfo) {
		this.tvTime.setText(followupInfo.getVisitDate());
		this.tvRemark.setText(followupInfo.getRemark());
	}

}
