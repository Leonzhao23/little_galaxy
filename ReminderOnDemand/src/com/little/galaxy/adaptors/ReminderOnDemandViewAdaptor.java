package com.little.galaxy.adaptors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.entities.ReminderOnDemandEntity.ReminderState;
import com.little.galaxy.layouts.ReminderOnDemandDoneViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandStartViewLayout;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandViewAdaptor extends BaseAdapter {
	
	private final Context context;
	private final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	private final ReminderState state;
	
	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities, ReminderState state){
		this.context = context;
		this.reminderOnDemandEntities = reminderOnDemandEntities;
		this.state = state;
	}

	@Override
	public int getCount() {
		return reminderOnDemandEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return reminderOnDemandEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ReminderOnDemandEntity entity = reminderOnDemandEntities.get(position);
		
		switch (state){
		case Start:	
			viewGroup = new ReminderOnDemandStartViewLayout(this.context, entity);
			break;
		case New:
		case Done:
			viewGroup = new ReminderOnDemandDoneViewLayout(this.context, entity);
			break;
		}
		return viewGroup;
	}

}
