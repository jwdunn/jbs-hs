//new happydata

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
import org.jbs.happysad.HappyBottle;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * HappyData is a helper class that serves as a layer of abstraction between you and the database. It deals with the logic of
 * using local or web storage so that you
 * @author Sahar
 */
public class HappyData {
	//for debugging purposes, delete when no longer needed
	private static final String TAG = "HappyData";

	//fields
	private HappyDB h;
	private static final long MyUserID = 1; //this should not be hardcoded to 1
	private static String[] FROM = { _ID, UID, LAT, LONG, EMO, MSG, TIME, SYNC };
	private static String ORDER_BY = TIME + " DESC";
	private NetHelper net = new NetHelper(MyUserID);

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

	/**
	 * Tries to add a bottle to the local db. Return true/false if it was successful or not.
	 * @param b
	 * @param isSynced - Has this bottle been uploaded to the server?
	 * @return
	 */
	public synchronized boolean addBottle(HappyBottle b, boolean isSynced){
		boolean toreturn = false;
		//notice this is synchronized. Thread safety ftw. 
		//get the db
		SQLiteDatabase db = h.getWritableDatabase();
		ContentValues values = b.getAll();
		int z = isSynced?1:0;
		//since sync isn't in the contentvalues of bottles, we have to add it manually. 
		values.put(SYNC, z);
		Log.e(TAG, "about to add bottle");
		//now we use the handy insertOrThrow method to add a bottle. 
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
		return toreturn;
	}

	
	/**
	 *for each entry that isn't synced, send to database. 
	 */
	public void syncUp(){
		//note that this method is written BADLY. See below for details
		Log.w(TAG, "SYNCUP");
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		//cursor is an easy way to iterate through a database
		while (cursor.moveToNext() ){
			long id = cursor.getLong(1);
			int synced = cursor.getInt(7);
			//now we check each row in the database to see if it's been synced yet.
			if ((id == MyUserID) && (synced == 0)) {
				//if we hit a bottle that has not been synced, and is ours, we upload it.
				HappyBottle b = createBottle(cursor);
				Log.e(TAG, "NETSEND");
				net.doTask(Task.SEND, b);
				Log.e(TAG, "REMOVEBYID");
				//after we upload it, we remove it!
				removeByID(cursor.getLong(0));
				//and then we add an identical bottle with synced set to true
				addBottle(b, true);
				//we do two HORRIBLY WRONG things here.
				//a. instead of deleting a row in the db only to insert an almost-identical one, we should just alter the row in the db instead.
				//b. instead of looping through the cursor, we should make the appropriate call and let the server find the matching rows for us.
			}

		} 
		cursor.close();
		db.close(); 
	}

	//temporary method for testing uses only.
	//syncs down everything.
	public void syncDown(){
		//syncMyDown();
		syncAllDown();
		Log.d(TAG, "synced down");

	}

	//same as syncAllDown, but just for my bottles
	private void syncMyDown(){
		ArrayList<HappyBottle> b = net.doTask(Task.GETMINE, null);
		addAvoidDupes(b);
	}

	//use nethelper to sync all down
	//then add the bottles to the database, avoiding duplicates of course.
	private void syncAllDown(){
		ArrayList<HappyBottle> b = net.doTask(Task.GETALL, null);
		addAvoidDupes(b);
	}

	//given the id of a row in the local database of a bottle, delete it.
	//NOTE that this id is very different from other ids. It's not the user id, it's not the row id in the server. 
	private void removeByID(long id){
		Log.e(TAG, "REMOVEBYID STARTED");
		SQLiteDatabase db = h.getWritableDatabase();
		db.delete(TABLE_NAME, "_ID==" + id, null );
		db.close();
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

				//ie if there are no rows in the local table that match the uid and time of the bottle
				Log.d(TAG, "adding bottle fromthe internets!");
				this.addBottle(b, true); //was true
			}
			c.close();
			db.close();

		}


	}

	/**
	 * This method creates an ArrayList of HappyBottles that id == MyUserID
	 * Calls the local database. Doesn't call the server.
	 * @return the ArrayList of HappyBottles
	 */
	public ArrayList<HappyBottle> getMyHistory(){
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor cursor = getCursor(db);
		ArrayList<HappyBottle> a = new ArrayList<HappyBottle>();
		//call the database 
		while (cursor.moveToNext() ){
			long id = cursor.getLong(1);
			if (id == MyUserID) {
				//for each entry in the database, where the user id == MyUserID
				HappyBottle b = createBottle(cursor);
				//add the corresponding bottle to the temporary arraylist of bottles to be returned.
				a.add(b);
		}}
		cursor.close();
		db.close();
		return a;
	}



	/**
	 * This method creates an ArrayList of HappyBottles to create AllHistory
	 * Calls the local database. Doesn't call the server.
	 * @return an ArrayList of HappyBottles
	 */
	public ArrayList<HappyBottle> getAllHistory(){
		//see getMyHistory. Same idea.
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
		//given cursor that is pointing to a row in the database, pull the info from that row and create a bottle object.
		//we are "inflating" rows into objects. 
		long uid = cursor.getLong(1);
		int latitude = cursor.getInt(2);
		int longitude = cursor.getInt(3);
		short emo = cursor.getShort(4);
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
