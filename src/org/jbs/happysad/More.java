package org.jbs.happysad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
//import android.util.Log;

/**
 * Creates the More activity
 * @author HS
 */
public class More extends Activity implements OnKeyListener, OnClickListener {
	//for debugging purposes, delete after debugging.
	//private static final String TAG = "there's more screen";
	
	//fields
	private float latitude = 5;
	private float longitude = 5;
	private HappyData dataHelper;
	int emotion = -1;
	String extradata;
	long myID = 1;

	/**
	 * Initializes activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
		//Intent to figure out whether they clicked happy or sad from Prompt.java
		Intent sender = getIntent();
		extradata = sender.getExtras().getString("Clicked");
		emotion = sender.getExtras().getInt("Emotion");
		
		//Finds the more_textbox view
		EditText textField = (EditText)findViewById(R.id.more_textbox);
		textField.setOnKeyListener(this);
		
		//Finds the more_text view
		TextView t = (TextView) findViewById(R.id.more_text);
		t.append(extradata);
		
		//Finds the update_button view
		View submitButton = findViewById(R.id.more_to_dash);
		submitButton.setOnClickListener(this);
		
		//Finds the location view
		TextView locationView = (TextView) findViewById(R.id.location);
		locationView.setText("unknown");
		locationStuff();	
	}
	
	/**
     * Invoked when a view is clicked
     */
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.more_to_dash:
			Intent i = new Intent(this, Dashboard.class);
			String userstring = ((TextView) findViewById(R.id.more_textbox)).getText().toString();
			saveUpdate(userstring); 			    
			startActivity(i);
			break;
		}
	}
	
	/**
	 * Called when a key is dispatched to a view.
	 */
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
			// Done pressed! Do something here.
			this.onClick(findViewById(R.id.more_to_dash));
			
		}
		// Returning false allows other listeners to react to the press.
		return false;
	}
	
	/**
	 * Helper method to deal with location.
	 */
	private void locationStuff(){
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				makeUseOfNewLocation(location);}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
				
		try {
			Location location = new Location(LocationManager.GPS_PROVIDER);
			longitude = (float) location.getLongitude();
			latitude =  (float) location.getLatitude();
			makeUseOfNewLocation(location);
		}
		catch (Exception e){
		// Remove the listener you previously added
		}
		
		locationManager.removeUpdates(locationListener);
	}
		
	
	
	/**
	 * Sets up the textview to show your lat/long
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location) {		
		//redundant V
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		TextView locationView = (TextView) findViewById(R.id.location);
		locationView.setText("unknown");
		locationView.setText("lat = " + latitude + " long = " + longitude);
		locationView.invalidate();
	}
	
	/**
	 * Saves the update as a bottle and adds teh bottle to the DB
	 * @param msg
	 */
	private void saveUpdate(String msg){
		
		HappyBottle b = new HappyBottle(myID, latitude, longitude, emotion, msg, System.currentTimeMillis());
		dataHelper = new HappyData(this);
		dataHelper.addBottle(b);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
		
	@Override
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
