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

import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.storages.DBServiceFactory;
import com.little.galaxy.storages.DBType;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandListViewAdaptor extends BaseAdapter {
	
	private final Context context;
	private final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	
	public ReminderOnDemandListViewAdaptor(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities){
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
		ReminderOnDemandEntity entity = reminderOnDemandEntities.get(position);
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date(entity.getCreateTime() + entity.getInterval());
		String formatDate = sdf.format(date);
		String desc = "Remindered at " + formatDate;
		return new ReminderOnDemandViewLayout(this.context, entity.getId(), entity.getName(), desc);
	}
	
	 private final class ReminderOnDemandViewLayout extends LinearLayout {
		 
	        private TextView subject;
	        private TextView Description;
	        private ImageButton btn;

	        public ReminderOnDemandViewLayout(final Context context, final long id, String subject, String Description) {

	            super(context);
	            btn = new ImageButton(context);
	            btn.setImageDrawable((getResources().getDrawable(android.R.drawable.sym_call_incoming)));
	            btn.setOnClickListener(new View.OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				IDBService dbService = DBServiceFactory.getDBService(DBType.SQLite, context);
	    				dbService.delete(id);
	    			}
	            });
	            setOrientation(LinearLayout.HORIZONTAL);

	            LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	            
	            LayoutParams childBtnParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	            childBtnParams.gravity = Gravity.RIGHT;
	            childBtnParams.setMargins(100, 5, 0, 0);
	           
	            
	            LinearLayout childLayout = new LinearLayout(context);
	            childLayout.setOrientation(LinearLayout.VERTICAL);
	            LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		                ViewGroup.LayoutParams.WRAP_CONTENT);
	            childParams.setMargins(5, 3, 5, 0);

	            this.subject = new TextView(context);
	            this.subject.setText(subject);
	            this.subject.setTextSize(18f);
	            this.subject.setTextColor(Color.GRAY);
	            childLayout.addView(this.subject, childParams);

	            this.Description = new TextView(context);
	            this.Description.setText(Description);
	            this.Description.setTextSize(10f);
	            this.Description.setTextColor(Color.GRAY);
	            childLayout.addView(this.Description, childParams);
	            
	            this.addView(childLayout, parentParams);
	            this.addView(this.btn, childBtnParams);
	        }
	    }

}
