package com.little.galaxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if(intent.getAction().equals("short")){
			Toast.makeText(ctx, "short alarm", Toast.LENGTH_LONG).show();
			ReplayOnDemand play = new ReplayOnDemand();
			play.play(ctx);
			} else{
				Toast.makeText(ctx, "repeating alarm",
				Toast.LENGTH_LONG).show();
			}
		
	}

}
