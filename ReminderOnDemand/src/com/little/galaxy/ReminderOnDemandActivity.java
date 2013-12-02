package com.little.galaxy;

import com.little.galaxy.services.IPlayService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ReminderOnDemandActivity extends Activity {
    private boolean isRecording = true;
    private IPlayService playService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
        Button speakBtn = (Button)findViewById(R.id.button1);
        Button stopBtn = (Button)findViewById(R.id.stop);
        bindService(new Intent("ReminderOnDemandService"), serviceConnection, Context.BIND_AUTO_CREATE);
       
        speakBtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(getClass().getSimpleName(), "speakBtn Touch event");
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
				{
					Log.d(getClass().getSimpleName(), "speakBtn Touch event: ACTION_DOWN");
					//doRecord();
				}
				case MotionEvent.ACTION_UP:
				{
					Log.d(getClass().getSimpleName(), "speakBtn Touch event: ACTION_UP");
					//stopRecord();
					ReminderOnDemandActivity.this.startActivity(new Intent(ReminderOnDemandActivity.this, ReminderOnDemandSettingsActivity.class));
				}
				default:
				 	break;
				}
				return false;
			}
		});
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					if (playService != null){
						playService.stop();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
        	
     
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_on_demand, menu);
        return true;
    }
    
    private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder bind) {
			playService = IPlayService.Stub.asInterface(bind);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			playService = null;
		} 
		
	};
   
    
}
