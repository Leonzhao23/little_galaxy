package com.little.galaxy.activities;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;
import com.little.galaxy.views.ReminderOnDemandView;


public class ReminderOnDemandViewActivity extends ListActivity {
	private IDBService dbService = null;
	private ProgressDialog progressDialog = null;
	private ReminderOnDemandView reminderOnDemandView = null;
	private List<ReminderOnDemandEntity> reminderOnDemandEntities = null;
	
	private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            Log.v(this.getClass().getSimpleName(), " worker thread done, setup ReviewAdapter");
            progressDialog.dismiss();
            reminderOnDemandView = new ReminderOnDemandView(ReminderOnDemandViewActivity.this, reminderOnDemandEntities);
            setListAdapter(reminderOnDemandView);
            
        }
    };  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder_on_demand_view);
		 // set list properties
        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dbService = DBServiceFactory.getDBService(DBType.SQLite, ReminderOnDemandViewActivity.this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
        this.progressDialog = ProgressDialog.show(this, " Working...", " Retrieving reviews", true, false);
        new Thread() {
            @Override
            public void run() {
            	reminderOnDemandEntities = dbService.getAllDoneReminders();
                handler.sendEmptyMessage(0);
            }
        }.start();
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
//		if (dbService != null){
//			dbService.cleanup();
//		}
		super.onDestroy();
	}
	
	

}
