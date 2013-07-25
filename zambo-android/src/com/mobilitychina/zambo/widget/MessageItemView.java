package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilitychina.zambo.R;

public class MessageItemView extends BaseMessageItemView {
	public TextView title;
	public TextView source;
	public Button refuseBtn;
	public Button repokBtn;
	public Button repread;
	public Button btn_see_reply;
	public TextView smsTotalId;
	public TextView listsize;
	public TextView txtSection;
	public ImageView more; // 显示更多的图标

	public MessageItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MessageItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MessageItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.title = (TextView) this.findViewById(R.id.ItemTitle);
		this.source = (TextView) this.findViewById(R.id.ItemSource);

		this.smsTotalId = (TextView) this.findViewById(R.id.smsTotalId);
		this.refuseBtn = (Button) this.findViewById(R.id.btn_refuse);
		this.repokBtn = (Button) this.findViewById(R.id.btn_repok);
		this.repread = (Button) this.findViewById(R.id.btn_read);

		this.btn_see_reply = (Button) this.findViewById(R.id.btn_see_reply);
		this.listsize = (TextView) this.findViewById(R.id.listsize);
		this.txtSection = (TextView)this.findViewById(R.id.txtSection);
	}
}
