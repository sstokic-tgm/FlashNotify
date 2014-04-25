package com.projekt.flashnotify;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;

/**
 * Call detect service. 
 * This service is needed, because MainActivity can lost it's focus,
 * and calls will not be detected.
 * 
 * 
 *
 */
public class CallDetectService extends Service {
	private MessageHelper messageHelper;
	private CallHelper callHelper;
	boolean call = false;
	boolean message = false;
	boolean whatsapp = false;
	boolean alarm = false;
 
    public CallDetectService() {
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int res = super.onStartCommand(intent, flags, startId);
		if(intent.getBooleanExtra("call",false)){
			callHelper = new CallHelper(this,intent.getLongExtra("call", 450));
			callHelper.start();
			call=true;
		}
		
		if(intent.getBooleanExtra("message",false)) {
			IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
			messageHelper=new MessageHelper();
			registerReceiver(messageHelper,filter);
			message=true;
		}
		
		if(intent.getBooleanExtra("whatsapp",false)) {
			whatsapp=true;
		}
		
		if(intent.getBooleanExtra("alarm",false)) {
			alarm=true;
		}
		
		showNotificationStartService();
		return res;	
	}
	
    @Override
	public void onDestroy() {
		super.onDestroy();
		if(call) {
			callHelper.stop();
		}	
		if(message) {
			unregisterReceiver(messageHelper);
			messageHelper=null;
		}	
		showNotificationStopService();
	}

	@Override
    public IBinder onBind(Intent intent) {
		// not supporting binding
    	return null;
    }
	
	
	
	public void showNotificationStartService(){
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		int icon = R.drawable.ic_launcher;
        CharSequence text = "Nachrichten werden wahrgenommen";
        CharSequence contentTitle = "FlashNotify";
        CharSequence contentText = "Nachrichten werden wahrgenommen.";
        long when = System.currentTimeMillis();
		
        Notification mNotification = new Notification(icon,text,when);
        
		mNotification.flags |= Notification.FLAG_NO_CLEAR;
		
		mNotification.setLatestEventInfo(this, contentTitle, contentText, null);
		
	    notificationManager.notify(0, mNotification);
	}
	
	public void showNotificationStopService(){
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		int icon = R.drawable.ic_launcher;
        CharSequence text = "Nachrichten werden nicht wahrgenommen";
        CharSequence contentTitle = "FlashNotify";
        CharSequence contentText = "Nachrichten werden nicht wahrgenommen.";
        long when = System.currentTimeMillis();
		
        Notification mNotification = new Notification(icon,text,when);
        
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		mNotification.setLatestEventInfo(this, contentTitle, contentText, null);
		
	    notificationManager.notify(0, mNotification);
	}
}