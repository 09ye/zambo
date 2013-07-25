package com.mobilitychina.zambo.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;

public class BaseTitlebar extends RelativeLayout {
	private TextView titleText;
	private Button leftButton;
	private Button backButton;
	private Button rightButton;

	public BaseTitlebar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BaseTitlebar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseTitlebar(Context context) {
		super(context);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		leftButton = (Button) findViewById(R.id.left_button);
		backButton = (Button) findViewById(R.id.back_button);
		titleText = (TextView) findViewById(android.R.id.title);
		rightButton = (Button) findViewById(R.id.right_button);

		leftButton.setVisibility(View.INVISIBLE);
		rightButton.setVisibility(View.INVISIBLE);
		backButton.setVisibility(View.GONE);
	}

	public void setTitle(CharSequence title) {
		if (titleText != null)
			titleText.setText(title);
	}

	public void setBackButton(String text) {
		backButton.setText(text);
		backButton.setVisibility(View.VISIBLE);
		leftButton.setVisibility(View.GONE);
	}

	public void setBackButton(String text, OnClickListener listener) {
		this.setBackButton(text);
		this.backButton.setOnClickListener(listener);
	}
	
	public void setLeftButton(String text, OnClickListener listener) {
		leftButton.setText(text);
		leftButton.setOnClickListener(listener);
		leftButton.setVisibility(View.VISIBLE);
		backButton.setVisibility(View.GONE);
	}

	public void setRightButton(String text, OnClickListener listener) {
		rightButton.setText(text);
		rightButton.setOnClickListener(listener);
		rightButton.setVisibility(View.VISIBLE);
	}

	public void setRightButton(int resId,OnClickListener listener){
		if(resId > 0){
			rightButton.setText("");
			rightButton.setBackgroundResource(resId);
			rightButton.setOnClickListener(listener);
			rightButton.setVisibility(View.VISIBLE);
		}
	}
	
	public Button getLeftTitleButton() {
		return leftButton;
	}

	public Button getBackButton() {
		return backButton;
	}

	public Button getRightTitleButton() {
		return rightButton;
	}
}
