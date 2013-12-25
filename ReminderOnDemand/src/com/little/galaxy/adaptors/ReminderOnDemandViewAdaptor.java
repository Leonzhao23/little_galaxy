package com.little.galaxy.adaptors;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.entities.ReminderOnDemandEntity.ReminderState;
import com.little.galaxy.layouts.ReminderOnDemandCancelViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandDoneViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandStartViewLayout;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;
import com.little.galaxy.services.IPlayService;

public class ReminderOnDemandViewAdaptor extends BaseAdapter {
	
	private final Context context;
	private final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	private final ReminderState state;
	private ReminderOnDemandServiceConnection conn;
	
	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities, ReminderState state){
		this.context = context;
		this.reminderOnDemandEntities = reminderOnDemandEntities;
		this.state = state;
	}
	
	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities, ReminderState state, ReminderOnDemandServiceConnection conn){
		this.context = context;
		this.reminderOnDemandEntities = reminderOnDemandEntities;
		this.state = state;
		this.conn = conn;
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
			viewGroup = new ReminderOnDemandStartViewLayout(this.context, entity, conn);
			break;
		case Cancel:
			viewGroup = new ReminderOnDemandCancelViewLayout(this.context, entity);
			break;
		case Done:
			viewGroup = new ReminderOnDemandDoneViewLayout(this.context, entity);
			break;
		default:
			break;
		}
		return viewGroup;
	}

}
