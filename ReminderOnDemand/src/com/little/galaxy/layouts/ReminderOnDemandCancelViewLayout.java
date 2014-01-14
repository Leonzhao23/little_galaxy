package com.little.galaxy.layouts;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceProvider;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandCancelViewLayout extends LinearLayout{
	private TextView subject;
    private TextView Description;
    private ImageButton btn;

    public ReminderOnDemandCancelViewLayout(final Context context, final ReminderOnDemandEntity entity) {
    	
        super(context);
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String desc =  "The Reminder was cancelled at " + sdf.format(new Date(entity.getExecTime()));
		
        btn = new ImageButton(context);
        btn.setImageDrawable((getResources().getDrawable(R.drawable.delete)));
        btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IDBService dbService = DBServiceProvider.getDBService(DBType.SQLite, context);
				dbService.delete(entity.getId());
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

        this.Description = new TextView(context);
        this.Description.setText(desc);
        this.Description.setTextSize(10f);
        this.Description.setTextColor(Color.GRAY);
        childLayout.addView(this.Description, childParams);
        
        this.addView(childLayout, parentParams);
        this.addView(this.btn, childBtnParams);
    }
}
