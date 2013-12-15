package com.little.galaxy.services;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandService extends Service {
	private ReminderOnDemandBind reminderOnDemandBind;
	private MediaPlayer mp;
	private Timer timer;
	private IDBService dbService = null;
	
	private class ReminderOnDemandTimerTask extends TimerTask{
		
		private ReminderOnDemandEntity entity;

		public ReminderOnDemandTimerTask(ReminderOnDemandEntity entity) {
			super();
			this.entity = entity;
			//this.recordLoc = Uri.parse("android.resource://ReminderOnDemand/raw/test.mp3"); 		
		}


		@Override
		public void run() {
			mp = MediaPlayer.create(ReminderOnDemandService.this, R.raw.test);	
			reminderOnDemandBind = new ReminderOnDemandBind();
			try {
				reminderOnDemandBind.play();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			dbService.updateByState(entity, ReminderOnDemandEntity.ReminderState.Done.getState());
		}
	}


	@Override
	public void onCreate(){
		//android.os.Debug.waitForDebugger();
		dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandService.this);
		super.onCreate();
		List <ReminderOnDemandEntity> reminders = dbService.getAllStartReminders();
		for (ReminderOnDemandEntity reminderOnDemandEntity : reminders) {
			int delayed  = reminderOnDemandEntity.getDelayed();
			long create_time = reminderOnDemandEntity.getCreate_time();
			long now = System.currentTimeMillis();
			int slot = (int)(now - create_time);
			delayed = delayed - slot;
			timer = new Timer();
			ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
			timer.schedule(timerTask, delayed < 0 ? 0:delayed);
		}
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//android.os.Debug.waitForDebugger();
		List <ReminderOnDemandEntity> reminders = dbService.getAllNewReminders();
		for (ReminderOnDemandEntity reminderOnDemandEntity : reminders) {
			int delayed  = reminderOnDemandEntity.getDelayed();
			timer = new Timer();
			ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
			Log.d(getClass().getSimpleName(), "delayed=" + delayed);
			timer.schedule(timerTask, delayed);
			dbService.updateByState(reminderOnDemandEntity, ReminderOnDemandEntity.ReminderState.Start.getState());
		}
		return super.onStartCommand(intent, flags, startId);
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
		dbService.cleanup();
		super.onDestroy();
	}
	

}
