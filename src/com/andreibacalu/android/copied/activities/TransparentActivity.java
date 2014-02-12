package com.andreibacalu.android.copied.activities;

import com.andreibacalu.android.copied.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class TransparentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transparent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
			}
		}, 200);
	}
}
