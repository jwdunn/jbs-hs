package org.jbs.happysad;


import static android.provider.BaseColumns._ID;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.SYNC;
import static org.jbs.happysad.Constants.TIME;
import static org.jbs.happysad.Constants.UID;

import java.util.ArrayList;



import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class History extends ListActivity implements OnClickListener{
	
	private HappyData dataHelper;
	//private static String[] FROM = { _ID, UID, LAT, LONG, EMO, MSG, TIME, SYNC };
	private static String[] FROM = { TIME, MSG, EMO,  };
	private static int[] TO = { R.id.time, R.id.msg, R.id.emo, };
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history2);
		
		View refreshButton = findViewById(R.id.refresh);
    	refreshButton.setOnClickListener(this);
		
		dataHelper = new HappyData(this);
		//ArrayList<HappyBottle> updates = getUpdates(); 
		//showUpdates(updates); 
	    showUpdatesCursor(dataHelper.getMyHistoryCursor());
	}
	
	
	
	
	public void onClick(View v) {
		Intent i = new Intent(this, History.class);

		switch(v.getId()) {		
		case R.id.refresh:	
			dataHelper.syncDown();
			startActivity(i);
			break;
			
		}}
	
	private void showUpdatesCursor(Cursor cursor){
		startManagingCursor(cursor);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, FROM, TO);
		setListAdapter(adapter);
		
	}
	
	
	
	
	/**
	 * Returns an ArrayList of HappyBottles of MyHistory
	 * @return
	 */
	private ArrayList<HappyBottle> getUpdates(){
		return dataHelper.getMyHistory();
	}
	
	/**
	 * Shows the ArrayList of HappyBottles on the Screen via a big string
	 * @param a
	 */
	private void showUpdates(ArrayList<HappyBottle> a){
		// Stuff them all into a big string
    	StringBuilder builder = new StringBuilder( 
          "Saved updates:\n");
	    for (HappyBottle b : a) { 
	       // Could use getColumnIndexOrThrow() to get indexes
	       builder.append(b.toString());
	       builder.append("\n");

	    }
	    // Display on the screen
	    TextView text = (TextView) findViewById(R.id.text); 
	    text.setText(builder);
	}
	
	/**
	 * Creates setting menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	/**
	 * Invoked when a option is clicked
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
			// More items go here (if any) ...
		}
	return false;
	}
}
