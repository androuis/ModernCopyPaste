package com.andreibacalu.android.copied.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;

public class TransparentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transparent);
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (intent != null) {
			intent.setClass(this, ChangeNotificationTextService.class);
			startService(intent);
			finish();
		}
	}
}
