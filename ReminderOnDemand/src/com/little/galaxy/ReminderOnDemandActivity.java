package com.little.galaxy;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ReminderOnDemandActivity extends Activity {
	MediaRecorder mRecorder;
	File mSampleFile = null;
	static final String SAMPLE_PREFIX = "ReminderOnDemandRecord";
	static final String SAMPLE_EXTENSION = ".mp3"; 
    private static final String TAG = "ReminderOnDemand";
    private boolean isRecording = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_on_demand);
        Button speakBtn = (Button)findViewById(R.id.button1);
        speakBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            	if (isRecording){
            		doRecording();   
            		isRecording = false;
            	} else{
            		stopRecording();
            		addToDB();
            		isRecording = true;
            	}
            		
              }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_on_demand, menu);
        return true;
    }
    
    private void doRecording(){
    	 mRecorder = new MediaRecorder();
         mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
         mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
         mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
         mRecorder.setOutputFile(mSampleFile.getAbsolutePath());
         try {
			mRecorder.prepare();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
         mRecorder.start();

         if (mSampleFile == null) {
 	          File sampleDir = Environment.getExternalStorageDirectory();
 	        
 	          try { mSampleFile = File.createTempFile(SAMPLE_PREFIX, SAMPLE_EXTENSION, sampleDir);
 	          } catch (IOException e) {
 	              Log.e(TAG,"sdcard access error");
 	              return;
 	          }
         }
    }
    
    private void stopRecording(){
    	mRecorder.stop();
        mRecorder.release();
    }
    
    protected void addToDB() {
        ContentValues values = new ContentValues(3);
        long current = System.currentTimeMillis();
            
        values.put(MediaStore.Audio.Media.TITLE, "test_audio");
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.DATA, mSampleFile.getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();
        
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
      }
    
}
