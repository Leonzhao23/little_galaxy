package com.little.galaxy.activities;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceProvider;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandSettingsActivity extends Activity {
	private Spinner spinnerInterval = null;
	private Spinner spinnerFrequency = null;
	private Spinner spinnerAutoStart = null;
	private EditText subjText = null;
	private EditText descText = null;
	private IDBService dbService  = null;
	private ReminderOnDemandEntity reminderOnDemandEntity = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        subjText = (EditText) findViewById(R.id.edit_subj); 
        descText = (EditText) findViewById(R.id.edit_desc); 
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval); 
        spinnerFrequency = (Spinner) findViewById(R.id.spinner_frequency); 
        spinnerAutoStart = (Spinner) findViewById(R.id.spinner_auto_start); 
        
        dbService = DBServiceProvider.getDBService(DBType.SQLite, ReminderOnDemandSettingsActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReminderOnDemandSettingsActivity.this);
        long id = this.getIntent().getLongExtra("newReminderId", System.currentTimeMillis());
        
        reminderOnDemandEntity = dbService.getReminderById(id);
		if (reminderOnDemandEntity != null){
			subjText.setText(reminderOnDemandEntity.getName());
			descText.setText(reminderOnDemandEntity.getDesc());
		}
		
     
		final ArrayAdapter<?> adapterInterval = ArrayAdapter.createFromResource(this, R.array.pref_inverval_title, android.R.layout.simple_spinner_item);  
		adapterInterval.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinnerInterval.setAdapter(adapterInterval);     
        
        spinnerInterval.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String str = (String)adapterInterval.getItem(position);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });   
        spinnerInterval.setVisibility(View.VISIBLE);  
        //
        final ArrayAdapter<?> adapterFrequency = ArrayAdapter.createFromResource(this, R.array.pref_frequency_title, android.R.layout.simple_spinner_item);  
		adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinnerFrequency.setAdapter(adapterFrequency);     
        
        spinnerFrequency.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        }); 
        
        //
        final ArrayAdapter<?> adapterAutoStart = ArrayAdapter.createFromResource(this, R.array.pref_start_title, android.R.layout.simple_spinner_item);  
		adapterAutoStart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinnerAutoStart.setAdapter(adapterAutoStart);     
       
	}
	
	@Override
    public void onBackPressed() {
		String subject = subjText.getText().toString();
		String desc = descText.getText().toString();
		ReminderOnDemandEntity entity = new ReminderOnDemandEntity(
														reminderOnDemandEntity.getId(),
														subject,
														desc,
														reminderOnDemandEntity.getRecoredLoc(),
														reminderOnDemandEntity.getCreateTime(),
														reminderOnDemandEntity.getExecTime(),
														reminderOnDemandEntity.getInterval(),
														reminderOnDemandEntity.getFrenquecy(),
														reminderOnDemandEntity.getAutoStartTime(),
														reminderOnDemandEntity.getState());
		dbService.update(entity);
		if (Log.isLoggable(TAG_ACTIVITY, Log.DEBUG)){
			Log.d(TAG_ACTIVITY, "The reminder[" + reminderOnDemandEntity.getName() +"] updated");
		}
		
		this.setResult(RESULT_OK, new Intent(this, ReminderOnDemandActivity.class));
        super.onBackPressed();
  }


}
