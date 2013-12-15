package com.little.galaxy.activities;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;
import com.little.galaxy.views.ReminderOnDemandView;

public class ReminderOnDemandActivity extends Activity implements OnItemClickListener {
    private boolean isRecording = true;
    private IPlayService playService = null;
    private ListView reminderListView = null;
	private IDBService dbService = null;
	private Timer timer = null;
	
	private Handler startViewHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
			List<ReminderOnDemandEntity> ReminderOnDemandEntities = dbService.getAllStartReminders();
	        // setup data adapter
	        ArrayAdapter<ReminderOnDemandEntity> adapter = new ArrayAdapter<ReminderOnDemandEntity>(ReminderOnDemandActivity.this, android.R.layout.simple_list_item_1, ReminderOnDemandEntities);
	        // assign adapter to list view
	        reminderListView.setAdapter(adapter);
	        // install handler
	        // hilight the first entry in the list...
	        reminderListView.setSelection(0);
        }
		
	};
	
	private TimerTask timerTask = new TimerTask(){

		@Override
		public void run() {
			startViewHandler.sendEmptyMessage(0);		
		}
		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
        reminderListView = (ListView) findViewById(R.id.reminder_start_view);
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandActivity.this);
        
        Button speakBtn = (Button)findViewById(R.id.button1);
        Button stopBtn = (Button)findViewById(R.id.stop);
        Button viewBtn = (Button)findViewById(R.id.button2);
      
       
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
					bindService(new Intent("ReminderOnDemandService"), serviceConnection, Context.BIND_AUTO_CREATE);
					if (playService != null){
						playService.stop();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
        
        viewBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReminderOnDemandActivity.this.startActivity(new Intent(ReminderOnDemandActivity.this, ReminderOnDemandViewActivity.class));
			}
		});
        	
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
        timer = new Timer();
        timer.schedule(timerTask, 0, 1*60*100);
    }
    
    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
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
