package com.andreibacalu.android.moderncopypaste;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class Activity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String action= (String)getIntent().getExtras().get("DO");
		if(action.equals("radio")){
			boolean isEnabled = Settings.System.getInt
					(getContentResolver(), 
							Settings.System.AIRPLANE_MODE_ON, 0) == 1;
			Settings.System.putInt(getContentResolver(),Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", !isEnabled);
			sendBroadcast(intent);
		}
		else if(action.equals("volume")){
			AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
			if(mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)
	    		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	    	else
	    		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
		else if(action.equals("reboot")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Î£Î¯Î³Î¿�?…�?�Î± �?�Îµ?")
	    	       .setPositiveButton("Î�Î±Î¹ �?�Îµ!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   try {
	        	   				Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
	        	   				proc.waitFor();
	        	   			} 
		        	    	catch (IOException ioe){
		        	    		Toast.makeText(getBaseContext(), "IOException:", Toast.LENGTH_LONG).show();
		        	        }
		        	    	catch (InterruptedException ie){
		        	    		Toast.makeText(getBaseContext(), "InterruptedException: Î•Î¯�?ƒÎ±Î¹ �?ƒÎ¯Î³Î¿�?…�?�Î± root?", Toast.LENGTH_LONG).show();
		        	    	}   	        	   	
	    	           }
	    	       })
	    	       .setNegativeButton("Îœ�?€Î¬Î±!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   dialog.cancel();
	    	        	   ((Activity) getBaseContext()).finish();
	    	           }
	    	       });
	    	AlertDialog alert = builder.create();
	    	alert.setCancelable(true);
	    	alert.setCanceledOnTouchOutside(false);
	    	alert.show();
		}
		else if(action.equals("top")){

		}
		else if(action.equals("app")){

		}
		/*if(!action.equals("top")){
			try { 
			   Object service = getSystemService ("statusbar"); 
			   Class <?> statusBarManager = Class.forName 
				("android.app.StatusBarManager"); 
			   //Use expand instead of collapse to view the notification area
			   Method collapse = statusBarManager.getMethod ("collapse"); 
			   collapse.invoke (service); 
			} catch (Exception e) { 
			   Toast.makeText(getApplicationContext(), e.getMessage(), 
				Toast.LENGTH_LONG).show(); 
			} 
		}*/
		if(!action.equals("reboot"))
			finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
