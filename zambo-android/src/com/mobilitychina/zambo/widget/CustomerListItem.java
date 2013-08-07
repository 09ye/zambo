package com.mobilitychina.zambo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;

/**
 * 客户列表的Item
 * 
 * @author chenwang
 * 
 */
public class CustomerListItem extends RelativeLayout {
	private ImageView ivFavorite;
	private TextView tvTitle;
	private TextView tvVisitNum;
	private ImageView ivSeperator;
	private ImageView ivYearVisit;
	private ProgressBar progressVisitNum;
	private ImageView ivCheckbox;
	private ImageView ivArrow;

	public CustomerListItem(Context context) {
		super(context);
	}

	public CustomerListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomerListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.ivFavorite =  (ImageView) this.findViewById(R.id.ivFavorite);
		this.tvTitle = (TextView) this.findViewById(R.id.title);
		this.tvVisitNum = (TextView) this.findViewById(R.id.tvVisitNum);
		this.ivYearVisit = (ImageView) this.findViewById(R.id.ivYearVisit);
		this.ivSeperator = (ImageView) this.findViewById(R.id.ivSeperator);
		this.progressVisitNum = (ProgressBar) this.findViewById(R.id.progressVisitNum);
		this.ivArrow = (ImageView) this.findViewById(R.id.ivArrow);
		this.ivCheckbox = (ImageView) this.findViewById(R.id.ivCheckbox);
		
		this.showCheckbox(false);
	}

	/**
	 * 显示客户信息
	 * 
	 * @param customerInfo
	 */
	public void show(CustomerInfo customerInfo) {
		this.tvTitle.setText(customerInfo.getCustName());
		this.tvVisitNum.setText(customerInfo.getVisitedNum() + "/" + customerInfo.getPlanVisitNum());

		if (customerInfo.getPlanVisitNum() > 0) {
			this.progressVisitNum.setProgress((float) customerInfo.getVisitedNum() / customerInfo.getPlanVisitNum());
		}else{
			this.progressVisitNum.setProgress(0);
		}
		if (customerInfo.getTotalVisitNum() > 0) {
			this.ivYearVisit.setVisibility(View.VISIBLE);
		} else {
			this.ivYearVisit.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否需要显示分隔线
	 * 
	 * @param show
	 */
	public void showSeperator(boolean show) {
		if (show) {
			this.ivSeperator.setVisibility(View.VISIBLE);
		} else {
			this.ivSeperator.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 设置收藏状态
	 * 
	 * @param select
	 */
	public void setFavoriteSelect(boolean select) {
		if (select) {
			this.ivFavorite.setImageResource(R.drawable.icon_favorite_selected);
		} else {
			this.ivFavorite.setImageResource(R.drawable.icon_favorite);
		}
	}
	/**
	 * 是否需要显示多选框
	 * 
	 * @param show
	 */
	public void showCheckbox(boolean show) {
		if (show) {
			this.ivArrow.setVisibility(View.GONE);
			this.ivCheckbox.setVisibility(View.VISIBLE);
		} else {
			this.ivArrow.setVisibility(View.VISIBLE);
			this.ivCheckbox.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置选中态
	 */
	public void setSelected(boolean selected) {
		ivCheckbox.setSelected(selected);
	}
}
