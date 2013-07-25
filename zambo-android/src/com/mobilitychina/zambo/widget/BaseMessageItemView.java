package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseMessageItemView  extends RelativeLayout {
	public TextView info;
	public ImageView more; // 显示更多的图标
	private final int DEFAULT_TEXT_LINE_COUNT = 4;
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.info = (TextView) this
				.findViewById(R.id.ItemText);
		this.more = (ImageView) this.findViewById(R.id.more);
	}
	
	public BaseMessageItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public BaseMessageItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public BaseMessageItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		this.more.setVisibility((this.info.getLineCount() > DEFAULT_TEXT_LINE_COUNT) ? View.VISIBLE : View.INVISIBLE);
	}

	public void setExpand(boolean expand) {
		if (expand) {
			this.info.setMaxLines(Integer.MAX_VALUE);
			((ImageView) more).setImageResource(R.drawable.arrow_up);
		} else {
			this.info.setMaxLines(DEFAULT_TEXT_LINE_COUNT);
			((ImageView) more).setImageResource(R.drawable.arrow_down);
		}
	}
}
