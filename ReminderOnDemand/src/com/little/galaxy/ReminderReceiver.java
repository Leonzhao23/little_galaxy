package com.little.galaxy;

import com.little.galaxy.utils.ReminderOnDemandUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if(intent.getAction().equals("short")){
			//Toast.makeText(ctx, "short alarm", Toast.LENGTH_LONG).show();
			ReminderOnDemandUtil.showNotification(ctx, intent, "reminderondemand...");
			ReplayOnDemand play = new ReplayOnDemand();
			play.play(ctx);
			} else{
				Toast.makeText(ctx, "repeating alarm",
				Toast.LENGTH_LONG).show();
			}
		
	}

}
