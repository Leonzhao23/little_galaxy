package com.little.galaxy.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.little.galaxy.R;


@SuppressLint("NewApi")
public class ReminderOnDemandUtil {
	
	public static void showNotification(Context ctx, Intent intent, String msg){
		NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
		Builder builder = new Notification.Builder(ctx);
		//new Notification(R.drawable.ic_launcher, "ReminderOnDemand...", System.currentTimeMillis());
		//builder = new Notification.Builder(MainActivity.this);
		builder.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
	   			//.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))
	   			.setTicker("reminder on demand ...") 
	   			.setWhen(System.currentTimeMillis())
	   			.setAutoCancel(true)
	            .setContentTitle("This is ContentTitle")
	            .setContentText("this is ContentText");
		
	//	notification.setLatestEventInfo(ctx, "hello", msg, pendingIntent);
	//	Notification notification = builder.getNotification();
		nm.cancel(0);
		nm.notify(0, builder.build());
		
		
	}

}
