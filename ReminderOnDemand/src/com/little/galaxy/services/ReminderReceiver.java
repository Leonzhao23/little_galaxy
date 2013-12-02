package com.little.galaxy.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.little.galaxy.utils.ReminderOnDemandUtil;

public class ReminderReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		Log.d(ReminderReceiver.class.getSimpleName(), action);
		if (action.equals("short")){
			//Toast.makeText(ctx, "short alarm", Toast.LENGTH_LONG).show();
			ReminderOnDemandUtil.showNotification(ctx, intent, "reminderondemand...");
			ReplayOnDemand play = new ReplayOnDemand();
			play.play(ctx);
		}
	}

}
