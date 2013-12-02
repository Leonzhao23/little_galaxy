package com.little.galaxy.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.little.galaxy.R;

public class ReplayOnDemand {
	
	public void play(Context ctx){
		MediaPlayer mp = MediaPlayer.create(ctx, R.raw.test);	
    	mp.start();
    	mp.setOnCompletionListener(new OnCompletionListener(){
    		public void onCompletion(MediaPlayer mp1){
    			
    		}
    	});
	}

}
