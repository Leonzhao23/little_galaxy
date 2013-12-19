package com.little.galaxy.services;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
	private ReminderOnDemandBind reminderOnDemandBind = null;
	private NotificationManager nm = null;
	private MediaPlayer mp = null;
	private Timer timer = null;
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
			
			sendNotification(R.drawable.ic_launcher, entity.getName(), entity.getRecoredLoc());
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
		super.onCreate();
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandService.this);
		List <ReminderOnDemandEntity> reminders = dbService.getAllStartReminders();
		for (ReminderOnDemandEntity reminderOnDemandEntity : reminders) {
			int interval  = reminderOnDemandEntity.getInterval();
			long create_time = reminderOnDemandEntity.getCreateTime();
			long now = System.currentTimeMillis();
			int slot = (int)(now - create_time);
			interval = interval - slot;
			timer = new Timer();
			ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
			timer.schedule(timerTask, interval < 0 ? 0:interval);
		}
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//android.os.Debug.waitForDebugger();
		List <ReminderOnDemandEntity> reminders = dbService.getAllNewReminders();
		for (ReminderOnDemandEntity reminderOnDemandEntity : reminders) {
			int interval  = reminderOnDemandEntity.getInterval();
			timer = new Timer();
			ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
			Log.d(getClass().getSimpleName(), "delayed=" + interval);
			timer.schedule(timerTask, interval);
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
	
	private void sendNotification(int id, String text, String desc){
		Intent notifyIntent = new Intent(ReminderOnDemandService.this, ReminderOnDemandService.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(ReminderOnDemandService.this, 0, notifyIntent, 0);
		Notification notification = new Notification();
		notification.icon = id;
		notification.tickerText = text;
		notification.defaults = Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(ReminderOnDemandService.this, text, desc, pendingIntent);
		nm.notify(0, notification);
	}

}
