package com.little.galaxy;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_NET;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class VoiceRecognizeOnDemand {
	public static final String LANGUAGE = "Language";
    private static final String DEFAULT_LANGUAGE = "zh-CN";
    private static final String GOOGLE_VOICE_API_URL = "http://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&maxresults=1&lang=";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    private static final int DEFAULT_READ_TIMEOUT = 20 * 1000;
    private static final String CONTENT_TYPE_WAV = "audio/L16;rate=16000";
    
    
    public  void startVoiceRecognizer(final byte[] wavData){
		final HttpURLConnection connection = getConnection();
		if (connection != null){
			new Thread(){
                @Override
                public void run(){
                    try {
                        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                        dos.write(wavData);
                        dos.flush();
                        dos.close();

                        InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), Charset.forName("utf-8"));
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder sb = new StringBuilder();
                        String tmpStr = null;
                        while ((tmpStr = bufferedReader.readLine()) != null){
                            sb.append(tmpStr);
                        }  
                        startParseJson(sb.toString());
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }.start();
        }
    }
    
    
	private HttpURLConnection getConnection(){
        HttpURLConnection connection = null;
        try{
            URL httpUrl = new URL(GOOGLE_VOICE_API_URL + DEFAULT_LANGUAGE);
            connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("User-Agent",USER_AGENT);
            connection.setRequestProperty("Content-Type",CONTENT_TYPE_WAV);
        }catch (MalformedURLException ex){
            Log.e(TAG_NET,"getConnection();Invalid url format",ex);
        }catch (ProtocolException ex){
            Log.e(DEFAULT_LANGUAGE, "getConnection();Un support protocol",ex);
        }catch (IOException ex){
            Log.e(DEFAULT_LANGUAGE,"getConnection();IO error while open connection",ex);
        }
        return connection;
    }
	
	
    private void startParseJson(String jsonString){
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            int status = jsonObject.getInt("status");
            if (status == 0){
                JSONArray hypotheses = jsonObject.getJSONArray("hypotheses");
                if (hypotheses!= null && hypotheses.length() > 0){
                    JSONObject hypot = hypotheses.optJSONObject(0);
                    String speechText = hypot.getString("utterance");
                   
                }
            }
        }catch (JSONException ex){
            Log.e(TAG_NET,"Decode JSON error",ex);
        }
    }
}
