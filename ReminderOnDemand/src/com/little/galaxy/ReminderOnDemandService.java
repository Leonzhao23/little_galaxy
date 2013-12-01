package com.little.galaxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReminderOnDemandService extends Service {

	@Override
	public void onCreate(){
		ReminderOnDemand demand = new ReminderOnDemand();
    	demand.reminder(ReminderOnDemandService.this);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
