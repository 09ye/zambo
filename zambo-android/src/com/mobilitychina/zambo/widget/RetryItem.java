package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 重试Item
 * @author chenwang
 *
 */
public class RetryItem extends RelativeLayout {
	private TextView tvMessage;
	private Button tvRetry;

	public RetryItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RetryItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RetryItem(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.tvMessage = (TextView) this.findViewById(R.id.tvMessage);
		this.tvRetry = (Button) this.findViewById(R.id.btnRetry);
	}

	public void setErrorMessage(String text) {
		this.tvMessage.setText(text);
	}

	public void setRetryOnClickListener(View.OnClickListener listener) {
		this.tvRetry.setOnClickListener(listener);
	}
}
