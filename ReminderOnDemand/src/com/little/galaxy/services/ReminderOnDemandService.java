package com.little.galaxy.services;


import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_SERVICE;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_MEDIA;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandService extends Service {
	private ScheduledExecutorService ses = null;
	private Map<String, ScheduledFuture<?>> scheduleMap = null;
	private ReminderOnDemandBind reminderOnDemandBind = null;
	private NotificationManager nm = null;
	private MediaPlayer mp = null;
	private IDBService dbService = null;
	
	private class ReminderOnDemandCommand implements Runnable{
		
		private ReminderOnDemandEntity entity;
		private Uri recordLoc;

		public ReminderOnDemandCommand(ReminderOnDemandEntity entity) {
			super();
			this.entity = entity;
			this.recordLoc = Uri.parse("file://" + entity.getRecoredLoc()); 		
		}

		@Override
		public void run() {
			sendNotification(R.drawable.ic_launcher, entity.getName(), entity.getRecoredLoc());
			mp = MediaPlayer.create(ReminderOnDemandService.this, recordLoc);	
			try {
				Log.d(TAG_MEDIA, "Begin play the reminder[" + entity.getName() +"]");
				reminderOnDemandBind.play();
			} catch (RemoteException re) {
				Log.e(getClass().getSimpleName(), "MediaPlayer encounters problems", re);
			}
			dbService.updateByState(entity, ReminderOnDemandEntity.ReminderState.Done.getState());
		}
	}


	@Override
	public void onCreate(){
		//android.os.Debug.waitForDebugger();
		super.onCreate();
		Log.d(TAG_SERVICE, "Service created!");
		reminderOnDemandBind = new ReminderOnDemandBind();
		ses = Executors.newSingleThreadScheduledExecutor(); 
		scheduleMap = new ConcurrentHashMap<String, ScheduledFuture<?>>();
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandService.this);
		List <ReminderOnDemandEntity> reminders = dbService.getAllStartedReminders();
		for (ReminderOnDemandEntity reminderOnDemandEntity : reminders) {
			int interval  = reminderOnDemandEntity.getInterval();
			long create_time = reminderOnDemandEntity.getCreateTime();
			long now = System.currentTimeMillis();
			int slot = (int)(now - create_time);
			interval = interval - slot;
		//	ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
		//	timer.schedule(timerTask, interval < 0 ? 0:interval);
			ReminderOnDemandCommand command = new ReminderOnDemandCommand(reminderOnDemandEntity);
			ScheduledFuture<?> sf = ses.schedule(command, interval, TimeUnit.MILLISECONDS);
			scheduleMap.put(String.valueOf(reminderOnDemandEntity.getId()), sf);
			Log.d(TAG_SERVICE, "Task [" + reminderOnDemandEntity.getName() + "] rescheduled. Interval=" + interval);
		}
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG_SERVICE, "Service onStart!");
		
		long id = intent.getLongExtra("newReminderId", System.currentTimeMillis());
		ReminderOnDemandEntity reminderOnDemandEntity = dbService.getReminderById(id);
		if (reminderOnDemandEntity != null){
			int interval  = reminderOnDemandEntity.getInterval();
			//ReminderOnDemandTimerTask timerTask = new ReminderOnDemandTimerTask(reminderOnDemandEntity);
			//timer.schedule(timerTask, interval);
			ReminderOnDemandCommand command = new ReminderOnDemandCommand(reminderOnDemandEntity);
			ScheduledFuture<?>  sf = ses.schedule(command, interval, TimeUnit.MILLISECONDS);
			scheduleMap.put(String.valueOf(reminderOnDemandEntity.getId()), sf);
			Log.d(TAG_SERVICE, "Task [" + reminderOnDemandEntity.getName() + "] scheduled. Interval=" + interval);
			dbService.updateByState(reminderOnDemandEntity, ReminderOnDemandEntity.ReminderState.Start.getState());
		}
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return reminderOnDemandBind;
	}
	
	public class ReminderOnDemandBind extends IPlayService.Stub{
		
		public ReminderOnDemandBind() {
			super();
		}

		@Override
		public void play() throws RemoteException {
			mp.start();	
		}

		@Override
		public void stop(String id) throws RemoteException {
			Log.d(TAG_SERVICE, "Enter Media Play stop()");
			if (mp != null){
				mp.stop();
				Log.d(TAG_SERVICE, "Play stopped!");
			}
			ScheduledFuture<?>  sf = scheduleMap.get(id);
			if (sf != null){
				sf.cancel(true);
				Log.d(TAG_SERVICE, "Task cancelled");
			}
			
		}
		
	}

	@Override
	public void onDestroy() {
		Log.d(TAG_SERVICE, "Service onDestroy!");
		
		if (mp != null){
			mp.reset();
			mp.release();
			mp = null;
		}
	
		if (dbService != null){
			dbService.cleanup();
		}
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
