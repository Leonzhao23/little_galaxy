package com.little.galaxy;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_RECORD;
import static com.little.galaxy.utils.ReminderOnDemandConsts.RECORD_PREFIX;
import static com.little.galaxy.utils.ReminderOnDemandConsts.RECORD_EXTENSION_RAW;
import static com.little.galaxy.utils.ReminderOnDemandConsts.RECORD_EXTENSION_WAV;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.little.galaxy.exceptions.ReminderOnDemandException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecordOnDemand {
	File mRawFile = null;
	File mWavFile = null;
	
	public static final String AUDIO_SOURCE = "AudioSource";
    private static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    public static final String SAMPLE_RATE = "SampleRate";
    private static final int DEFAULT_SAMPLE_RATE = 16000;
    private static final int DEFAULT_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final short DEFAULT_PER_SAMPLE_IN_BYTES = 2;
    private static final short DEFAULT_PER_SAMPLE_IN_BIT = 16;
    public static final String CHANNELS = "Channels";
    private static final short DEFAULT_CHANNELS = 1; //MONO = 1, STEREO = 2
    
    private int mSampleRate;
    private short mChannels;
    private int mAudioSource;

    private AudioRecord mRecorder;
    private int mBufferSize;
    private int mRecordedLength;
    private byte[] mRecordedData;
    private byte[] wavHeader;
    
    final File internalFileDir;
    
    
    public AudioRecordOnDemand(File internalFileDir) {
		super();
		this.internalFileDir = internalFileDir;
	}
    
    public String getAudioLoc(){
    	return mWavFile.getAbsolutePath();
    }

	private void prepareRecorder() throws ReminderOnDemandException{
    	 mAudioSource = MediaRecorder.AudioSource.MIC;
         mSampleRate = DEFAULT_SAMPLE_RATE;
         mChannels = DEFAULT_CHANNELS;
         
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate,
                AudioFormat.CHANNEL_IN_MONO, DEFAULT_AUDIO_ENCODING);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG_RECORD, "Params are not support by hardware "
                    + "sample rate: " + mSampleRate + 
                    "; channel: " + AudioFormat.CHANNEL_IN_MONO
                    +"; encoding: "
                    + DEFAULT_AUDIO_ENCODING);
            throw new ReminderOnDemandException("failed to get min buffer size of audioRecord");
        }else if (minBufferSize == AudioRecord.ERROR){
            Log.w(TAG_RECORD, "Unable to query hardware for output property");
            minBufferSize = mSampleRate * (120 / 1000) * DEFAULT_PER_SAMPLE_IN_BYTES * mChannels;
        }
        mBufferSize = minBufferSize * 2;
        
        mRecorder = new AudioRecord(mAudioSource, mSampleRate,
                AudioFormat.CHANNEL_IN_MONO, DEFAULT_AUDIO_ENCODING, mBufferSize);
        if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED){
            Log.e(TAG_RECORD, "AudioRecord initialize failed");
            throw new ReminderOnDemandException("AudioRecord initialize failed");
        }

        mRecordedLength = 0;
        int maxRecordLength = mSampleRate * mChannels * DEFAULT_PER_SAMPLE_IN_BYTES * 35;
        mRecordedData = new byte[maxRecordLength];
    }
    
	public void doRecording() throws ReminderOnDemandException{
		prepareRecorder();
	    if (mRecorder == null
	            || mRecorder.getState() != AudioRecord.STATE_INITIALIZED){
	        return;
	    }
	    mRecorder.startRecording();
	    
	    new Thread(){
            @Override
            public void run(){
            	FileOutputStream fos = null; 
        	    try {
        	    	mRawFile = createAudioFile(RECORD_PREFIX, RECORD_EXTENSION_RAW);
                    fos = new FileOutputStream(mRawFile);
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            	if (mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
        	    	byte[] tmpBuffer = new byte[mBufferSize/2];
                    while (mRecorder != null
                            && mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                        int numOfRead = mRecorder.read(tmpBuffer, 0 , tmpBuffer.length);
                        if (numOfRead < 0){
                            break;
                        }

//                        float sum = 0;
//                        for (int i=0; i < tmpBuffer.length; i+=2){
//                            short t = (short)(tmpBuffer[i] | (tmpBuffer[i+1] <<8 ));
//                            sum += Math.abs(t);
//                        }
//                       
//                        float rms = sum/(tmpBuffer.length * 2);
                        if (mRecordedData.length > mRecordedLength + numOfRead){
                            System.arraycopy(tmpBuffer, 0, mRecordedData, mRecordedLength, numOfRead);
                            try { 
                            	 fos.write(tmpBuffer); 
                            } catch (IOException e) { 
                                e.printStackTrace(); 
                            } 
                            mRecordedLength += numOfRead;
                        }else {
                            break;
                        }       
                    }
              }

        	  try {
                    fos.close();
                } catch (IOException e) { 
                    e.printStackTrace(); 
              } 
            }
	    }.start();
	
	}
	
	public void stopRecording(){
        if (mRecorder != null){
        	//&& mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
            mRecorder.stop();
            mRecorder = null;
        }
    }
	
	private File createAudioFile(String prefix, String suffix) { 
		File file = null;
		//try SD card first
        File sDDir = Environment.getExternalStorageDirectory();
        if (sDDir.exists() && sDDir.canWrite()){
      	  try { 
      		  file = File.createTempFile(prefix, suffix, sDDir);
	          } catch (IOException e) {
	              Log.e(TAG_RECORD, "sdcard access error");
	          }
        } else{//internal storage
      	  try {
      		  file = File.createTempFile(prefix, suffix, internalFileDir);
			} catch (IOException e) {
				Log.e(TAG_RECORD, "internal storage access error");
			}
        }
    	Log.d(TAG_RECORD, "record file to " + file.getAbsolutePath());
        return file;
    } 
	
	private void createWavHeaderIfNeed(boolean forceCreate){
	    if (!forceCreate && wavHeader != null){
	        return;
	    }
	    // sample rate * number of channel * bit per sample / bit per bytes
	    int avgBytesPerSec = mSampleRate * mChannels * DEFAULT_PER_SAMPLE_IN_BIT / 8;
	    wavHeader = new byte[]{
	            'R','I','F','F',           //id = RIFF , fixed chars
	            0, 0, 0, 0,                // RIFF WAVE chunk size = 36 + data length
	            'W','A','V','E',           //  Type
	            /* Format chunk */
	            'f','m','t',' ',          // id = 'fmt '
	            16, 0, 0, 0,              // format chunk size = 16, if 18, means existing extension message
	            1, 0,                     // format tag, 0x0001 = 16 pcm
	            (byte)mChannels, 0, // number of channels (MONO = 1, STEREO =2)
	            /* 4 bytes , sample rate */
	            (byte)(mSampleRate & 0xff),
	            (byte)((mSampleRate >> 8) & 0xff),
	            (byte)((mSampleRate >> 16) & 0xff),
	            (byte)((mSampleRate >> 24) & 0xff),
	            /* 4 bytes average bytes per seconds */
	            (byte)(avgBytesPerSec & 0xff),
	            (byte)((avgBytesPerSec >> 8) & 0xff),
	            (byte)((avgBytesPerSec >> 16) & 0xff),
	            (byte)((avgBytesPerSec >> 24) & 0xff),
	            /* 2 bytes, block align */
	            /******************************
	             *              sample 1
	             ******************************
	             * channel 0 least| channel 0 most|
	             * ******************************/
	            (byte)(DEFAULT_PER_SAMPLE_IN_BIT * mChannels / 8), // per sample in bytes
	            0,
	            /* 2 bytes, Bits per sample */
	            16, 0,
	            /* data chunk */
	            'd','a','t','a', /// Id = 'data'
	            0, 0, 0, 0   // data size, set 0 due to unknown yet
	    };
	}

	private void setWavHeaderInt(int offset,int value){
	    if (offset < 0 || offset > 40){
	        //total length = 44, int length = 4,
	        //44 - 4 = 40
	        throw new IllegalArgumentException("offset out of range");
	    }
	    createWavHeaderIfNeed(false);

	    wavHeader[offset++] = (byte)(value & 0xff);
	    wavHeader[offset++] = (byte)((value >> 8) & 0xff);
	    wavHeader[offset++] = (byte)((value >> 16) & 0xff);
	    wavHeader[offset] = (byte)((value >> 24) & 0xff);
	}

	public byte[] getWavData(){
	    setWavHeaderInt(4, 36 + mRecordedLength);
	    setWavHeaderInt(40, mRecordedLength);
	    byte[] wavData = new byte[44 + mRecordedLength];
	    FileOutputStream fos = null; 
	    try {
	    	mWavFile = createAudioFile(RECORD_PREFIX, RECORD_EXTENSION_WAV);
            fos = new FileOutputStream(mWavFile);
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
	    System.arraycopy(wavHeader, 0, wavData, 0, wavHeader.length);
	    System.arraycopy(mRecordedData, 0, wavData, wavHeader.length, mRecordedLength);
	    try { 
	    	  fos.write(wavHeader); 
	      	  fos.write(mRecordedData); 
		    } catch (IOException e) { 
	          e.printStackTrace(); 
		} 
	    
	    try {
            fos.close();
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
	    return wavData;
	}

}
