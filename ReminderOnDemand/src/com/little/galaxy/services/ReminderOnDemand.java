package com.little.galaxy.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderOnDemand {
	
	public void reminder(Context ctx){
		Intent intent = new Intent(ctx, ReminderReceiver.class);
	    intent.setAction("short");
	    PendingIntent sender= PendingIntent.getBroadcast(ctx, 0, intent, 0);
	    
	    Calendar calendar=Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());
	    calendar.add(Calendar.SECOND, 5);
	    
	    AlarmManager alarm=(AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
	    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	    
	    Toast.makeText(ctx, "5 secds", Toast.LENGTH_LONG).show();
	}

} 
