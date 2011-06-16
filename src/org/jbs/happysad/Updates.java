package org.jbs.happysad;


import static android.provider.BaseColumns._ID;



import android.app.Activity;
import android.app.ListActivity;
//...
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;


public class Updates extends Activity{
	
	private static String[] FROM = { _ID, LAT, LONG, EMO, MSG, TIME, };
	private static String ORDER_BY = TIME + " DESC";
	private HappyData updates;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		updates = new HappyData(this);
		try {
	         Cursor cursor = getUpdates(); 
	         showUpdates(cursor); 
	      } finally {
	         updates.close(); 
	      }
	}
	
	private Cursor getUpdates(){
		// Perform a managed query. The Activity will handle closing
	    // and re-querying the cursor when needed.
		SQLiteDatabase db = updates.getReadableDatabase();
	    Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
	           null, ORDER_BY);
	    startManagingCursor(cursor);
	    return cursor;
	}
	
	private void addUpdate(int emo){
	//for basic updates	
		SQLiteDatabase db = updates.getWritableDatabase();
		ContentValues values = new ContentValues();
	    values.put(TIME, System.currentTimeMillis());
	    //values.put(LAT, <latitude>);
	    //values.put(LONG, <longitude>);
	    values.put(EMO, emo);
	    
	    db.insertOrThrow(TABLE_NAME, null, values);
	}
	
	private void showUpdates(Cursor cursor){
	 // Stuff them all into a big string
    	StringBuilder builder = new StringBuilder( 
          "Saved updates:\n");
	    while (cursor.moveToNext()) { 
	       // Could use getColumnIndexOrThrow() to get indexes
	       long id = cursor.getLong(0); 
	       long time = cursor.getLong(5);
	       long latitude = cursor.getLong(1);
	       long longitude = cursor.getLong(2);
	       long emo = cursor.getLong(3);
	       String msg = cursor.getString(4);
	       
	       builder.append(id).append(": "); 
	       builder.append(latitude).append(": ");
	       builder.append(longitude).append(": ");
	       builder.append(emo).append(": ");
	       builder.append(msg).append(": "); 
	       builder.append(time).append("\n");

	    }
	    // Display on the screen
	    TextView text = (TextView) findViewById(R.id.text); 
	    text.setText(builder);
 }
}
