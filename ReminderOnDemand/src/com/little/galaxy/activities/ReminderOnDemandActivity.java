package com.little.galaxy.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;
import com.little.galaxy.views.ReminderOnDemandView;

public class ReminderOnDemandActivity extends Activity implements OnItemClickListener {

    private Button speakBtn = null;
    private Button stopBtn = null;
    private Button doneReminderBtn = null;
    private Button startReminderBtn = null;
    private ListView reminderStartListView = null;
    private ListView reminderDoneListView = null;
	private IDBService dbService = null;
	private IPlayService playService = null;
	private Timer timer = null;
	
	private Handler startViewHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
			List<ReminderOnDemandEntity> ReminderOnDemandEntities = dbService.getAllStartReminders();
			List<Map<String, String>> listMap =  getMapsForSimpleAdaptor(ReminderOnDemandEntities);
			if (listMap.size() > 0){
				startReminderBtn.setVisibility(View.VISIBLE);
				  // setup data adapter
				SimpleAdapter adapter = new SimpleAdapter(ReminderOnDemandActivity.this, listMap, android.R.layout.simple_list_item_2, 
						new String[]{getResources().getString(R.id.subject), getResources().getString(R.id.desc)},
						new int[]{R.id.subject,R.id.desc});
		        // assign adapter to list view
				reminderStartListView.setAdapter(adapter);
			}
        }
		
	};
	
	private Handler doneViewHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
			List<ReminderOnDemandEntity> ReminderOnDemandEntities = dbService.getFilterReminders(new String[]{"2", "0", "2"});
			if (ReminderOnDemandEntities.size() > 0){
				doneReminderBtn.setVisibility(View.VISIBLE);
				  // setup data adapter
				ReminderOnDemandView adapter = new ReminderOnDemandView(ReminderOnDemandActivity.this, ReminderOnDemandEntities);
		        // assign adapter to list view
				reminderDoneListView.setAdapter(adapter);
				
			}
	      
        }
		
	};
	
	private TimerTask timerTask = new TimerTask(){

		@Override
		public void run() {
			startViewHandler.sendEmptyMessage(0);	
		}
		
	};
	
	private TimerTask timerTask1 = new TimerTask(){

		@Override
		public void run() {
			doneViewHandler.sendEmptyMessage(0);	
		}
		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
        reminderStartListView = (ListView) findViewById(R.id.reminder_start_view);
        reminderDoneListView = (ListView) findViewById(R.id.reminder_done_view);
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandActivity.this);
        
        timer = new Timer();
        timer.schedule(timerTask, 0, 1*60*100);
        timer.schedule(timerTask1, 0, 1*60*100);
        Log.d(getClass().getSimpleName(), "onCreate() invoked, timer.schedule invoked");
        
        speakBtn = (Button)findViewById(R.id.button1);
        stopBtn = (Button)findViewById(R.id.stop);
        startReminderBtn = (Button)findViewById(R.id.button_start);
        startReminderBtn.setVisibility(View.INVISIBLE);
        doneReminderBtn = (Button)findViewById(R.id.button2);
        doneReminderBtn.setVisibility(View.INVISIBLE);
       
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
        
       startReminderBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandViewActivity.class);
				intent.putExtra("type", "start");
				ReminderOnDemandActivity.this.startActivity(intent);
			}
		});
        
        doneReminderBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandViewActivity.class);
				intent.putExtra("type", "done");
				ReminderOnDemandActivity.this.startActivity(intent);
			}
		});
        
        
        	
    }
    
    @Override
	protected void onDestroy() {
    	Log.d(getClass().getSimpleName(), "onDestroy() invoked, timer.cancel() invoked");
    	timer.cancel();
    	timer = null;
    	if (dbService != null){
    		dbService.cleanup();
    	}
    	playService = null;
		super.onDestroy();
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
    
    private List<Map<String, String>> getMapsForSimpleAdaptor(final List<ReminderOnDemandEntity> reminderOnDemandEntities){
    	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    	for(ReminderOnDemandEntity entity: reminderOnDemandEntities){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put(getResources().getString(R.id.subject), entity.getName() + "(scheduled at " + entity.getInterval() +")");
        	map.put(getResources().getString(R.id.desc), entity.getRecoredLoc());
        	list.add(map);
    	}
    	return list;
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
