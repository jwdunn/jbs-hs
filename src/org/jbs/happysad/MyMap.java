package org.jbs.happysad;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import android.widget.Button;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Creates a My Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class MyMap extends MapActivity implements OnClickListener {
	//fields
	private MapView map; 
	int checkHappy;
	int checkSad;
	boolean run;
	boolean check;
	boolean streetView;
	MyLocationOverlay userLocationOverlay;
	private MapController controller;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay; 
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		checkHappy = getIntent().getExtras().getInt("Happy");
		checkSad = getIntent().getExtras().getInt("Sad");
		run = getIntent().getExtras().getBoolean("Run");
		streetView = getIntent().getExtras().getBoolean("Sreet");
		
		//Defines the drawable items for the happy and sad overlays
		Drawable happyface = this.getResources().getDrawable(R.drawable.pinhappy);
		Drawable sadface = this.getResources().getDrawable(R.drawable.pinsad);
		
		//initializes the happy and sad overlays
		happyOverlay = new ItemizedEmotionOverlay(happyface, this);	
		sadOverlay = new ItemizedEmotionOverlay(sadface, this);
		
		//instantiates HappyData and creates an arraylist of all the bottles
		HappyData datahelper = new HappyData(this);
		ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
		
		//adds items to overlays
		emotionOverlayFiller(1,plottables,happyOverlay);
		emotionOverlayFiller(0,plottables,sadOverlay);
		
		//initialize and display map view and user location
		initMapView();
		goToMyLocation();
		
		if (streetView ) {
			map.setStreetView(true);
			map.setSatellite(false);
		} else {
			map.setSatellite(true);
			map.setStreetView(false);
		}
		
		//Finds the show_sad view
		View sadButton = findViewById(R.id.showSad);
		sadButton.setOnClickListener(this);
		
		//Finds the show_happy view
		View happyButton = findViewById(R.id.showHappy);
		happyButton.setOnClickListener(this); 
		
		//Finds the switch_view
		View switchButton = findViewById(R.id.switchView);
		switchButton.setOnClickListener(this); 
		
		//Finds the history_button view
		View histButton = findViewById(R.id.myTrack_button);
		histButton.setOnClickListener(this);  	
		
		//Finds the history_button view
		View chartButton = findViewById(R.id.myChart_button);
		chartButton.setOnClickListener(this);
		
		//Finds the my_map view
		View myButton = findViewById(R.id.map);
		((Button) myButton).setText("GlobalMap");
		myButton.setOnClickListener(this);
	}

	/**
	 * Invoked when a view is clicked
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		//checks what current view is, then switches it off and starts the alternate view
		case R.id.switchView:
			if (streetView==false) {
				map.setStreetView(true);
				streetView = true;
				map.setSatellite(false);  
			} else{
				map.setStreetView(false);
				streetView = false;
				map.setSatellite(true);
			}
			break;
		
		//used to show/hide the happy faces
		case R.id.showHappy:
			if (checkHappy==0){ 
				map.getOverlays().add(happyOverlay); //adds happy face overlay to visible overlays 
				checkHappy = 1; 
			} else{ 
				map.getOverlays().clear(); //clears all overlays
				if (checkSad == 1){
					map.getOverlays().add(sadOverlay);  //if sad faces should be visible, it adds them back
				}
				checkHappy = 0; 
			}
			invalidateOverlay(); //method call
			break;
		
		//used to show/hide the sad faces
		case R.id.showSad:		
			if (checkSad == 0){
				map.getOverlays().add(sadOverlay); //adds sad face overlay to visible overlays
				checkSad = 1; 
			} else{
					map.getOverlays().clear(); //clears all overlays
				if (checkHappy==1) {
					map.getOverlays().add(happyOverlay); //if happy faces should be visible, it adds them back
				}
				checkSad = 0;
			}
			invalidateOverlay(); //method call
			break;
		
		case R.id.map:
			Intent j = new Intent(this, GlobalMap.class);
			j.putExtra("Run", false);
			j.putExtra("Happy", checkHappy);
			j.putExtra("Sad", checkSad);
			startActivity(j);
			finish();
			break;
		
		case R.id.myTrack_button:
			startActivity(new Intent(this, History.class));
			break;
		
		case R.id.myChart_button:
			startActivity(new Intent(this, ChartList.class));
			break;
		}
		map.invalidate();

	}
	
	//helper method for showHappy and showSad onClick cases
	private void invalidateOverlay() {
		map.getOverlays().add(userLocationOverlay);
	}
	
	//Finds and initializes the map view.
	private void initMapView() {
		map = (MapView) findViewById(R.id.themap);
		controller = map.getController();
		//adds the sad and happy overlays to the map
		if (checkSad == 1)
			map.getOverlays().add(sadOverlay);
		if (checkHappy == 1) 
			map.getOverlays().add(happyOverlay);
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
	}
	
	private void goToMyLocation() {
		if (run == true) {
			userLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					// Zoom in to current location
					controller.setZoom(15);
					controller.animateTo(userLocationOverlay.getMyLocation());
				}
			});
		}
		map.getOverlays().add(userLocationOverlay);
	}
	
	
	//creates an emotion overlay
	private static void emotionOverlayFiller(int emotion, ArrayList<HappyBottle> plottables, ItemizedEmotionOverlay itemizedoverlay){ 
		for(int i = 0; i<plottables.size();i++) {
			HappyBottle element = plottables.get(i);
			if (element.getEmo()==emotion) { //happy or sad filter
				int latitude =  element.getLat();
				int longitude = element.getLong();
				GeoPoint point = new GeoPoint(latitude,longitude);
				String S = (String) new Timestamp(element.getTime()).toLocaleString();
				itemizedoverlay.addToOverlay(new OverlayItem(point, S+emotion, element.getMsg()));
			}
		}
	}
	
	//Required method to make the map work
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.current_location:
	    	run = true;
	        goToMyLocation();
	        return true;
	    case R.id.new_update:
	    	startActivity(new Intent(this, Prompt.class));
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	//Disables MyLocation
	@Override
	protected void onPause() {
		super.onPause();
		userLocationOverlay.disableMyLocation();  
	}
	
	//Enables MyLocation
	@Override
	protected void onResume() {
		super.onResume();
		userLocationOverlay.enableMyLocation();
	}
}