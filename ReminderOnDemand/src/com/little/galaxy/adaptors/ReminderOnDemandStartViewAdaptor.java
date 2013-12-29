package com.little.galaxy.adaptors;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_DB;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_PLAY;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_SCHEDULE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.entities.ReminderOnDemandEntity.ReminderState;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;


public class ReminderOnDemandStartViewAdaptor extends
		ReminderOnDemandViewAdaptor {
	
	ReminderOnDemandServiceConnection conn = null;
	
	public ReminderOnDemandStartViewAdaptor(Context context,
			List<ReminderOnDemandEntity> reminderOnDemandEntities) {
		super(context, reminderOnDemandEntities);
	}
	
	public ReminderOnDemandStartViewAdaptor(Context context,
			List<ReminderOnDemandEntity> reminderOnDemandEntities,
			final ReminderOnDemandServiceConnection conn) {
		super(context, reminderOnDemandEntities);
		
		this.conn = conn;
		
	}

	@Override
	protected View initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.reminder_start_listview, null);
		return view;
	}

	@Override
	protected void addHolders(View view, final ReminderOnDemandEntity entity) {
		//nothing needs
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      	String startTime =  "The Reminder started at " + sdf.format(new Date(entity.getCreateTime()));
      	
      	int interval = entity.getInterval();
      	Log.d(TAG_SCHEDULE, "Interval: " + interval);
      	int subtract = (int)(System.currentTimeMillis() - entity.getCreateTime());
      	Log.d(TAG_SCHEDULE, "Subtract: " + subtract);
      	int slot = interval - subtract;
      	int minutes = slot/(60*1000);
      	minutes = minutes < 0 ? 0 : minutes;
      	Log.d(TAG_SCHEDULE, "reminging: " + minutes + " Minutes");
      	
      	holder.startTime = (TextView)view.findViewById(R.id.start_time);;
      	holder.startTime.setText(startTime);
      	
      	holder.execTime = (TextView)view.findViewById(R.id.exec_time);;
      	holder.execTime.setText(context.getResources().getString(R.string.reminder_exec_time) + " "+ minutes + " minutes");
    
        holder.cancel = (ImageButton)view.findViewById(R.id.cancel_time);
        holder.cancel.setImageDrawable((context.getResources().getDrawable(R.drawable.cancel)));
        holder.cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					IPlayService playService = conn.getPlayService();
					if (playService != null){
						playService.stop(String.valueOf(entity.getId()));
					}
				} catch (RemoteException e) {
					Log.e(TAG_PLAY, "Call remote service stop() failed!");
					e.printStackTrace();
				}
				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
				dbService.updateByState(entity, ReminderState.Cancel.getState());
				if (Log.isLoggable(TAG_DB, Log.DEBUG)){
					Log.d(TAG_DB, "The reminder[" + entity.getName() +"] was cancelled");
				}
			}
        });
	}
	
	

	
	
	

}
