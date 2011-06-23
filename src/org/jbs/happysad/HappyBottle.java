package org.jbs.happysad;

import android.content.ContentValues;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.UID;

import java.sql.Timestamp;

/**
 * Happy Bottle is the object used to express 1 row in the database, or 1 update.
 * @author HS
 * 
 */
public class HappyBottle {
	//fields
	private float lati;
	private float longi;
	private float emo;
	private String msg;
	private long time;
	private long uid;
	
	/**
	 * Constructs a HappyBottle object
	 * @param id
	 * @param la
	 * @param lo
	 * @param e
	 * @param t
	 * @param time
	 */
	public HappyBottle(long id, float la, float lo, float e, String t, long time ){
		lati = la;
		longi = lo;
		emo = e;
		msg = t;
		this.time = time;
		uid = id;
	}	
	
	
	/**
	 * Constructs HappyBottle object
	 * @param id
	 * @param GPS_la
	 * @param GPS_lo
	 * @param Network_la
	 * @param Network_lo
	 * @param e
	 * @param t
	 * @param time
	 */
	public HappyBottle(long id, float GPS_la, float GPS_lo, float Network_la, float Network_lo, float e, String t, long time ){
	
		float la;
		float lo;
		
		if (GPS_lo == 0 && GPS_la == 0){
			la = Network_la;
			lo = Network_lo;
		}
		
		else{
			la = GPS_la;
			lo = GPS_lo;			
		}
		
		lati = la;
		longi = lo;
		emo = e;
		msg = t;
		this.time = time;
		uid = id;
	}
	
	/**
	 * Puts all the values into a ContentValues object
	 * @return all values as a ContentValues object
	 */
	public ContentValues getAll(){
		ContentValues values = new ContentValues();
		values.put(TIME, this.time);
		values.put(LAT, lati);
		values.put(LONG, longi);
		values.put(MSG, this.msg);
		values.put(EMO, this.emo);
		values.put(UID, uid);
		return values;
	}
	
	public float getLong(){
		return longi;
	}
	public float getLat(){
		return lati;
	}
	public float getEmo(){
		return emo;
	}
	public String getMsg(){
		return this.msg;
	}
	public float getTime(){
		return time;
	}
	public long getUID(){
		return uid;
	}
	
	public String toString(){
		String s = "";
		//s+= "bottle:: ";
		s += "uid: " + uid + " ";
		s += "lat: " + lati + " ";
		s += "long: " + longi + " ";
		s += "emo: " + emo + " ";
		s += "msg: " + msg + " ";
		s += "time: " + new Timestamp( time) + " ";	
		return s;
	}
	
}
