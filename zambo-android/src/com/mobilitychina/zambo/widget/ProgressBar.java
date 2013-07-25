package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobilitychina.zambo.R;

public class ProgressBar extends RelativeLayout {
	private LinearLayout layoutProgress;
	private float progressValue;

	public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.layoutProgress = (LinearLayout) this.findViewById(R.id.layoutProgress);
	}

	private void updateProgress() {
		float totalWidth = this.getLayoutParams().width;
		int width = (int) (totalWidth * progressValue);
		if (width < 10) { // 最小的宽度为10像素
			this.layoutProgress.setVisibility(View.INVISIBLE);
		} else {
			this.layoutProgress.setVisibility(View.VISIBLE);
			this.layoutProgress.setLayoutParams(new LayoutParams(width, this.getLayoutParams().height));
		}
	}

	/**
	 * 设置进度条，范围在0至1之前的浮点数
	 * 
	 * @param value
	 */
	public void setProgress(float value) {
		if (value < 0 || value > 1) {
			return;
		}
		progressValue = value;
		this.updateProgress();
	}
}
