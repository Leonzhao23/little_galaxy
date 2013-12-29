package com.little.galaxy.adaptors;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandCancelViewAdaptor extends
		ReminderOnDemandViewAdaptor {

	public ReminderOnDemandCancelViewAdaptor(Context context,
			List<ReminderOnDemandEntity> reminderOnDemandEntities) {
		super(context, reminderOnDemandEntities);
	}

	@Override
	protected View initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.reminder_cancel_listview, null);
		return view;
	}

	@Override
	protected void addHolders(View view, final ReminderOnDemandEntity entity) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String cancelTime =  "The Reminder was cancelled at " + sdf.format(new Date(entity.getExecTime()));
		holder.execTime = (TextView)view.findViewById(R.id.cancel_time);
      	holder.execTime.setText(cancelTime);
      	holder.restart = (ImageButton)view.findViewById(R.id.restart);
	    holder.del = (ImageButton)view.findViewById(R.id.del);
	    
	    holder.restart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG_ACTIVITY, "restart the reminder[" + entity.getName() + "]");
				Intent intent = new Intent(context, ReminderOnDemandService.class);
				Log.d(TAG_ACTIVITY, "Try to start service!");
				context.startService(intent);	
			}
	      });
	    holder.del.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View v) {
				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
				dbService.delete(entity.getId());
				dbService.cleanup();
			}	
	    });
	}

}
