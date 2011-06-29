package org.jbs.happysad;

import static android.provider.BaseColumns._ID;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.SYNC;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.UID;


import java.util.ArrayList;
import org.jbs.happysad.HappyBottle;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

/**
 * HappyData is a helper class that serves as a layer of abstraction between you and the database. It deals with the logic of
 * using local or web storage so that you
 * @author HS
 */
public class HappyData {
	//for debugging purposes, delete when no longer needed
	private static final String TAG = "HappyData";
	
	//fields
	private HappyDB h;
	private static final long MyUserID = 1; //should not be hardcoded to 1
	private static String[] FROM = { _ID, UID, LAT, LONG, EMO, MSG, TIME, SYNC };
	private static String ORDER_BY = TIME + " DESC";
	private NetHelper net = new NetHelper(MyUserID);

	//private Handler mainThread;
	public HappyData(Context ctx){
		h = new HappyDB(ctx);
		//mainThread = new Handler();
	}
	
	/**
	 * Tries to add a bottle to the local db. Return true/false if it was successful or not.
	 * @param b
	 * @return Did our attempt to add the bottle succeed?
	 */
	public boolean addBottle(HappyBottle b){
		return addBottle(b, false);
	}
	
	public boolean addBottle(HappyBottle b, boolean isSynced){
		boolean toreturn = false;
		
		SQLiteDatabase db = h.getWritableDatabase();
		ContentValues values = b.getAll();
		int z = isSynced?1:0;
		values.put(SYNC, z);
		Log.e(TAG, "about to add bottle");
		try {
			db.insertOrThrow(TABLE_NAME, null, values);
			toreturn = true;	
		}
		catch(Exception e){
			Log.e(TAG, e.toString());
			toreturn = false;
		}
		finally{
			h.close();
		}	
		syncUp();
		return toreturn;
		
	}
	//for each entry that isn't synced, send to database.
	private void syncUp(){
		Log.w(TAG, "SYNCUP");
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		while (cursor.moveToNext() ){
			long id = cursor.getLong(1);
			int synced = cursor.getInt(7);
			if ((id == MyUserID) && (synced == 0)) {
				HappyBottle b = createBottle(cursor);
				Log.e(TAG, "NETSEND");
				net.send(b);
				Log.e(TAG, "REMOVEBYID");
				removeByID(cursor.getLong(0));
				addBottle(b, true);
			}
			
		} 
		cursor.close();
		db.close(); 
	}
	
	//temporary method for testing uses only.
	public void syncDown(){
		/*String j = " [   { user:8, lat:20, lon:48, emo:.1, msg:\"Shalom Salaam Peace\", t:333}  ,   { user:8, lat:211, lon:33, emo:.8, msg:\"Hadag Nachash\", t:339}  ] ";
		ArrayList<HappyBottle> b = net.parse(j);
		for (HappyBottle bottle: b){
			Log.e(TAG, "PARSED:" + bottle.toString());	
		}
		*/
		//so far so good.
		syncAllDown();
		//addAvoidDupes(b);
	
	}
	
	private void syncMyDown(){
		String j = net.doTask(Task.GETMINE);
		ArrayList<HappyBottle> b = net.parse(j);
		addAvoidDupes(b);
	}
	
	private void syncAllDown(){
		String j = net.doTask(Task.GETALL);
		ArrayList<HappyBottle> b = net.parse(j);
		addAvoidDupes(b);
	}
	private void removeByID(long id){
		Log.e(TAG, "REMOVEBYID STARTED");
		SQLiteDatabase db = h.getWritableDatabase();
		db.delete(TABLE_NAME, "_ID==" + id, null );
		
	}
	
	//addAvoidDupes = takes an arraylist of bottles. Adds them to the database but only if they are not dupes
	private void addAvoidDupes(ArrayList<HappyBottle> a){
		//for each bottle
		//if there is nothing with the same uid and time as the bottle in your localdb
		//add it to the db
		
		for (HappyBottle b : a){
			long t = b.getTime();
			long u = b.getUID();
			SQLiteDatabase db = h.getReadableDatabase();
			String[] columns = {_ID, MSG};
			Cursor c = db.query(TABLE_NAME, columns, UID+"=\'"+u+"\' AND "+TIME+"="+t, null, null, null, null);
			if (c.getCount() == 0){
				c.close();
				db.close();
				
				//ie if there are no rows in the local table that match the uid and time of the bottle
				Log.d(TAG, "adding bottle fromthe internets!");
				this.addBottle(b, true);
			}
			c.close();
			db.close();
		
		}
	
		
	}
	
	/**
	 * This method creates an ArrayList of HappyBottles that id == MyUserID
	 * @return the ArrayList of HappyBottles
	 */
	public ArrayList<HappyBottle> getMyHistory(){
		
		
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		ArrayList<HappyBottle> a = new ArrayList<HappyBottle>();
		while (cursor.moveToNext() ){
			long id = cursor.getLong(1);
			if (id == MyUserID) {
				//infinite loop here?????
				HappyBottle b = createBottle(cursor);
				a.add(b);
				}
		}
		cursor.close();
		db.close();
		return a;
	}
	
	//FOR HW W5L1
	public Cursor getMyHistoryCursor(){
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		
		return cursor;
	}
	
	/**
	 * This method creates an ArrayList of HappyBottles to create AllHistory
	 * @return an ArrayList of HappyBottles
	 */
	public ArrayList<HappyBottle> getAllHistory(){
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		ArrayList<HappyBottle> a = new ArrayList<HappyBottle>();
		while (cursor.moveToNext() ){
			HappyBottle b = createBottle(cursor);
			a.add(b);
		}
		cursor.close();
		db.close();
		return a;
	}
	
	/**
	 * Creates a Bottle
	 * @param cursor
	 * @return return the created bottle
	 */
	private HappyBottle createBottle(Cursor cursor){
		
		long uid = cursor.getLong(1);
		float latitude = cursor.getFloat(2);
		float longitude = cursor.getFloat(3);
		float emo = cursor.getFloat(4);
		String msg = cursor.getString(5);
		long time = cursor.getLong(6);
		
		HappyBottle b = new HappyBottle(uid, latitude, longitude, emo, msg, time);
		return b;
	}
	
	/**
	 * Provides random read-write access to the result set returned by a database query.
	 * @return Cursor object
	 */
	private Cursor getCursor(SQLiteDatabase db){
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
		           null, ORDER_BY);
		return cursor;
	}
	
}
