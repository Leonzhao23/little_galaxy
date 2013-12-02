package com.little.galaxy.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderOnDemandServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		Log.d(ReminderReceiver.class.getSimpleName(), action);
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
			ctx.startService(new Intent(ctx, ReminderOnDemandService.class));
		}

	}

}
