package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 加载提示Item
 * @author chenwang
 *
 */
public class LoadingItem extends RelativeLayout {
	private TextView tvLoading;

	public LoadingItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LoadingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingItem(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvLoading = (TextView) this.findViewById(R.id.tvLoading);
	}

	public void setLoadingText(String text) {
		this.tvLoading.setText(text);
	}
}
