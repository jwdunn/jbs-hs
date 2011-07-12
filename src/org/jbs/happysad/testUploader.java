package org.jbs.happysad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Formatter;

import android.util.Log;

public class testUploader {
	public static String TAG = "testuploader";
	public static String newUser(String s){
		
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
	
	public static void main(String[] args){
		
		newUser("Hello@blah.com");
	}
}
