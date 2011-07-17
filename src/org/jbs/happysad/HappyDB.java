package org.jbs.happysad;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.SYNC;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.UID;


/**
 * Creates the Database
 * @author HappyTrack
 *
 */
public class HappyDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "happy.db";

	private static final int DATABASE_VERSION = 4; //5

	private static final String TAG = "HappyDB"; 
	
	/**
	 * Constructor for the DB object
	 */
	public HappyDB(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * The first time this is created, create the table the app will forever use
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+ TABLE_NAME + 
			" (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			UID + " INTEGER, " + 
			LAT + " INTEGER, " +
			LONG + " INTEGER, " + 
			EMO + " REAL, " + 
			MSG + " TEXT, " + 
			TIME + " INTEGER," + 
			SYNC + " INTEGER);");
	}

	/**
	 * When you realize that you need a new table configuration,
	 * destroy the old table and create a new one.
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "upgrading");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	     onCreate(db);
	}	
	
	
}
