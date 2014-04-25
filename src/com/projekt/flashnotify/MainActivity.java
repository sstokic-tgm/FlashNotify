package com.projekt.flashnotify;

import android.app.*;
import android.app.ActivityManager.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Main activity, with button to toggle phone calls detection on and off.
 * 
 *
 */
public class MainActivity extends Activity {

	private boolean detectEnabled = false;
	
	private TextView textViewDetectState;
	private Button buttonToggleDetect;
	private Button buttonExit;
	private CheckBox call_box;
	private CheckBox message_box;
	private CheckBox whatsapp_box;
	private CheckBox alarm_box;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textViewDetectState = (TextView) findViewById(R.id.textViewDetectState);
        buttonToggleDetect = (Button) findViewById(R.id.buttonDetectToggle);
        call_box = (CheckBox) findViewById(R.id.call_box);
    	message_box = (CheckBox) findViewById(R.id.message_box);
    	whatsapp_box = (CheckBox) findViewById(R.id.whatsapp_box);
    	alarm_box = (CheckBox) findViewById(R.id.alarm_box);
        
        if(isServiceRunning() == true){
			buttonToggleDetect.setText("Turn off");
    		textViewDetectState.setText("Detecting");
    		
    		call_box.setChecked(true);
    		message_box.setChecked(true);
    		
    		call_box.setEnabled(false);
    		message_box.setEnabled(false);
		}
        
        buttonToggleDetect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(call_box.isChecked() || message_box.isChecked()) {
					
					if(isServiceRunning() == true){	
						setDetectEnabled(false);
					}else{	
						setDetectEnabled(true);
					}
				}
			}
		});
        
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				MainActivity.this.finish();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void setDetectEnabled(boolean enable) {
    	
    	detectEnabled = enable;
    	
        Intent intent = new Intent(this, CallDetectService.class);
	        intent.putExtra("call", call_box.isChecked());
	        intent.putExtra("message", message_box.isChecked());
	        intent.putExtra("whatsapp", whatsapp_box.isChecked());
	        intent.putExtra("alarm", alarm_box.isChecked());
        
    	if (enable) {
    		 // start detect service 
            startService(intent);
            
            buttonToggleDetect.setText("Turn off");
    		textViewDetectState.setText("Detecting");
    		call_box.setEnabled(false);
    		message_box.setEnabled(false);
    	}
    	else {
    		// stop detect service
    		stopService(intent);
    		
    		buttonToggleDetect.setText("Turn on");
    		textViewDetectState.setText("Not detecting");
    		call_box.setEnabled(true);
    		message_box.setEnabled(true);
    	}
    }
    
    @Override
	protected void onResume() {
		super.onResume();   
	}

	@Override
	protected void onPause() {
		super.onPause();   
	}
	
	private boolean isServiceRunning() {
		  ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		  for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		    if ("com.projekt.flashnotify.CallDetectService".equals(service.service.getClassName())) {
		      return true;
		    }
		  }
		  return false;
	}

}
