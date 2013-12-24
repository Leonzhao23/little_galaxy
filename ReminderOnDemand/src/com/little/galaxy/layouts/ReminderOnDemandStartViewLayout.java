package com.little.galaxy.layouts;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.entities.ReminderOnDemandEntity.ReminderState;
import com.little.galaxy.local.services.ReminderOnDemandServiceConnection;
import com.little.galaxy.services.IPlayService;
import com.little.galaxy.services.ReminderOnDemandService;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandStartViewLayout extends LinearLayout {
	private TextView subject;
	private TextView createTime;
	private TextView execTime;
    private TextView Description;
    private ImageButton btn;
    private static final String TAG = "EXEC";

    public ReminderOnDemandStartViewLayout(final Context context, final ReminderOnDemandEntity entity) {

        super(context);
       
      	SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      	String timeCreate =  "The Reminder created at " + sdf.format(new Date(entity.getCreateTime()));
      	
      	int interval = entity.getInterval();
      	Log.d(TAG, "Interval: " + interval);
      	int subtract = (int)(System.currentTimeMillis() - entity.getCreateTime());
      	Log.d(TAG, "Subtract: " + subtract);
      	int slot = interval - subtract;
      	int minutes = slot/(60*1000);
      	minutes = minutes < 0 ? 0 : minutes;
      	Log.d(TAG, "reminging: " + minutes + "Minute");
    
        btn = new ImageButton(context);
        btn.setImageDrawable((getResources().getDrawable(R.drawable.stop)));
        btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					ReminderOnDemandServiceConnection conn = new ReminderOnDemandServiceConnection();
					context.bindService(new Intent(context,ReminderOnDemandService.class), conn, Context.BIND_AUTO_CREATE);
					IPlayService playService = conn.getPlayService();
					if (playService != null){
						playService.stop(String.valueOf(entity.getId()));
					}
				} catch (RemoteException e) {
					Log.e(TAG, "Call remote service stop() failed!");
					e.printStackTrace();
				}
				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
				dbService.updateByState(entity, ReminderState.Cancel.getState());
			}
        });
        setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        
        LayoutParams childBtnParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childBtnParams.gravity = Gravity.RIGHT;
        childBtnParams.setMargins(150, 5, 0, 0);	  
       
        
        LinearLayout childLayout = new LinearLayout(context);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.setMargins(5, 3, 5, 0);

        this.subject = new TextView(context);
        this.subject.setText(entity.getName());
        this.subject.setTextSize(18f);
        this.subject.setTextColor(Color.GRAY);
        childLayout.addView(this.subject, childParams);
        
        this.createTime = new TextView(context);
        this.createTime.setText(timeCreate);
        this.createTime.setTextSize(10f);
        this.createTime.setTextColor(Color.GRAY);
        childLayout.addView(this.createTime, childParams);
        
        this.execTime = new TextView(context);
        this.execTime.setText(context.getResources().getString(R.string.reminder_exec_time) + minutes + " minute");
        this.execTime.setTextSize(10f);
        this.execTime.setTextColor(Color.GRAY);
        childLayout.addView(this.execTime, childParams);

        this.Description = new TextView(context);
        this.Description.setText("Will get from automatically translating of your speak!");
        this.Description.setTextSize(10f);
        this.Description.setTextColor(Color.GRAY);
        childLayout.addView(this.Description, childParams);
        
        this.addView(childLayout, parentParams);
        this.addView(this.btn, childBtnParams);
    }
}
