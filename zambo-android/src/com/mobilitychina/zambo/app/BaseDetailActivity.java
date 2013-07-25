package com.mobilitychina.zambo.app;

import android.view.View;
import android.view.View.OnClickListener;

import com.mobilitychina.zambo.R;

public class BaseDetailActivity extends BaseActivity {
	private DetailTitlebar mDetailTitlebar;

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mDetailTitlebar = (DetailTitlebar) this.findViewById(R.id.detailTitlebar);
		if (mDetailTitlebar == null) {
			throw new RuntimeException("Your content must have a DetailTitlebar whose id attribute is "
					+ "'R.id.detailtitlebar'");
		}
		mDetailTitlebar.getBackButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void setTitle(String title) {
		if (mDetailTitlebar != null) {
			mDetailTitlebar.setTitle(title);
		}
	}

	public DetailTitlebar getTitlebar() {
		return mDetailTitlebar;
	}
}
