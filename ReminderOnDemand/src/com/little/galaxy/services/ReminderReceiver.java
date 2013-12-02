package com.little.galaxy.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.little.galaxy.utils.ReminderOnDemandUtil;

public class ReminderReceiver extends BroadcastReceiver{
	private IPlayService playService = null;
	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		Log.d(ReminderReceiver.class.getSimpleName(), action);
		if (action.equals("short")){
			//Toast.makeText(ctx, "short alarm", Toast.LENGTH_LONG).show();
			ReminderOnDemandUtil.showNotification(ctx, intent, "reminderondemand...");
			ctx.bindService(new Intent("ReminderOnDemandService"), serviceConnection, Context.BIND_AUTO_CREATE);
			try {
				playService.play();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder bind) {
			playService = IPlayService.Stub.asInterface(bind);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			playService = null;
		} 
		
	};

}
