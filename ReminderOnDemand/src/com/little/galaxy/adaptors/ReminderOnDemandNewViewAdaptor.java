package com.little.galaxy.adaptors;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_VIEW;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.little.galaxy.PlayOnDemand;
import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandNewViewAdaptor extends ReminderOnDemandViewAdaptor {
	private ViewHolder holder = null;
	
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
	public View getView(int position, View view, ViewGroup viewGroup) {
		final ReminderOnDemandEntity entity = reminderOnDemandEntities.get(position);
		if (Log.isLoggable(TAG_VIEW, Log.DEBUG)){
			Log.d(TAG_VIEW, "View item position = " + position);
		}
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		if (view == null){
	        view = layoutInflater.inflate(R.layout.reminder_new_listview, null);
	        holder = new ViewHolder();
	        holder.subject = (TextView)view.findViewById(R.id.subject);
	        holder.desc = (TextView)view.findViewById(R.id.desc);
	        holder.start = (ImageButton)view.findViewById(R.id.start);
	        holder.del = (ImageButton)view.findViewById(R.id.del);
	        holder.play = (ImageButton)view.findViewById(R.id.play);
	        holder.stop = (ImageButton)view.findViewById(R.id.stop);
	        view.setTag(holder);
	        	
	    } else{
	       	holder = (ViewHolder)view.getTag();
	    }
		
		holder.subject.setText(entity.getName());
        holder.desc.setText(entity.getName());
       
        holder.start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ReminderOnDemandService.class);
				Log.d(TAG_ACTIVITY, "Try to start service!");
				context.startService(intent);
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_CLICK));
				
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
		return view;
		
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
