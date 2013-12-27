package com.little.galaxy.layouts;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_ACTIVITY;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.PlayOnDemand;
import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandNewViewLayout extends LinearLayout{
	private TextView subject;
    private TextView desc;
    private ImageButton start;
    private ImageButton play;
    private ImageButton stop;
    private ImageButton del;

    public ReminderOnDemandNewViewLayout(final Context context, final ReminderOnDemandEntity entity) {
    	
        super(context);
        start = new ImageButton(context);
        start.setBackgroundColor(Color.WHITE);
        start.setImageDrawable((getResources().getDrawable(R.drawable.start)));
        start.setPadding(0, 0, 0, 0);
        start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ReminderOnDemandService.class);
				Log.d(TAG_ACTIVITY, "Try to start service!");
				context.startService(intent);
			}
        });
        
        del = new ImageButton(context);
        del.setImageDrawable((getResources().getDrawable(R.drawable.delete)));
        del.setBackgroundColor(Color.WHITE);
        del.setPadding(0, 0, 0, 0);
        del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
				dbService.delete(entity.getId());
				dbService.cleanup();
			}
        });
        
        
        play = new ImageButton(context);
        play.setImageDrawable((getResources().getDrawable(R.drawable.play_little)));
        play.setBackgroundColor(Color.WHITE);
        play.setPadding(0, 0, 0, 0);
        play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayOnDemand play = new PlayOnDemand(context, entity.getRecoredLoc());
				play.play();
			}
        });
        
        stop = new ImageButton(context);
        stop.setImageDrawable((getResources().getDrawable(R.drawable.stop_little)));
        stop.setBackgroundColor(Color.WHITE);
        stop.setPadding(0, 0, 0, 0);
        stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayOnDemand play = new PlayOnDemand(context, entity.getRecoredLoc());
				play.stop();
			}
        });
        
        
        setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        
        LinearLayout childLeftLayout = new LinearLayout(context);
        childLeftLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams childLeftParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childLeftParams.gravity = Gravity.LEFT;

        this.subject = new TextView(context);
        this.subject.setText(entity.getName());
        this.subject.setTextSize(18f);
        this.subject.setTextColor(Color.BLACK);
        childLeftLayout.addView(this.subject, childLeftParams);

        this.desc = new TextView(context);
        this.desc.setText("This is a test ddddddddd");
        this.desc.setTextSize(10f);
        this.desc.setTextColor(Color.GRAY);
        childLeftLayout.addView(this.desc, childLeftParams);
        
        LinearLayout childChildLayout = new LinearLayout(context);
        childChildLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams childChildParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        
        childChildLayout.addView(this.play, childChildParams);
        childChildLayout.addView(this.stop, childChildParams);
        childLeftLayout.addView(childChildLayout, childLeftParams);
       
        
        LinearLayout childRightLayout = new LinearLayout(context);
        childRightLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams childRightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        childRightParams.gravity = Gravity.RIGHT|Gravity.BOTTOM;   
        childRightParams.setMargins(0, 0, 0, 0);
        int autoStartTime = entity.getAutoStartTime();
        if (autoStartTime != -1){
        
            IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
			dbService.updateStartTime(entity);
			dbService.cleanup();
		   
			displayAutoStartTime(autoStartTime);
            if (autoStartTime < 1){
           	Intent intent = new Intent(context, ReminderOnDemandService.class);
				Log.d(TAG_ACTIVITY, "Try to start service!");
				context.startService(intent);
            }
        }
        childRightLayout.addView(this.start, childRightParams);
        childRightLayout.addView(this.del, childRightParams);
        
        this.addView(childLeftLayout, parentParams);
        this.addView(childRightLayout, parentParams);
       
    }
    
    private void displayAutoStartTime(final int autoStartTime){
    	
    	switch(autoStartTime){
    	case 5:
    		start.setImageDrawable((getResources().getDrawable(R.drawable.start5)));
    		break;
    	case 4:
    		start.setImageDrawable((getResources().getDrawable(R.drawable.start4)));
    		break;
    	case 3:
    		start.setImageDrawable((getResources().getDrawable(R.drawable.start3)));
    		break;
    	case 2:
    		start.setImageDrawable((getResources().getDrawable(R.drawable.start2)));
    		break;
    	case 1:
    		start.setImageDrawable((getResources().getDrawable(R.drawable.start1)));
    		break;
    	}
    }

}