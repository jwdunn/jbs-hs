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
	private float GPS_latitude;
	private float GPS_longitude;
	private float Network_latitude;
	private float Network_longitude;
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
		
		//Updates location
		locationStuff();			
		
		//Finds the more_text view
		//TextView t = (TextView) findViewById(R.id.more_text);
		//t.append(extradata);
		
		//Finds the update_button view
		View submitButton = findViewById(R.id.more_to_dash);
		submitButton.setOnClickListener(this);
		
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
		LocationManager GPSlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationManager NetworklocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Define a GPS listener that responds to location updates
		LocationListener GPSlocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				//updateNetworkLocation(location);
				makeUseOfNewLocation(location);}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Define a Network listener that responds to location updates
		LocationListener networkLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				updateNetworkLocation(location);
				//makeUseOfNewLocation(location);
				}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};
		
		//registers the location managers
		GPSlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, GPSlocationListener);
		NetworklocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,	0, networkLocationListener);
		
		try {
			Location locationGPS = new Location(LocationManager.GPS_PROVIDER);
			Location locationNetwork = new Location(LocationManager.NETWORK_PROVIDER);			
			makeUseOfNewLocation(locationGPS);
			updateNetworkLocation(locationNetwork);
		
		}
		catch (Exception e){
		}
		
	}
		
	
	
	/**
	 * Updates GPS location
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location) {		
		
		if(GPS_longitude == 0 && GPS_latitude == 0){
			GPS_longitude = (float) location.getLongitude();
			GPS_latitude = (float) location.getLatitude();
		}
		
	}
	
	/**
	 * Updates Network location
	 * @param location
	 */
	private void updateNetworkLocation(Location location) {
		if(Network_longitude == 0 && Network_latitude == 0){
			Network_longitude = (float) location.getLongitude();
			Network_latitude = (float) location.getLatitude();
		}
	}
	
	
	/**
	 * Saves the update as a bottle and adds the bottle to the DB
	 * @param msg
	 */
	private void saveUpdate(String msg){
		
		HappyBottle b = new HappyBottle(myID, GPS_latitude, GPS_longitude, Network_latitude, Network_longitude, emotion, msg, System.currentTimeMillis());
		dataHelper = new HappyData(this);
		dataHelper.addBottle(b);
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
