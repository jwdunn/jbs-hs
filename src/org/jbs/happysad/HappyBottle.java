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
	private int lati;
	private int longi;
	private short emo;
	private String msg;
	private long time;
	private long uid;
	
	
	/**
	 * Constructs a HappyBottle object to retrieve and save updates
	 * @param id
	 * @param la
	 * @param lo
	 * @param e
	 * @param t
	 * @param time
	 */
	public HappyBottle(long uid, int lati, int longi, short emo, String msg, long time) {
		this.lati = lati;
		this.longi = longi;
		this.emo = emo;
		this.msg = msg;
		this.time = time;
		this.uid = uid;
	}	
	
	/**
	 * Puts all the values into a ContentValues object
	 * @return all values as a ContentValues object
	 */
	public ContentValues getAll(){
		ContentValues values = new ContentValues();
		values.put(TIME, time);
		values.put(LAT, lati);
		values.put(LONG, longi);
		values.put(MSG, msg);
		values.put(EMO, emo);
		values.put(UID, uid);
		return values;
	}
	/**
	 * 
	 * @return the longitude of the bottle (float)
	 */
	public int getLong(){
		return longi;
	}
	/**
	 * 
	 * @return the latitude of the bottle (float)
	 */
	public int getLat(){
		return lati;
	}
	/**
	 * 
	 * @return the emotion. 1 for happy 0 for sad
	 */
	public short getEmo(){
		return emo;
	}
	/**
	 * 
	 * @return message (string)
	 */
	public String getMsg(){
		return msg;
	}
	/**
	 * 
	 * @return time teh bottle was created (by the user) long (milliseconds since the epoch)
	 */
	public long getTime(){
		return time;
	}
	/**
	 * 
	 * @return the uid in the bottle. 
	 */
	public long getUID(){
		return uid;
	}
	
	public String toString(){
		String s = "";
		//s+= "bottle:: ";

		s += UID + ": "+ uid + " ";
		s += "lat: " + lati + " ";
		s += "long: " + longi + " ";
		s += "emo: " + emo + " ";
		s += "msg: " + msg + " ";
		s += "time: " + new Timestamp( time) + " ";	

		return s;
	}	
}
