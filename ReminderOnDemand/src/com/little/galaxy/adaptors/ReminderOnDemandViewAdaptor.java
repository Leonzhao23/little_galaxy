package com.little.galaxy.adaptors;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_VIEW;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.PlayOnDemand;
import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public abstract class ReminderOnDemandViewAdaptor extends BaseAdapter {
	
	protected final Context context;
	protected final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	protected ViewHolder holder = null;
	
	public ReminderOnDemandViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities){
		this.context = context;
		this.reminderOnDemandEntities = reminderOnDemandEntities;
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
		final ReminderOnDemandEntity entity = reminderOnDemandEntities.get(position);
		if (Log.isLoggable(TAG_VIEW, Log.DEBUG)){
			Log.d(TAG_VIEW, "View item position = " + position);
		}
		if (view == null){
	       view = initView();
	       holder = new ViewHolder();
		   holder.subject = (TextView)view.findViewById(R.id.subject);
		   holder.desc = (TextView)view.findViewById(R.id.desc);
		   holder.play = (ImageButton)view.findViewById(R.id.play);
		   holder.stop = (ImageButton)view.findViewById(R.id.stop);
		   view.setTag(holder);
	        	
	    } else{
	       	holder = (ViewHolder)view.getTag();
	    }
		
		holder.subject.setText(context.getResources().getString(R.string.reminder_view_subject) +": " + entity.getName());
        holder.desc.setText(context.getResources().getString(R.string.reminder_view_desc) + ": " + entity.getDesc());
        
        holder.play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayOnDemand play = new PlayOnDemand(context, entity.getRecoredLoc());
				play.play();
			}
        });
        
        holder.stop.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
				PlayOnDemand play = new PlayOnDemand(context, entity.getRecoredLoc());
				play.stop();
			}
        });
        
        addHolders(view, entity);
		
		return view;
	}
	
	protected abstract View initView();
	
	protected abstract void addHolders(View view, ReminderOnDemandEntity entity);
	
	protected static class ViewHolder {
		
		protected TextView subject;
		protected TextView desc;
		protected TextView startTime;
		protected TextView execTime;

		protected ImageButton play;
		protected ImageButton stop;
		protected ImageButton cancel;
		protected ImageButton del;	
		protected ImageButton edit;
		protected ImageButton start;
		protected ImageButton restart;
	}

}
