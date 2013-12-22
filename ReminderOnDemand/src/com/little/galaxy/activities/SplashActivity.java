package com.little.galaxy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.little.galaxy.R;

public class SplashActivity extends Activity {
	
	private final static int delayed = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Handler handler = new Handler();
		handler.postDelayed(new SplashHandler(), delayed);
	}
	class SplashHandler implements Runnable{
		public void run(){
			SplashActivity.this.startActivity(
					new Intent(SplashActivity.this.getApplication(), ReminderOnDemandActivity.class));
			SplashActivity.this.finish();
		}
	}

}
