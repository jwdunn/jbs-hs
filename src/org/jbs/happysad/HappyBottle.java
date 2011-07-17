package org.jbs.happysad;

import android.content.ContentValues;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.UID;
/**
 * Happy Bottle is the object used to express 1 row in the database, or 1 update.
 * @author HappyTrack
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
	 * @param uid user ID of type long
	 * @param lati latitude coordinate of type int
	 * @param longi longitude coordinate of type int
	 * @param emo emotion value of type short
	 * @param msg message of type string
	 * @param time time stamp of type long
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
	 * Returns the longitude in type int
	 * @return the longitude in type int
	 */
	public int getLong(){
		return longi;
	}
	/**
	 * Returns the latitude in type int
	 * @return the latitude in type int
	 */
	public int getLat(){
		return lati;
	}
	/**
	 * Returns the emotion value of type short
	 * @return the emotion value of type short
	 */
	public short getEmo(){
		return emo;
	}
	/**
	 * Returns the message in type string
	 * @return the message in type string
	 */
	public String getMsg(){
		return msg;
	}
	/**
	 * Returns the time in type long
	 * @return the time in type long
	 */
	public long getTime(){
		return time;
	}
	/**
	 * Returns the user integer value in type long
	 * @return the user integer value in type long
	 */
	public long getUID(){
		return uid;
	}
}
