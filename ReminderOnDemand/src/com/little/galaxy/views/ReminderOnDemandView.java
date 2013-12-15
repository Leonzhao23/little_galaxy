package com.little.galaxy.views;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.entities.ReminderOnDemandEntity;

public class ReminderOnDemandView extends BaseAdapter {
	
	private final Context context;
	private final List<ReminderOnDemandEntity> reminderOnDemandEntities;
	
	public ReminderOnDemandView(Context context, List<ReminderOnDemandEntity> reminderOnDemandEntities){
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
		return new ReminderOnDemandViewLayout(this.context, entity.getName(), String.valueOf(entity.getDelayed()));
	}
	
	 private final class ReminderOnDemandViewLayout extends LinearLayout {

	        private TextView name;
	        private TextView rating;

	        public ReminderOnDemandViewLayout(Context context, String name, String rating) {

	            super(context);
	            setOrientation(LinearLayout.VERTICAL);

	            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	            params.setMargins(5, 3, 5, 0);

	            this.name = new TextView(context);
	            this.name.setText(name);
	            this.name.setTextSize(16f);
	            this.name.setTextColor(Color.WHITE);
	            this.addView(this.name, params);

	            this.rating = new TextView(context);
	            this.rating.setText(rating);
	            this.rating.setTextSize(16f);
	            this.rating.setTextColor(Color.GRAY);
	            this.addView(this.rating, params);
	        }
	    }

}
