//new nethelper
package org.jbs.happysad;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.jbs.happysad.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;
import org.jbs.happysad.HappyBottle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Base64;
import android.util.Log;

public class NetHelper {
	private String TAG = "NETHELPER";
	private long myid;
	public NetHelper(long id){
		myid = id;
	}
	
	
	public ArrayList<HappyBottle> doTask(Task t, HappyBottle b){
		switch(t){
		case SEND:
			upload(b);
			return null;
		default:
			return download(t);
		}
	}
	
	private ArrayList<HappyBottle> download(Task t) {
        BufferedReader in = null;
        String page = "error";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://stark-water-134.heroku.com/bottles.json"));
            if( t.equals(Task.GETMINE)){
            	request.setURI(new URI("http://stark-water-134.heroku.com/bottles/" + myid+".json"));
            }
            BasicHeader declareAuth = new BasicHeader("Authorization", "Basic " + Base64.encodeToString("dhh:secret".getBytes(), Base64.DEFAULT) + "==");
            request.setHeader(declareAuth);
            
            HttpResponse response = client.execute(request);
            in = new BufferedReader
            (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            page = sb.toString();
            Log.d(TAG, "this is the download dump " + page);
            }
        catch (Exception e){
        	e.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parse(page);
    }
    
    
 
 
    
    private String upload(HappyBottle b) {
		
		String page = "erorr";
		HttpClient client = new DefaultHttpClient();
	    HttpPost request = new HttpPost();
	    Object[] values = {b.getEmo(), b.getLat(), b.getLong(), b.getMsg(), b.getUID(), b.getTime()};  
		Formatter f = new Formatter();
		f.format("bottle[emo]=%s&bottle[lat]=%s&bottle[long]=%s&bottle[msg]=%s&bottle[user_id]=%s&bottle[time]=%s&commit=Create Bottle", values);
		String data = f.toString();
		BufferedReader in = null;
		URI url;
		try { 
			url = new URI("http", "stark-water-134.heroku.com", "/bottles", data, null);
			request.setURI(url);
			Log.d(TAG, "data: "+ data);
			Log.d(TAG, "uRL: " + url);
		} catch (URISyntaxException e1) {
			
			e1.printStackTrace();
		}
	    try {
		
	    BasicHeader declareAuth = new BasicHeader("Authorization", "Basic " + Base64.encodeToString("dhh:secret".getBytes(), Base64.DEFAULT) + "==");
	    request.setHeader(declareAuth);
	    
	    HttpResponse response = client.execute(request);
	    in = new BufferedReader
	    (new InputStreamReader(response.getEntity().getContent()));
	    StringBuffer sb = new StringBuffer("");
	    String line = "";
	    String NL = System.getProperty("line.separator");
	            while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();
	            page = sb.toString();
	            
	            }
	        catch (Exception e){
	        	e.printStackTrace();
	        }
	        finally {
	            if (in != null) {
	                try {
	                    in.close();
	                    } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return page;
    
}
	

	
	
	public ArrayList<HappyBottle> parse(String in){
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
			   Log.e(TAG,  "array error" + e.toString());
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
