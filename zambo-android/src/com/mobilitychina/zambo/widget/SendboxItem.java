package com.mobilitychina.zambo.widget;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.today.data.MessageInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SendboxItem extends RelativeLayout{

	private ImageView mImageView;
	private TextView mTextTitle;
	private TextView mTextContent;
	public SendboxItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public SendboxItem(Context context) {
		super(context);
	}

	public SendboxItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setMessageValue(MessageInfo info){
		mTextTitle.setText(info.getSendDate());
		mTextContent.setText(info.getSmscontent());
		if(info.getSmstype().equalsIgnoreCase(MessageInfo.MSGLeader)){
			mImageView.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mImageView = (ImageView)this.findViewById(R.id.imgRight);
		mTextTitle = (TextView)this.findViewById(R.id.txtTitle);
		mTextContent = (TextView)this.findViewById(R.id.txtContent);
	}
}
