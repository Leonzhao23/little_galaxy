package com.little.galaxy;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

public class CalendarEventOnDemand {
	private final static String calanderURL = "content://com.android.calendar/calendars";  
	private final static String calanderEventURL = "content://com.android.calendar/events";  
	private final static String calanderRemiderURL = "content://com.android.calendar/reminders";
	
	public void reminder(Context ctx){ 
        String calId = "";  
        Cursor userCursor = ctx.getContentResolver().query(Uri.parse(calanderURL), null,   
                null, null, null);  
        if(userCursor.getCount() > 0){  
            userCursor.moveToFirst();  
            calId = userCursor.getString(userCursor.getColumnIndex("_id"));  
              
        }  
        ContentValues event = new ContentValues();  
        event.put("title", "test");  
        event.put("description", "testing");  
        event.put("calendar_id",calId);  
          
        Calendar mCalendar = Calendar.getInstance();  
        mCalendar.set(Calendar.HOUR_OF_DAY,10);  
        long start = mCalendar.getTime().getTime();  
        mCalendar.set(Calendar.HOUR_OF_DAY,11);  
        long end = mCalendar.getTime().getTime();  
          
        event.put("dtstart", start);  
        event.put("dtend", end);  
        event.put("hasAlarm",1);  
          
        Uri newEvent = ctx.getContentResolver().insert(Uri.parse(calanderEventURL), event);  
        long id = Long.parseLong( newEvent.getLastPathSegment() );  
        ContentValues values = new ContentValues();  
        values.put( "event_id", id );  
        values.put( "minutes", 10 );  
        ctx.getContentResolver().insert(Uri.parse(calanderRemiderURL), values);  
        Toast.makeText(ctx, "good", Toast.LENGTH_LONG).show();  
		
	}

}
