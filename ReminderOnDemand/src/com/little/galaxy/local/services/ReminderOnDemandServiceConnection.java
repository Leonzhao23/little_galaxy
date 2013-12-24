package com.little.galaxy.local.services;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.little.galaxy.services.IPlayService;

public class ReminderOnDemandServiceConnection implements ServiceConnection {
	
	private IPlayService playService;

	@Override
	public void onServiceConnected(ComponentName name, IBinder bind) {
		playService = IPlayService.Stub.asInterface(bind);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		playService = null;
	}

	public IPlayService getPlayService() {
		return playService;
	}

}
