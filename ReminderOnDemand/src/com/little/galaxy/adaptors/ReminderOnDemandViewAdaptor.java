package com.little.galaxy.adaptors;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.entities.ReminderOnDemandEntity.ReminderState;
import com.little.galaxy.layouts.ReminderOnDemandCancelViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandDoneViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandNewViewLayout;
import com.little.galaxy.layouts.ReminderOnDemandStartViewLayout;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_VIEW;

public abstract class ReminderOnDemandViewAdaptor extends BaseAdapter {
	
	protected final Context context;
	protected final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	
	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities){
		this.context = context;
		this.reminderOnDemandEntities = reminderOnDemandEntities;
	}
	
//	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities, ReminderState state, ReminderOnDemandServiceConnection conn){
//		this.context = context;
//		this.reminderOnDemandEntities = reminderOnDemandEntities;
//		this.state = state;
//		this.conn = conn;
//	}

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

//	@Override
//	public View getView(int position, View view, ViewGroup viewGroup) {
//		ReminderOnDemandEntity entity = reminderOnDemandEntities.get(position);
//		if (Log.isLoggable(TAG_VIEW, Log.DEBUG)){
//			Log.d(TAG_VIEW, "View item position = " + position);
//		}
//		switch (state){
//		case New:
//			view = new ReminderOnDemandNewViewLayout().getView(context, viewGroup, entity);
//			break;
//		case Start:	
//			view = new ReminderOnDemandNewViewLayout().getView(context, viewGroup, entity);
//			//view = new ReminderOnDemandStartViewLayout(this.context, entity, conn);
//			break;
//		case Cancel:
//			//view = new ReminderOnDemandCancelViewLayout(this.context, entity);
//			view = new ReminderOnDemandNewViewLayout().getView(context, viewGroup, entity);
//			break;
//		case Done:
//			//view = new ReminderOnDemandDoneViewLayout(this.context, entity);
//			view = new ReminderOnDemandNewViewLayout().getView(context, viewGroup, entity);
//			break;
//		default:
//			break;
//		}
//		return view;
//	}
	
	 protected static class ViewHolder {
	    	protected TextView subject;
	        TextView desc;
	        ImageButton start;
	        ImageButton play;
	        ImageButton stop;
	        ImageButton del;
	 }

}
