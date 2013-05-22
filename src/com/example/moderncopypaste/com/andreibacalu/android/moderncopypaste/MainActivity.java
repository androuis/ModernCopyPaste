package com.example.moderncopypaste.com.andreibacalu.android.moderncopypaste;

import com.andreibacalu.android.moderncopypaste.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.hello_world);
		textView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				addNotification();
			}
		});
	}
	
	public void nextClicked(View view) {
		Toast.makeText(getBaseContext(), "dsadad", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	protected void addNotification() {
		// Prepare intent which is triggered if the
		// notification is selected

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		CharSequence tickerText = "Shortcuts";
		long when = System.currentTimeMillis();
		Notification.Builder builder = new Notification.Builder(this);
		Notification notification=builder.getNotification();
		notification.when=when;
		notification.tickerText=tickerText;
		notification.icon=R.drawable.ic_launcher;
		
		RemoteViews contentView=new RemoteViews(this.getPackageName(), R.layout.notification_layout);
		
		//set the button listeners
		setListeners(contentView);
		
		notification.contentView = contentView;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		CharSequence contentTitle = "From Shortcuts";
		mNotificationManager.notify(548853, notification);
		
		/*Intent intent = new Intent(this, Activity2.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!")
		        .setOngoing(false)
		        .addAction(R.drawable.ic_launcher, "1", pIntent)
		        .addAction(R.drawable.ic_launcher, "1", pIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());*/
	}

	private void setListeners(RemoteViews rv) {
		Intent radio = new Intent(this, Activity2.class); 
		radio.putExtra("DO", "volume");//if necessary

		PendingIntent pRadio = PendingIntent.getActivity(this, 0, radio, 0);

		rv.setOnClickPendingIntent(R.id.text_previous, pRadio);		
	}

	
	
}
