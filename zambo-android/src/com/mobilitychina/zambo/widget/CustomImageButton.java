package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的图片按钮，左边带icon，右边文字，底部有背景图片
 * 
 * @author chenwang
 * 
 */
public class CustomImageButton extends RelativeLayout {
	private ImageView ivButtomImage;
	private TextView tvButtonTitle;
	private Button btnAction;

	public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.btnAction = (Button) this.findViewById(R.id.btnAction);
		this.tvButtonTitle = (TextView) this.findViewById(R.id.tvButtonTitle);
		this.ivButtomImage = (ImageView) this.findViewById(R.id.ivButtonImage);
	}

	public void setBackgroundImageResource(int resId) {
		this.btnAction.setBackgroundResource(resId);
	}

	public void setImageResource(int resId) {
		this.ivButtomImage.setImageResource(resId);
	}

	public void setText(String text) {
		this.tvButtonTitle.setText(text);
	}

	public void setOnClickListener(View.OnClickListener listener) {
		this.btnAction.setOnClickListener(listener);
	}
}
