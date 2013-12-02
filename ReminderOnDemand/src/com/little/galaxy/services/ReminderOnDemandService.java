package com.little.galaxy.services;


import com.little.galaxy.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

public class ReminderOnDemandService extends Service {
	private ReminderOnDemandBind reminderOnDemandBind;
	private MediaPlayer mp;

	@Override
	public void onCreate(){
		if (mp == null){
			mp = MediaPlayer.create(ReminderOnDemandService.this, R.raw.test);	
		}
		if (reminderOnDemandBind == null){
			reminderOnDemandBind = new ReminderOnDemandBind();
		}
		try {
			reminderOnDemandBind.play();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return reminderOnDemandBind;
	}
	
	public class ReminderOnDemandBind extends IPlayService.Stub{

		@Override
		public void play() throws RemoteException {
			mp.start();	
		}

		@Override
		public void stop() throws RemoteException {
			mp.stop();
		}
		
	}

	@Override
	public void onDestroy() {
		mp.stop();
		super.onDestroy();
	}
	

}
