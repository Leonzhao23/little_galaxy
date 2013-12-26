package com.little.galaxy.activities;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_HANDLER;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_PAGE;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_SPEECH;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.little.galaxy.R;
import com.little.galaxy.RecordOnDemand;
import com.little.galaxy.adaptors.ReminderOnDemandPagerAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandViewAdaptor;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandActivity extends Activity implements OnItemClickListener {
	
	private final static int RETURN_CODE_FROM_SETTINGS = 0;

    private ImageButton speakBtn = null;
    private ViewPager viewPager = null;
    private ArrayList<String> titles = null;
    private ArrayList<View> pagesArrayList;
    private ReminderOnDemandPagerAdaptor pagerAdaptor = null;
    private ListView reminderNewListView = null;
    private ListView reminderStartListView = null;
    private ListView reminderDoneListView = null;
    private ListView reminderCancelListView = null;
	private IDBService dbService = null;
	private RecordOnDemand recordOnDemand = null;
	private ScheduledExecutorService ses = null;
	private ReminderOnDemandServiceConnection conn = null;
	private boolean bind = false;
	private boolean canSpeechRecognized = false; 
	
	
	private Handler viewHandler = new Handler(){
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
			List<ReminderOnDemandEntity> entities = null;
			ReminderOnDemandViewAdaptor adaptor = null;
			switch(msg.what){
			
			case 0:
				entities = dbService.getAllNewReminders();
				adaptor = new ReminderOnDemandViewAdaptor(
						ReminderOnDemandActivity.this, 
						entities, 
						ReminderOnDemandEntity.ReminderState.New);
				reminderNewListView.setAdapter(adaptor);
				Log.d(TAG_HANDLER, "Refresh New View");
			case 1:
				entities = dbService.getAllStartReminders();
				adaptor = new ReminderOnDemandViewAdaptor(
						ReminderOnDemandActivity.this, 
						entities, 
						ReminderOnDemandEntity.ReminderState.Start, conn);
				reminderStartListView.setAdapter(adaptor);
				Log.d(TAG_HANDLER, "Refresh Start View");
				break;
			case 2:
				 entities = dbService.getAllCancelledReminders();
				 adaptor = new ReminderOnDemandViewAdaptor(
						 ReminderOnDemandActivity.this, 
						 entities, 
						 ReminderOnDemandEntity.ReminderState.Cancel);
			     // assign adapter to list view
				 reminderCancelListView.setAdapter(adaptor);
				 Log.d(TAG_HANDLER, "Refresh Cancel View");
				 break;
			case 3:
				entities = dbService.getAllDoneReminders();
				adaptor = new ReminderOnDemandViewAdaptor(
						ReminderOnDemandActivity.this, 
						entities, 
						ReminderOnDemandEntity.ReminderState.Done);
				reminderDoneListView.setAdapter(adaptor);
				Log.d(TAG_HANDLER, "Refresh Done View");
				
			}
			pagerAdaptor.notifyDataSetChanged();
			
        }
		
	};
	
	private Handler speechHandler = new Handler(){
		@Override
        public void handleMessage(final Message msg) {
			
		}
	};
	
	private Runnable refreshNewViewTask = new Runnable(){

		@Override
		public void run() {
			viewHandler.sendEmptyMessage(0);	
		}
		
	};
	
	private Runnable refreshStartViewTask = new Runnable(){

		@Override
		public void run() {
			viewHandler.sendEmptyMessage(1);	
		}
		
	};
	
	private Runnable refreshCancelViewTask = new Runnable(){

		@Override
		public void run() {
			viewHandler.sendEmptyMessage(2);	
		}
		
	};
	
	private Runnable refreshDoneViewTask = new Runnable(){

		@Override
		public void run() {
			viewHandler.sendEmptyMessage(3);	
		}
		
	};
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
       
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandActivity.this);
        recordOnDemand = new RecordOnDemand(this);
        
        initPagerViews();
        
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() < 1) {
            if (Log.isLoggable(TAG_SPEECH, Log.WARN)){
            	Log.w(TAG_SPEECH, "CAN NOT RECOGNIZE SPEECH, PLEASE INSTALL GOOGLE VOICE SEACH!");
            }     
        } else{
        	canSpeechRecognized = true;
        	if (Log.isLoggable(TAG_SPEECH, Log.DEBUG)){
        		Log.d(TAG_SPEECH, "RECOGNIZE SPEECH");
            } 
        	
        }
        
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
//					new Thread(){
//						@Override
//						public void run(){
//							if (canSpeechRecognized){
//								startVoiceRecognitionActivity();
//							}
//						}
//					}.start();
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
        	
    }
    
    
    

	@Override
	protected void onStart() {
		//bind remote service
        if (!bind){
        	conn = new ReminderOnDemandServiceConnection();
        	bindService(new Intent("com.little.galaxy.services.IPlayService"), conn, Context.BIND_AUTO_CREATE);
        	bind = true;
        }
		super.onStart();
	}




	@Override
	protected void onDestroy() {
    	Log.d(getClass().getSimpleName(), "onDestroy() invoked, timer.cancel() invoked");
    	if (dbService != null){
    		dbService.cleanup();
    	} 	
    	if (bind){
    		unbindService(conn);	
    		bind = false;
    	}
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            for(String str: matches){
            	Log.d(TAG_SPEECH, str);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, 1);
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
    	 titles.add(getResources().getString(R.string.reminder_new_text));
    	 titles.add(getResources().getString(R.string.reminder_start_text));
         titles.add(getResources().getString(R.string.reminder_cancel_text));
         titles.add(getResources().getString(R.string.reminder_done_text));
         pagesArrayList = new ArrayList<View>();
    
         LayoutInflater layoutInflater=getLayoutInflater();
         viewPager = (ViewPager)findViewById(R.id.viewPager); 
         
         reminderNewListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         reminderStartListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         reminderDoneListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         reminderCancelListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
       
         pagesArrayList.add(reminderNewListView);
         pagesArrayList.add(reminderStartListView);
		 pagesArrayList.add(reminderCancelListView);	
		 pagesArrayList.add(reminderDoneListView);
		 
         pagerAdaptor = new ReminderOnDemandPagerAdaptor(pagesArrayList, titles);
         viewPager.setAdapter(pagerAdaptor);
         viewPager.setOnPageChangeListener(new ReminderOnDemaindPageChangeListener());
         viewPager.setCurrentItem(0);
         
         ses = Executors.newScheduledThreadPool(1);
         ses.scheduleWithFixedDelay(refreshNewViewTask, 0, 1, TimeUnit.SECONDS);
         Log.d(getClass().getSimpleName(), "onCreate() invoked, timer.schedule invoked");
        
    }
	
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
            Log.d(TAG_PAGE,  "Current Page Position =" + position);
            switch(position)
            {
            case 0:  	
            	viewHandler.sendEmptyMessage(0);
            	break;
            case 1:
            	viewHandler.sendEmptyMessage(1);
            	break;
            case 2:
            	viewHandler.sendEmptyMessage(2);
            	break;
            case 3:
            	viewHandler.sendEmptyMessage(3);
            }
        }
    }
    
}
