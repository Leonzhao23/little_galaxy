package com.little.galaxy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ReminderOnDemandActivity extends Activity {
    //private boolean isRecording = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
        Button speakBtn = (Button)findViewById(R.id.button1);
        speakBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
//            	if (isRecording){
//            		doRecording();   
//            		isRecording = false;
//            	} else{
//            		stopRecording();
//            		addToDB();
//            		isRecording = true;
//            	}
            	ReplayOnDemand replay = new ReplayOnDemand();
            	replay.play(ReminderOnDemandActivity.this);
              }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_on_demand, menu);
        return true;
    }
   
    
}
