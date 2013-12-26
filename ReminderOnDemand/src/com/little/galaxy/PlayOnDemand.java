package com.little.galaxy;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class PlayOnDemand {
	
	private MediaPlayer mp = null;
	
	public PlayOnDemand(Context ctx, String fileLoc){
		mp = MediaPlayer.create(ctx, Uri.parse("file://" + fileLoc));
	}
	
	public void play(){
		mp.start();
	}
	
	public void stop(){
		mp.stop();
	}

}
