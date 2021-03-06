package com.little.galaxy;

import java.io.File;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class RecordOnDemand {
	
	private Context ctx;
	MediaRecorder mRecorder;
	File mSampleFile = null;
	static final String SAMPLE_PREFIX = "ReminderOnDemandRecord";
	static final String SAMPLE_EXTENSION = ".mp3"; 
    private static final String TAG = "RecordOnDemand";
    
    public RecordOnDemand(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public void doRecording(){
		//try SD card first
        File sDDir = Environment.getExternalStorageDirectory();
        if (sDDir.exists() && sDDir.canWrite()){
      	  try { 
	        	  mSampleFile = File.createTempFile(SAMPLE_PREFIX, SAMPLE_EXTENSION, sDDir);
	          } catch (IOException e) {
	              Log.e(TAG,"sdcard access error");
	          }
        } else{//internal storage
      	  try {
				mSampleFile = File.createTempFile(SAMPLE_PREFIX, SAMPLE_EXTENSION, ctx.getFilesDir());
			} catch (IOException e) {
				Log.e(TAG,"internal storage access error");
			}
        }
    	Log.d(TAG, "record file to " + mSampleFile.getAbsolutePath());
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
        Log.d(TAG, "record start");
   }
   
   public String stopRecording(){
	   if (mRecorder != null){
		   mRecorder.stop();
		   mRecorder.release();
		   Log.d(TAG, "record stop");
		   if (mSampleFile.exists()){
			   Log.d(TAG, "record as " + mSampleFile.getAbsolutePath());
			   return mSampleFile.getAbsolutePath();
		   }
	   }
	   return null;
   }
   
   protected void persist(Context ctx) {
       ContentValues values = new ContentValues(3);
       long current = System.currentTimeMillis();
           
       values.put(MediaStore.Audio.Media.TITLE, "test_audio");
       values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
       values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
       values.put(MediaStore.Audio.Media.DATA, mSampleFile.getAbsolutePath());
       ContentResolver contentResolver = ctx.getContentResolver();
       
       Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
       Uri newUri = contentResolver.insert(base, values);

       ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
     }

}
