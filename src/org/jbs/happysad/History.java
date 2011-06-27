package org.jbs.happysad;

import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;

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


public class History extends ListActivity implements OnClickListener{
	

	private HappyData dataHelper;
	private static String[] FROM = { TIME, MSG, EMO,  };
	private static int[] TO = { R.id.time, R.id.msg, R.id.emo, };
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		

		View refreshButton = findViewById(R.id.refresh_button);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
		//ArrayList<HappyBottle> updates = getUpdates(); 
		//showUpdates(updates); 
	    showUpdatesCursor(dataHelper.getMyHistoryCursor());
	}




	public void onClick(View v) {
		Intent i = new Intent(this, History.class);

		switch(v.getId()) {		
		case R.id.refresh_button:	
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
	 * Creates setting menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.history, menu);
		return true;
	}

	/**
	 * Invoked when a option is clicked
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dashboard:
			startActivity(new Intent(this, Dashboard.class));
			return true;
		}
		return false;
	}
}
