package org.jbs.happysad;

import org.jbs.happysad.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Formatter;
import org.jbs.happysad.HappyBottle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class NetHelper {
	private String TAG = "NETHELPER";
	private long myid;
	public NetHelper(long id){
		myid = id;
	}
	
	public String send(HappyBottle bottle){
		//Log.v(TAG, "ContentValues c = bottle.getAll()");
		String d = upload(bottle);
		return d;
	}
	
	public ArrayList<HappyBottle> doTask(Task task){
		String result = "connecting...";
		HttpURLConnection con = null;
		try {
	    
			URL url = new URL ("http://stark-water-134.heroku.com/bottles.json");
			
	          
	    	  // Check if task has been interrupted
	    	  if (Thread.interrupted())
	    		  throw new InterruptedException();

	    	  // Build RESTful query for Google API
	    	  //     String q = URLEncoder.encode(original, "UTF-8");
	        
	         
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
	         Log.d(TAG, "bottles.json: " + payload );
	         // Parse to get translated text
	         return parse(payload, task);
	         
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
	    return new ArrayList<HappyBottle>();
	      
	   }

	private String upload(HappyBottle b) {
		Log.d(TAG, "SENDING THROUGH NETHELPER");  
		String result = "uploading..";
		
		Log.d(TAG, "Time: " +  b.getTime());
		//String t = Long.toString(b.getTime());
		Object[] values = {b.getEmo(), b.getLat(), b.getLong(), b.getMsg(), b.getUID(), b.getTime()};  
		
		 
    	Formatter f = new Formatter();
    	f.format("bottle[emo]=%s&bottle[lat]=%s&bottle[long]=%s&bottle[msg]=%s&bottle[user_id]=%s&bottle[time]=%s&commit=Create Bottle", values);
    	String data = f.toString();
		
    	 try {
             
             // Send the request
             URL url = new URL("http://stark-water-134.heroku.com/bottles");
             URLConnection conn = url.openConnection();
             conn.setDoOutput(true);
             OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
             
             //write parameters
             writer.write(data);
             writer.flush();
             
             // Get the response
             StringBuffer answer = new StringBuffer();
             BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line;
             while ((line = reader.readLine()) != null) {
                 answer.append(line);
             }
             writer.close();
             reader.close();
             
             //Output the response
             result = answer.toString();
             
         } catch (MalformedURLException ex) {
        	 Log.v(TAG, "Malformed URL exception in upload");
        	 ex.printStackTrace();
         } catch (IOException ex) {
        	 Log.v(TAG, "IO exception in upload");
        	 ex.printStackTrace();
         }
         catch (Exception e) {
        	 e.printStackTrace();
        	 Log.v(TAG, "lame error I don't understand");
         }
         Log.d(TAG, "upload result: " + result);
         return result;
     }
		
	
	public String newUser(String s){
	
		Formatter f = new Formatter();
		Object[] values = {s};
    	f.format("user[email]=?", values);
    	String data = f.toString();
		String result = data;
    	try {
            
            // Send the request
            URL url = new URL("http://stark-water-134.heroku.com/users");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            
            //write parameters
            writer.write(data);
            writer.flush();
            
            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();
            
            //Output the response
            result = answer.toString();
            
        } catch (MalformedURLException ex) {
       	 Log.v(TAG, "Malformed URL exception in upload");
       	 ex.printStackTrace();
        } catch (IOException ex) {
       	 Log.v(TAG, "IO exception in upload");
       	 ex.printStackTrace();
        }
        catch (Exception e) {
       	 e.printStackTrace();
       	 Log.v(TAG, "lame error I don't understand");
        }
        Log.d(TAG, "upload result: " + result);
        return result;
		
		
		
	}
	
	
	public ArrayList<HappyBottle> parse(String in, Task task){
		   ArrayList<HappyBottle> a = new ArrayList<HappyBottle>();
		   try {

			  // Log.w(TAG, "Trying to add the new jsonarray");
			   JSONArray jarray = new JSONArray(in);
			   //Log.w(TAG, "added new jarray");
			   for (int i = 0; i<jarray.length(); i++){
				   JSONObject o = jarray.getJSONObject(i);
				   HappyBottle b = newparseone(o);
				   Log.d(TAG, "successfully parsed new happybottle - " + b);
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
	   
	  
	private HappyBottle newparseone(JSONObject o){
		try{
			String bottle = o.getString("bottle");
			JSONObject o2 = new JSONObject(bottle);
			return newparsetwo(o2);
		}
		catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "object error" + e.toString());
			return new HappyBottle(myid , (float) 1, (float) 1,(short) 1, "JSONOBJECTERROR",1) ;
	   	}
		
	}
	
	
	private HappyBottle newparsetwo(JSONObject o){
		try {
			   float lati = (float) o.getDouble("lat");
			   float longi = (float) o.getDouble("long");
			   short emo = (short) o.getInt("emo");
			   String msg = o.getString("msg");
			   long time = o.getLong("time");
			   long uid = o.getLong("user_id");
			   return new HappyBottle( uid, lati , longi , emo, msg ,time);
		   	} catch (JSONException e) {
				e.printStackTrace();
				Log.e(TAG + "object error", e.toString());
				return new HappyBottle(myid , (float) 1, (float) 1,(short) 1, "JSONOBJECTERROR",1) ;
		   	}
	}
	
	
	   
	   
}
