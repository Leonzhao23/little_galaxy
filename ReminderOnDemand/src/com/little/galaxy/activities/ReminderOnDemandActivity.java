package com.little.galaxy.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.little.galaxy.R;
import com.little.galaxy.RecordOnDemand;
import com.little.galaxy.adaptors.ReminderOnDemandListViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandPagerAdaptor;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandActivity extends Activity implements OnItemClickListener {

    private ImageButton speakBtn = null;
    private ViewPager viewPager = null;
    private ArrayList<String> titles = null;
    private ArrayList<View> pagesArrayList;
    private ReminderOnDemandPagerAdaptor pagerAdaptor = null;
    private Button doneReminderBtn = null;
    private Button startReminderBtn = null;
    private ListView reminderStartListView = null;
    private ListView reminderDoneListView = null;
	private IDBService dbService = null;
	private IPlayService playService = null;
	private RecordOnDemand recordOnDemand = null;
	private Timer timer = null;
	
	private Handler startViewHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
//			List<ReminderOnDemandEntity> ReminderOnDemandEntities = dbService.getAllStartReminders();
//			List<Map<String, String>> listMap =  getMapsForSimpleAdaptor(ReminderOnDemandEntities);
//			if (listMap.size() > 0){
//				startReminderBtn.setVisibility(View.VISIBLE);
//				  // setup data adapter
//				SimpleAdapter adapter = new SimpleAdapter(ReminderOnDemandActivity.this, listMap, android.R.layout.simple_list_item_2, 
//						new String[]{getResources().getString(R.id.subject), getResources().getString(R.id.desc)},
//						new int[]{R.id.subject,R.id.desc});
//		        // assign adapter to list view
//				reminderStartListView.setAdapter(adapter);
//			}
			 List<ReminderOnDemandEntity> startEntities = dbService.getAllStartReminders();
	         ReminderOnDemandListViewAdaptor startAdaptor = new ReminderOnDemandListViewAdaptor(ReminderOnDemandActivity.this, startEntities);
		     // assign adapter to list view
			 reminderStartListView.setAdapter(startAdaptor);
			 pagerAdaptor.notifyDataSetChanged();
        }
		
	};
	
	private Handler doneViewHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
			List<ReminderOnDemandEntity> ReminderOnDemandEntities = dbService.getAllDoneReminders();
			// setup data adapter
			ReminderOnDemandListViewAdaptor adaptor = new ReminderOnDemandListViewAdaptor(ReminderOnDemandActivity.this, ReminderOnDemandEntities);
	        // assign adapter to list view
			reminderDoneListView.setAdapter(adaptor);
			pagerAdaptor.notifyDataSetChanged();
        }
		
	};
	
	private TimerTask refreshStartViewTask = new TimerTask(){

		@Override
		public void run() {
			startViewHandler.sendEmptyMessage(0);	
		}
		
	};
	
	private TimerTask refreshDoneViewTask = new TimerTask(){

		@Override
		public void run() {
			doneViewHandler.sendEmptyMessage(0);	
		}
		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
       
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandActivity.this);
        recordOnDemand = new RecordOnDemand(this);
        
        initPagerViews();
        
        speakBtn = (ImageButton)findViewById(R.id.button1);
       // stopBtn = (Button)findViewById(R.id.stop);
        //startReminderBtn = (Button)findViewById(R.id.button_start);
        //startReminderBtn.setVisibility(View.INVISIBLE);
       // doneReminderBtn = (Button)findViewById(R.id.button2);
        //doneReminderBtn.setVisibility(View.INVISIBLE);
       
        speakBtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(getClass().getSimpleName(), "speakBtn Touch event");
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
				{
					Log.d(getClass().getSimpleName(), "speakBtn Touch event: ACTION_DOWN");
					recordOnDemand.doRecording();
					break;
				}
				case MotionEvent.ACTION_UP:
				{
					Log.d(getClass().getSimpleName(), "speakBtn Touch event: ACTION_UP");
					String recordLoc = recordOnDemand.stopRecording();
					Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandSettingsActivity.class);
					intent.putExtra("recordLoc", recordLoc);
					ReminderOnDemandActivity.this.startActivity(intent);
					break;
				}
				default:
				 	break;
				}
				return false;
			}
		});
        
//        stopBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				try {
//					bindService(new Intent("ReminderOnDemandService"), serviceConnection, Context.BIND_AUTO_CREATE);
//					if (playService != null){
//						playService.stop();
//					}
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//        
//       startReminderBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandViewActivity.class);
//				intent.putExtra("type", "start");
//				ReminderOnDemandActivity.this.startActivity(intent);
//			}
//		});
//        
//        doneReminderBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandViewActivity.class);
//				intent.putExtra("type", "done");
//				ReminderOnDemandActivity.this.startActivity(intent);
//			}
//		});
        
        
        	
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
    
    private void initPagerViews(){   
    	 titles = new ArrayList<String>();
    	 titles.add(getResources().getString(R.string.reminder_start_text));
         titles.add(getResources().getString(R.string.reminder_done_text));
         pagesArrayList = new ArrayList<View>();
    
         LayoutInflater layoutInflater=getLayoutInflater();
         viewPager = (ViewPager)findViewById(R.id.viewPager); 
         
         reminderStartListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         reminderDoneListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
       
		 pagesArrayList.add(reminderStartListView);	
		 pagesArrayList.add(reminderDoneListView);
		 
         pagerAdaptor = new ReminderOnDemandPagerAdaptor(pagesArrayList, titles);
         viewPager.setAdapter(pagerAdaptor);
         viewPager.setOnPageChangeListener(new ReminderOnDemaindPageChangeListener());
         viewPager.setCurrentItem(0);
         
         timer = new Timer();
         timer.schedule(refreshStartViewTask, 0, 1*60*1000);
         timer.schedule(refreshDoneViewTask, 1*60*1000, 1*60*1000);
         Log.d(getClass().getSimpleName(), "onCreate() invoked, timer.schedule invoked");
        
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
	
	private class ReminderOnDemaindPageChangeListener implements OnPageChangeListener{
        @Override
        public   void onPageScrollStateChanged (int state){
            // TODO Auto-generated method stub
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub
        }
        Color preColor;
        @Override
        public void onPageSelected(int position) {
            Log.d("page view",  "current position="+position);
            switch(position)
            {
            case 0: 
            	startViewHandler.sendEmptyMessage(0);
            	break;
            case 1:
            	doneViewHandler.sendEmptyMessage(0);
            	break;
            
            }
        }
    }
    
}
