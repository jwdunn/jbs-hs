package org.jbs.happysad;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.util.Log;


public class Dashboard extends MapActivity {//implements OnClickListener{
	private static final String TAG = "dashboard";
	private MapView map;
	private MapController controller;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
      super.onCreate(savedInstanceState);
      Log.d(TAG, "created");
      setContentView(R.layout.dashboard);
      
      initMapView();
      initMyLocation();
      
      /**View updateButton = findViewById(R.id.update_button);
  	  updateButton.setOnClickListener(this);
  	  
  	  View histButton = findViewById(R.id.history_button);
 	  histButton.setOnClickListener(this);
 	 
  	  Intent sender = getIntent();
  	  TextView t = (TextView)findViewById(R.id.welcome_title);
  	  
  	  String extradata = "\nwelcome!";
  	  

  	  try {
  	  	Log.d(TAG, "getting data from previous intent: bundle");
  	  	Bundle b = sender.getExtras();
  	  	Log.d(TAG, "getting data from previous intent: extradata");
  	  	Log.d(TAG, b.getString("textboxmessage"));
  	  	extradata = b.getString("textboxmessage");
  	  	String happysaddata = b.getString("promptmessage");
  	  	
  	  	
  	  }
  	  catch (Exception e) {
  	  		//do nothing
  	  		Log.d(TAG, "no worries - the first time you run this activity of course you will have no extra data.");
  	  		//no worries - the first time you run this activity of course you will have no extra data.
  	  		Log.d(TAG, e.toString());
  	  }
  	  finally{
  	  	t.append("\n"+ extradata);
  	  }
 	*/
    }
      
      /**
      public void onClick(View v) {
		
			Log.d(TAG, "clicked" + v.getId());
			System.out.println(TAG + "clicked" + v.getId());
			switch(v.getId()) {
			
			case R.id.update_button:
				Log.d(TAG, "case" + v.getId()); 
				Intent i = new Intent(this, Prompt.class);
				i.putExtra("Clicked", "Happy");
				startActivity(i);
				break;
			case R.id.history_button:
				Log.d(TAG, "case" + v.getId());
				Intent j = new Intent(this, Updates.class);
				startActivity(j);
				break;
			}
			
      }
      */
      
      /** Find and initialize the map view. */
      private void initMapView() {
          map = (MapView) findViewById(R.id.map);
          controller = map.getController();
          map.setSatellite(true);
          map.setBuiltInZoomControls(true);
       }
       

       
       /** Start tracking the position on the map. */
       private void initMyLocation() {
          final MyLocationOverlay overlay = new MyLocationOverlay(this, map);
          overlay.enableMyLocation();
          //overlay.enableCompass(); // does not work in emulator
          overlay.runOnFirstFix(new Runnable() {
             public void run() {
                // Zoom in to current location
                controller.setZoom(8);
                controller.animateTo(overlay.getMyLocation());
             }
          });
          map.getOverlays().add(overlay);
       }
       	

       
       @Override
       protected boolean isRouteDisplayed() {
          // Required by MapActivity
          return false;
       }
}


