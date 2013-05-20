package com.example.moderncopypaste;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationHandler extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Toast.makeText(this, "TEST", Toast.LENGTH_LONG).show();
	}

}
