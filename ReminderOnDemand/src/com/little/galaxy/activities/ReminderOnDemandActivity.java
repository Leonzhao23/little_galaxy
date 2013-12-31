package com.little.galaxy.activities;

import static com.little.galaxy.utils.ReminderOnDemandConsts.RETURN_CODE_FROM_SETTINGS;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_HANDLER;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_PAGE;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_SPEECH;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_THREAD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.little.galaxy.R;
import com.little.galaxy.RecordOnDemand;
import com.little.galaxy.adaptors.ReminderOnDemandCancelViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandDoneViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandNewViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandPagerAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandStartViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandViewAdaptor;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandActivity extends Activity {
	
    private ImageButton speakBtn = null;
    private ViewPager viewPager = null;
    private ArrayList<String> titles = null;
    private ArrayList<View> pagesArrayList;
    private ReminderOnDemandPagerAdaptor pagerAdaptor = null;
    private ListView reminderNewListView = null;
    private ListView reminderStartListView = null;
    private ListView reminderCancelListView = null;
    private ListView reminderDoneListView = null;
    private List<ReminderOnDemandEntity> doneEntities;
    
    //footer view
    private View footerView = null;;
    private boolean enablePullLoad;
    private boolean pullLoading;
    private boolean isFooterReady = false;
    private int startIndex  = 0;
    private int delta = 2;
    private float lastY = -1;
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static float OFFSET_RADIO = 1.8f; 
    
	private IDBService dbService = null;
	private RecordOnDemand recordOnDemand = null;
	private ScheduledExecutorService ses = null;
	private ReminderOnDemandServiceConnection conn = null;
	private boolean bind = false;
	private boolean canSpeechRecognized = false; 
	
	
	public Runnable getRefreshNewViewTask(){
		return refreshNewViewTask;
	}
	
	
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
			
			switch(msg.what){
			
			case 0:
				ReminderOnDemandViewAdaptor adaptor = (ReminderOnDemandViewAdaptor)msg.obj;
				reminderNewListView.setAdapter(adaptor);
				if (Log.isLoggable(TAG_HANDLER, Log.DEBUG)){
					Log.d(TAG_HANDLER, "Refresh new view");
				}
				break;
			case 1:
				reminderStartListView.setAdapter((ReminderOnDemandViewAdaptor)msg.obj);
				if (Log.isLoggable(TAG_HANDLER, Log.DEBUG)){
					Log.d(TAG_HANDLER, "Refresh start view");
				}
				break;
			case 2:
				reminderCancelListView.setAdapter((ReminderOnDemandViewAdaptor)msg.obj);
				if (Log.isLoggable(TAG_HANDLER, Log.DEBUG)){
					Log.d(TAG_HANDLER, "Refresh cancel view");
				}
				 break;
			case 3:
				reminderDoneListView.setAdapter((ReminderOnDemandViewAdaptor)msg.obj);
				if (Log.isLoggable(TAG_HANDLER, Log.DEBUG)){
					Log.d(TAG_HANDLER, "Refresh done view");
				}
			
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
			List<ReminderOnDemandEntity> entities = dbService.getAllNewReminders();
			ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandNewViewAdaptor(
													reminderNewListView.getContext(), 
													entities);	
			if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
				Log.d(TAG_THREAD, "Thread refreshNewVIewTask");
			}
			Message msg= Message.obtain(viewHandler, 0, adaptor);
			viewHandler.sendMessage(msg);	
		}
		
	};
	
	private Runnable refreshStartViewTask = new Runnable(){

		@Override
		public void run() {
			List<ReminderOnDemandEntity> entities = dbService.getAllStartedReminders();
			ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandStartViewAdaptor(
													reminderStartListView.getContext(), 
													entities,
													conn);	
			if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
				Log.d(TAG_THREAD, "Thread refreshStartViewTask");
			}
			Message msg= Message.obtain(viewHandler, 1, adaptor);
			viewHandler.sendMessage(msg);	
		}
		
	};
	
	private Runnable refreshCancelViewTask = new Runnable(){

		@Override
		public void run() {
			List<ReminderOnDemandEntity> entities = dbService.getAllCancelledReminders();
			ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandCancelViewAdaptor(
													reminderCancelListView.getContext(), 
													entities);	
			if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
				Log.d(TAG_THREAD, "Thread refreshCancelViewTask");
			}
			Message msg= Message.obtain(viewHandler, 2, adaptor);
			viewHandler.sendMessage(msg);		
		}
		
	};
	
	private Runnable refreshDoneViewTask = new Runnable(){

		@Override
		public void run() {
			if (doneEntities == null){
				doneEntities = dbService.getFilterReminders(new String[]{"2", String.valueOf(startIndex), String.valueOf(delta)});
			} else{
				doneEntities.addAll(dbService.getFilterReminders(new String[]{"2", String.valueOf(startIndex), String.valueOf(delta)}));
			}
			
			ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
													reminderDoneListView.getContext(), 
													doneEntities);	
			if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
				Log.d(TAG_THREAD, "Thread refreshDoneViewTask");
			}
			Message msg= Message.obtain(viewHandler, 3, adaptor);
			viewHandler.sendMessage(msg);	
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
					long id = System.currentTimeMillis();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReminderOnDemandActivity.this);
					SimpleDateFormat sdf=new SimpleDateFormat("MM/dd HH:mm:ss");
					String name = sdf.format(new Date(id));
					String desc = prefs.getString("desc", "This is your reminder");
					String getStr = prefs.getString("interval", "15");
					String getAutoStartTime = prefs.getString("start", "Never");
					int interval = 15;
					int autoStartTime = -1;
					try{
						autoStartTime = Integer.parseInt(getAutoStartTime);
						interval = Integer.parseInt(getStr);
					}catch(NumberFormatException nfe){
						Log.w(this.getClass().getSimpleName(), "parse string to int error, use default value 15mins");
					}
					ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, id, interval*60*1000, 1/*frequency=1*/, autoStartTime, 0/*state new*/);
					dbService.insert(entity);
					if (Log.isLoggable(TAG_ACTIVITY, Log.DEBUG)){
						Log.d(TAG_ACTIVITY, "New reminder[" + name +"] created");
					}	
					
					new Thread(refreshNewViewTask).start();
					
//					Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandSettingsActivity.class);
//					intent.putExtra("recordLoc", recordLoc);
//					ReminderOnDemandActivity.this.startActivityForResult(intent, RETURN_CODE_FROM_SETTINGS);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0, Menu.FIRST, 0, "Preference");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST:
			 Log.d(this.getClass().getSimpleName(), " First item selected!");
			 Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandPrefrenceActivity.class);
			 ReminderOnDemandActivity.this.startActivity(intent);
			 break;
		}
		return super.onOptionsItemSelected(item);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
//            // Fill the list view with the strings the recognizer thought it could have heard
//            ArrayList<String> matches = data.getStringArrayListExtra(
//                    RecognizerIntent.EXTRA_RESULTS);
//            for(String str: matches){
//            	Log.d(TAG_SPEECH, str);
//            }
        }
        
        if (requestCode == RETURN_CODE_FROM_SETTINGS && resultCode == RESULT_OK){
        	new Thread(refreshNewViewTask).start();
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
         
         reminderCancelListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         reminderDoneListView = (ListView) 
         		 (layoutInflater.inflate(R.layout.activity_reminder_on_demand_view, null).findViewById(R.id.listView));
         
         footerView = layoutInflater.inflate(R.layout.listview_footer, null);
         reminderDoneListView.addFooterView(footerView);
         footerView.setVisibility(View.INVISIBLE);
         reminderDoneListView.setOnScrollListener(new ReminderOnDemandOnScrollAndTouch());
         
//         reminderNewListView.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Object obj = reminderNewListView.getItemAtPosition(position);
//				Intent intent = new Intent(ReminderOnDemandActivity.this, ReminderOnDemandSettingsActivity.class);
//				ReminderOnDemandActivity.this.startActivityForResult(intent, RETURN_CODE_FROM_SETTINGS);
//			}
//        	 
//         });
         pagesArrayList.add(reminderNewListView);
         pagesArrayList.add(reminderStartListView);
		 pagesArrayList.add(reminderCancelListView);	
		 pagesArrayList.add(reminderDoneListView);
		 
         pagerAdaptor = new ReminderOnDemandPagerAdaptor(pagesArrayList, titles);
         viewPager.setAdapter(pagerAdaptor);
         viewPager.setOnPageChangeListener(new ReminderOnDemandPageChangeListener());
         viewPager.setCurrentItem(0);
         
         ses = Executors.newScheduledThreadPool(2);
         ses.scheduleWithFixedDelay(refreshNewViewTask, 0, 1*60, TimeUnit.SECONDS);
         ses.scheduleWithFixedDelay(refreshStartViewTask, 1*60, 1*60, TimeUnit.SECONDS);
         Log.d(getClass().getSimpleName(), "onCreate() invoked, timer.schedule invoked");
        
    }
	
	private class ReminderOnDemandPageChangeListener implements OnPageChangeListener{
        @Override
        public   void onPageScrollStateChanged (int state){
        	
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG_PAGE,  "Current Page Position =" + position);
            switch(position)
            {
            case 0:  	
            	new Thread(refreshNewViewTask).start();
            	break;
            case 1:
            	new Thread(refreshStartViewTask).start();
            	break;
            case 2:
            	new Thread(refreshCancelViewTask).start();
            	break;
            case 3:
            	if (doneEntities == null){
            		new Thread(refreshDoneViewTask).start();
            	}
            }
        }
    }
	
	private class ReminderOnDemandOnScrollAndTouch  implements OnScrollListener, OnTouchListener {

		@Override
		public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			switch (scrollState){
				case OnScrollListener.SCROLL_STATE_IDLE:
					// for footer
					if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
						if (footerView != null){
							footerView.setVisibility(View.VISIBLE);
						}
						if (listview.getCount() < startIndex + delta){
							
						} else{
							startIndex = startIndex + delta;
							new Thread(refreshDoneViewTask).start();
						}
						
					}
					// for header
					if(listview.getFirstVisiblePosition() == 0){
						
			        }

			     break;
			}
			
		 		
			
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				 lastY = event.getRawY();
			case MotionEvent.ACTION_MOVE:
				 final float deltaY = event.getRawY() - lastY;
                 lastY = event.getRawY();
                 if (reminderDoneListView.getLastVisiblePosition() == reminderDoneListView.getCount() - 1
                     || deltaY < 0) {
                
                 }
			default:
					
			
			}
			return false;
			
		}

		
		
	}
    
}
