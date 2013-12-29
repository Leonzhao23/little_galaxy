package com.little.galaxy.adaptors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandDoneViewAdaptor extends
		ReminderOnDemandViewAdaptor {

	public ReminderOnDemandDoneViewAdaptor(Context context,
			List<ReminderOnDemandEntity> reminderOnDemandEntities) {
		super(context, reminderOnDemandEntities);
	}

	@Override
	protected View initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.reminder_done_listview, null);
		return view;
	}

	@Override
	protected void addHolders(View view, final ReminderOnDemandEntity entity) {
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String execTime =  "Remindered at " + sdf.format(new Date(entity.getExecTime()));
		holder.execTime = (TextView)view.findViewById(R.id.exec_time);
      	holder.execTime.setText(execTime);
      	
        holder.del = (ImageButton)view.findViewById(R.id.del);
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
