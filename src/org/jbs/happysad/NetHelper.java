package org.jbs.happysad;

import static android.provider.BaseColumns._ID;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.UID;


import org.jbs.happysad.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jbs.happysad.HappyBottle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

public class NetHelper {
	private String TAG = "NETHELPER";
	private long myid;
	public NetHelper(long id){
		myid = id;
	}
	
	public String send(HappyBottle bottle){
		ContentValues c = bottle.getAll();
		//Log.v(TAG, "ContentValues c = bottle.getAll()");
		String d = upload(c);
		return d;
	}
	
	
	
	public String doTask(Task task){
		String result = "connecting...";
		HttpURLConnection con = null;
		try {
	    	  URL url3 = new URL ("http://popper.cs-i.brandeis.edu:8088/tim/jbs2011/hsdb.servlet?action=getMine&user="+myid);
	          URL url1 = new URL ("http://popper.cs-i.brandeis.edu:8088/tim/jbs2011/hsdb.servlet?action=getAll");
	          Log.e(TAG, "http://popper.cs-i.brandeis.edu:8088/tim/jbs2011/hsdb.servlet?action=getMine&user="+myid);
	          URL url = url1;
	    	  // Check if task has been interrupted
	    	  if (Thread.interrupted())
	    		  throw new InterruptedException();

	    	  // Build RESTful query for Google API
	    	  //     String q = URLEncoder.encode(original, "UTF-8");
	         
	    	  switch(task) {
	    	  case GETMINE:
	    		  url = url3;
	    		  break;
	    	  case GETALL:
	    		  url=url1;
	    		  break;
	         }
	         
	         con = (HttpURLConnection) url.openConnection();
	         con.setReadTimeout(10000 /* milliseconds */);
	         con.setConnectTimeout(15000 /* milliseconds */);
	         con.setRequestMethod("GET");
	         con.setDoInput(true);

	         
	         // Start the query
	         con.connect();

	         // Check if task has been interrupted
	         if (Thread.interrupted())
	            throw new InterruptedException();

	         // Read results from the query
	         BufferedReader reader = new BufferedReader(
	        		 new InputStreamReader(con.getInputStream(), "UTF-8"));
	         String payload = reader.readLine();
	         reader.close();

	         // Parse to get translated text
	         return payload;
	         
	      } catch (IOException e) {
	         Log.e(TAG, "IOException", e);
	      } catch (InterruptedException e) {
	         Log.d(TAG, "InterruptedException", e);
	         result = "interruptedexception";
	      } finally {
	         if (con != null) {
	            con.disconnect();
	         }
	      }

	      // All done
	      Log.d(TAG, "   -> returned " + result);
	      return result;
	   }

	private String upload(ContentValues c) {
		Log.v(TAG, "SENDING THROUGH NETHELPER");  
		String result = "uploading..";
		HttpURLConnection con = null;
		String surl = "http://popper.cs-i.brandeis.edu:8088/tim/jbs2011/hsdb.servlet?action=store&lat=";
		   surl += c.getAsFloat(LAT);
		   surl += "&lon=";
		   surl += c.getAsFloat(LONG);
		   surl += "&t=";
		   surl += c.getAsLong(TIME);
		   surl += "&user=";
		   surl += c.getAsString(UID);
		   surl += "&emo=";
		   surl += c.getAsShort(EMO);
		   surl += "&msg=";
		   surl += c.getAsString(MSG);
		   
		   Log.v(TAG, "surl = " + surl);
		try{
			   URL url = new URL (surl);
			   con = (HttpURLConnection) url.openConnection();
			   con.setReadTimeout(10000 /* milliseconds */);
			   con.setConnectTimeout(15000 /* milliseconds */);
			   con.setRequestMethod("GET");
			   con.setDoInput(true);
			   con.connect();
		         
			   if (Thread.interrupted())
		    		  throw new InterruptedException();
		  
			   BufferedReader reader = new BufferedReader(
		        		 new InputStreamReader(con.getInputStream(), "UTF-8"));
		       String payload = reader.readLine();
		       reader.close();
		       Log.d(TAG, "sent a bottle!");
		       return payload;
	       
		   } catch (IOException e) {
			   Log.e(TAG, "IOException", e);
		   } catch (InterruptedException e) {
			   Log.d(TAG, "InterruptedException", e);
			   result = "interrupted";
		   } finally {
			   if (con != null) {
				   con.disconnect();
			   }
		   }

		   // All done
		   Log.d(TAG, "   -> returned " + result);
		   return result;
	   }
	
	
	public ArrayList<HappyBottle> parse(String in){
		   ArrayList<HappyBottle> a = new ArrayList<HappyBottle>();
		   try {

			  // Log.w(TAG, "Trying to add the new jsonarray");
			   JSONArray jarray = new JSONArray(in);
			   //Log.w(TAG, "added new jarray");
			   for (int i = 0; i<jarray.length(); i++){
				   JSONObject o = jarray.getJSONObject(i);
				   HappyBottle b = parseone(o);
				   a.add(b);
			   }  
		   } catch (JSONException e) {
			   Log.e(TAG + "array error", e.toString());
			   a.add(new HappyBottle(myid , (float) 1, (float) 1,(short) 1, "JSONARRAYERROR",1) );
		   }
		   catch (Exception e){
			   Log.e(TAG + "mysterious other error", e.toString());
		   }
		   return a;
	}
	   
	   
	   //Given a JSONobject corresponding to a bottle, turn it into a bottle;
	private HappyBottle parseone(JSONObject o){
		try {
			   float lati = (float) o.getDouble("lat");
			   float longi = (float) o.getDouble("lon");
			   short emo = (short) o.getInt("emo");
			   String msg = o.getString("msg");
			   long time = o.getLong("t");
			   long uid = myid;
			   return new HappyBottle( uid, lati , longi , emo, msg ,time);
		   	} catch (JSONException e) {
				e.printStackTrace();
				Log.e(TAG + "object error", e.toString());
				return new HappyBottle(myid , (float) 1, (float) 1,(short) 1, "JSONOBJECTERROR",1) ;
		   	}
	}
	   
	   
}
