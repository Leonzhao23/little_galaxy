package com.little.galaxy.adaptors;

import static com.little.galaxy.utils.ReminderOnDemandConsts.RETURN_CODE_FROM_SETTINGS;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.little.galaxy.R;
import com.little.galaxy.activities.ReminderOnDemandActivity;
import com.little.galaxy.activities.ReminderOnDemandSettingsActivity;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandNewViewAdaptor extends ReminderOnDemandViewAdaptor {
	
	public final static float[] BT_CLICK = new float[]{
		2,0,0,0,2,
		0,2,0,0,2,
		0,0,2,0,2,
		0,0,0,1,0
	};

	public ReminderOnDemandNewViewAdaptor(Context context,
			List<ReminderOnDemandEntity> reminderOnDemandEntities) {
		super(context, reminderOnDemandEntities);
	}

	@Override
	protected View initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.reminder_new_listview, null);
		return view;
	}

	@Override
	protected void addHolders(View view, final ReminderOnDemandEntity entity) {
		holder.start = (ImageButton)view.findViewById(R.id.start);
		holder.edit = (ImageButton)view.findViewById(R.id.edit);
	    holder.del = (ImageButton)view.findViewById(R.id.del);
	    
	    holder.start.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ReminderOnDemandService.class);
				intent.putExtra("newReminderId", entity.getId());
				Log.d(TAG_ACTIVITY, "Try to start service!");
				context.startService(intent);
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_CLICK));
				v.setBackground(v.getBackground());					
				new Thread(((ReminderOnDemandActivity)context).getRefreshNewViewTask()).start();
			}
	      });
	    holder.edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ReminderOnDemandSettingsActivity.class);
				intent.putExtra("newReminderId", entity.getId());
				Log.d(TAG_ACTIVITY, "Start settings activity!");
				((Activity)context).startActivityForResult(intent, RETURN_CODE_FROM_SETTINGS);
			}
	      });
	    holder.del.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View v) {
				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
				dbService.delete(entity.getId());
				dbService.cleanup();
				new Thread(((ReminderOnDemandActivity)context).getRefreshNewViewTask()).start();
			}	
	    });
		  
	    int autoStartTime = entity.getAutoStartTime();
	      if (autoStartTime != -1){
	            IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
	  			dbService.updateStartTime(entity);
	  			dbService.cleanup();
	  		   
	  			displayAutoStartTime(context, autoStartTime);
	            if (autoStartTime < 1){
	           	    Intent intent = new Intent(context, ReminderOnDemandService.class);
	  				Log.d(TAG_ACTIVITY, "Try to start service!");
	  				context.startService(intent);
	            }
	    } 
		
	}
	
	private void displayAutoStartTime(final Context ctx, final int autoStartTime){
    	
    	switch(autoStartTime){
    	case 5:
    		holder.start.setImageDrawable((ctx.getResources().getDrawable(R.drawable.start5)));
    		break;
    	case 4:
    		holder.start.setImageDrawable((ctx.getResources().getDrawable(R.drawable.start4)));
    		break;
    	case 3:
    		holder.start.setImageDrawable((ctx.getResources().getDrawable(R.drawable.start3)));
    		break;
    	case 2:
    		holder.start.setImageDrawable((ctx.getResources().getDrawable(R.drawable.start2)));
    		break;
    	case 1:
    		holder.start.setImageDrawable((ctx.getResources().getDrawable(R.drawable.start1)));
    		break;
    	}
    }

}
