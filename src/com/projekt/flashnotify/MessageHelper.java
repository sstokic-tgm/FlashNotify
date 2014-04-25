package com.projekt.flashnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class MessageHelper  extends BroadcastReceiver{
	Camera cam;    
	Parameters p;
	private SharedPreferences preferences;

	@Override
	public void onReceive(Context context, Intent intent) {

		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){

			//Startet die Kamera
			cam = Camera.open();
			p = cam.getParameters();

			//Jede 01 ist ein Blinken
			String myString = "010101";

			//Blinkdauer in ms
			long blinkDelay = 250;
			for (int i = 0; i < myString.length(); i++) {
				//Wenn aktuelle Textstelle 0 => Licht an
				if (myString.charAt(i) == '0') {
					p = cam.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					cam.setParameters(p);
					cam.startPreview();
					//Sonst => Licht aus
				} else {
					p = cam.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_OFF);
					cam.setParameters(p);
				}

				try {
					//Die angegebene Dauer warten
					Thread.sleep(blinkDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			cam.setParameters(p);
			cam.stopPreview();
			//Kamera schlieﬂen
			cam.release();
			cam=null;
		}
	}


	public MessageHelper() {

	}
}
