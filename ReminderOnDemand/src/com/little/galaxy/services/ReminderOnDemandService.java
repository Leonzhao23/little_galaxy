package com.little.galaxy.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReminderOnDemandService extends Service {

	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ReminderOnDemand demand = new ReminderOnDemand();
    	demand.reminder(ReminderOnDemandService.this);
		return super.onStartCommand(intent, flags, startId);
	}
	

}
