package com.projekt.flashnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Helper class to detect incoming and outgoing calls.
 * 
 *
 */
public class CallHelper {

	Camera cam;    
	Parameters p;
	boolean isCalling = false;
	Handler handler;
	long blinkDelay;
	
	
	/**
	 * Listener to detect incoming calls. 
	 */
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			//Wenn es klingelt => Blinken
			case TelephonyManager.CALL_STATE_RINGING:
				
				//Kamera starten
				cam = Camera.open();
				p = cam.getParameters();
				
				//Blinkfolge
				String myString = "010101010101";
				
				//Durchlaufen der Abfolge
				for (int i = 0; i < myString.length(); i++) {
				   if (myString.charAt(i) == '0') {
					   p = cam.getParameters();
					   //Blitzlicht an
					   p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					   cam.setParameters(p);
					   cam.startPreview();
				   } else {
					   p = cam.getParameters();
					   //Blitzlicht aus
					   p.setFlashMode(Parameters.FLASH_MODE_OFF);
					   cam.setParameters(p);
				   }
				   
				   try {
					   //Angegebene Dauer pausieren
				      Thread.sleep(blinkDelay);
				   } catch (InterruptedException e) {
				      e.printStackTrace();
				   }
				   
				}
				
				p = cam.getParameters();
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				cam.setParameters(p);
				cam.stopPreview();
				
				//Kamera schließen
				cam.release();
				cam=null;
				
				//Wenn keine Anruf => Beenden
			case TelephonyManager.CALL_STATE_IDLE:
				isCalling = false;
				break;	
			}
		}
	}		
	
	
	Context ctx;
	private TelephonyManager tm;
	private CallStateListener callStateListener;
	
	
	public CallHelper(Context ctx, long bd) {
		blinkDelay = bd+50;
		this.ctx = ctx;
		callStateListener = new CallStateListener();
	}
	
	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
	}

}