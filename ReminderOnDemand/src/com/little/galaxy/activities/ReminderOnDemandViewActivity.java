package com.little.galaxy.activities;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.little.galaxy.R;
import com.little.galaxy.adaptors.ReminderOnDemandViewAdaptor;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;


public class ReminderOnDemandViewActivity extends ListActivity {
	private IDBService dbService = null;
	private ProgressDialog progressDialog = null;
	private ReminderOnDemandViewAdaptor reminderOnDemandView = null;
	private List<ReminderOnDemandEntity> reminderOnDemandEntities = null;
	
	private String type;
	
	private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            Log.d(this.getClass().getSimpleName(), " worker thread done, setup ReviewAdapter");
            progressDialog.dismiss();
            // reminderOnDemandView = new ReminderOnDemandViewAdaptor(ReminderOnDemandViewActivity.this, reminderOnDemandEntities, ReminderOnDemandEntity.ReminderState.Done);
            setListAdapter(reminderOnDemandView);
            
        }
    };  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_reminder_on_demand_view);
		 // set list properties
        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        type = getIntent().getStringExtra("type");
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandViewActivity.this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
        this.progressDialog = ProgressDialog.show(this, " Working...", " Retrieving reviews", true, false);
        new Thread() {
            @Override
            public void run() {
            	if (type.equals("start")){
            		reminderOnDemandEntities = dbService.getAllStartedReminders();
            	} 
                handler.sendEmptyMessage(0);
            }
        }.start();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	 menu.add(0, Menu.FIRST, 0, "item 1");
    	 menu.add(0, Menu.FIRST + 1, 0, "item 2");
       // getMenuInflater().inflate(R.menu.reminder_on_demand, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST:
			 Log.d(this.getClass().getSimpleName(), " First item selected!");
			 break;
		case Menu.FIRST+1:
			 Log.d(this.getClass().getSimpleName(), " Second item selected");
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		 
	}

	@Override
	protected void onDestroy() {
		if (dbService != null){
			dbService.cleanup();
		}
		super.onDestroy();
	}
	
	

}
