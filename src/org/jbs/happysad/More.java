package org.jbs.happysad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class More extends Activity implements OnKeyListener, OnClickListener {
	private static final String TAG = "there's more screen";
	String extradata;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "entering oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);

		Intent sender = getIntent();
		extradata = sender.getExtras().getString("Clicked");

		TextView t = (TextView) findViewById(R.id.more_text);
		t.setText(extradata);

		EditText textField = (EditText) findViewById(R.id.more_textbox);
		textField.setOnKeyListener(this);

		TextView locationView = (TextView) findViewById(R.id.location);
		locationView.setText("unknown");

		View submitButton = findViewById(R.id.more_to_dash);
		submitButton.setOnClickListener(this);

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		Log.d(TAG, "creating a new location listner");
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		Log.d(TAG, "Registration  of listener");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
		
		//SAHAR STORE FROM HERE!!
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		makeUseOfNewLocation(location);
		
		// Remove the listener you previously added
		locationManager.removeUpdates(locationListener);
	}

	private void makeUseOfNewLocation(Location location) {
		Log.d(TAG, "entering makeuseofnewlocatoin");
		int x = 0;
		System.out.println(x);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		TextView locationView = (TextView) findViewById(R.id.location);
		locationView.setText("unknown");
		locationView.setText("lat = " + latitude + " long = " + longitude);
		locationView.invalidate();
	}

	public void onClick(View v) {
		Log.d(TAG, "clicked" + v.getId());
		System.out.println(TAG + "clicked" + v.getId());
		switch (v.getId()) {
		case R.id.more_to_dash:
			Intent i = new Intent(this, Dashboard.class);

			String userstring = ((TextView) findViewById(R.id.more_textbox))
					.getText().toString();

			i.putExtra("textboxmessage", userstring);
			i.putExtra("happysaddata", extradata);
			Log.d(TAG, "adding " + userstring + " to intent");
			startActivity(i);
			break;
		}
	}

	// got following code from
	// http://stackoverflow.com/questions/2004344/android-edittext-imeoptions-done-track-finish-typing
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
			// Done pressed! Do something here.
			EditText t = (EditText) v;
			Log.d(TAG, "text entered: " + t.getText());
			this.onClick(findViewById(R.id.more_to_dash));
			// Intent i = new Intent(this, prompt.class);
			// startActivity(i);

		}
		// Returning false allows other listeners to react to the press.
		return false;
	}

}