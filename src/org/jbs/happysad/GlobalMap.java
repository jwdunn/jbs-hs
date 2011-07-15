package org.jbs.happysad;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappySad
 */
public class GlobalMap extends MapActivity implements OnClickListener {
	//fields
	private MapView map; //setting view
	private MapController controller; //setting pinch to zoom
	int checkHappy = 1; //check digits to keep track of whether happy faces are being shown on map - standard binary
	int checkSad = 1; //check digit to keep track of whether sad faces are being shown on map - standard binary
	MyLocationOverlay userLocationOverlay; //an overlay that marks users position on the map
	ItemizedEmotionOverlay happyOverlay; //an overlay that marks all the happy faces
	ItemizedEmotionOverlay sadOverlay; //an overlay that marks all the sad faces
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.globalmap); //sets the view
		
		//Defines the drawable items for the happy and sad overlays
		Drawable happyface = this.getResources().getDrawable(R.drawable.pinhappy);
		Drawable sadface = this.getResources().getDrawable(R.drawable.pinsad);
		
		//initializes the happy and sad overlays
		happyOverlay = new ItemizedEmotionOverlay(happyface, this);	
		sadOverlay = new ItemizedEmotionOverlay(sadface, this);
		
		//get all HappyBottles from HappyData
		HappyData datahelper = new HappyData(this); //instantiates HappyData to access local storage
		ArrayList<HappyBottle> plottables = datahelper.getAllHistory(); //creates an arraylist of all the bottles
		
		
		//adds items to overlays
		//1 is for sad and 0 is for happy, according to the HappyTrack system
		emotionOverlayFiller(1,plottables,happyOverlay); //adds all happy bottles to the happy overlay
		emotionOverlayFiller(0,plottables,sadOverlay); //adds all sad bottles to the sad overlay
			
		//initialize and display map view and user location
		initMapView();
		initMyLocation();
      
		//Add ClickListener for the button
		View sadButton = findViewById(R.id.showSad);
		sadButton.setOnClickListener(this);
      
		// Add ClickListener for the button
		View happyButton = findViewById(R.id.showHappy);
		happyButton.setOnClickListener(this); 
      
		// Add ClickListener for the button
		View switchButton = findViewById(R.id.switchView);
		switchButton.setOnClickListener(this); 
		
		
		//Finds the chart_button view
  	  	View chartButton = findViewById(R.id.myTrack_button);
  	  	chartButton.setOnClickListener(this);  	
  	  	
  	  	//Finds the history_button view
  	  	View histButton = findViewById(R.id.myChart_button);
  	  	histButton.setOnClickListener(this);
  	  	
  	  	//Finds the my_map view
  	  	View myButton = findViewById(R.id.myMap);
  	  	myButton.setOnClickListener(this);
	}
   
    /**
     * Invoked when a view is clicked
     */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		//checks what current view is, then switches it off and starts the alternate
		case R.id.switchView:
			if (map.isStreetView()==false){
				map.setStreetView(true);
				map.setSatellite(false);  
			} else{
				map.setStreetView(false);
				map.setSatellite(true);
			}
			break;
		
		//used to show/hide the happy faces
		//checks the check digit to note whether the user wants to show or hide the happy faces
		//acts accordingly and updates the check digit
		case R.id.showHappy:
			if (checkHappy==0){ //checks if happy faces are visible, goes here if not visible
				map.getOverlays().add(happyOverlay); //adds happy face overlay to visible overlays 
				checkHappy = 1; //updates the check digit
			} else{ //goes here if happy faces are already visible
				map.getOverlays().clear(); //clears all overlays
				if (checkSad == 1){
					map.getOverlays().add(sadOverlay);  //if sad faces should be visible, it adds them back
				}
				checkHappy = 0; //updates the check digit
			}
			map.getOverlays().add(userLocationOverlay); //adds the users current location overlay back to the map
			map.invalidate(); //redraws the map with the new overlay settings
			break;
			
		//used to show/hide the sad faces
		case R.id.showSad:		
			if (checkSad == 0){ //checks if sad faces are visible, goes here if not visible
				map.getOverlays().add(sadOverlay); //adds sad face overlay to visible overlays
				checkSad = 1; //updates the check digit        	   
			} else{ //goes here if sad faces are already visible
				map.getOverlays().clear(); //clears all overlays
				if (this.checkHappy==1){
					map.getOverlays().add(happyOverlay); //if happy faces should be visible, it adds them back
				}
				checkSad = 0; //updates the check digit
			}
			map.getOverlays().add(userLocationOverlay); //adds the users current location overlay back to the map
			map.invalidate(); //redraws the map with the new overlay settings
			break;
			
		case R.id.myMap:
			startActivity(new Intent(this, MyMap.class));
			break;
		
		}
	}
	
	//Finds and initializes the map view.
	private void initMapView() {
		map = (MapView) findViewById(R.id.map); //sets map view from xml
		controller = map.getController(); //gets pinch to zoom controller for map
		map.setStreetView(true); //sets default view to screen view
		map.getOverlays().add(sadOverlay); //adds the sad faces to the map
		map.getOverlays().add(happyOverlay); //adds the happy faces to the map
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons
	}
	
	//Starts tracking the users position on the map. 
	private void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map); //creates an overlay with the users current location
		userLocationOverlay.enableMyLocation(); //enables location detection
		userLocationOverlay.runOnFirstFix(new Runnable() { //the statements within should only be run when the map is first loaded
			public void run() {
				// Zoom in to current location
				controller.animateTo(userLocationOverlay.getMyLocation()); //sets the view to centralize the user
				controller.setZoom(15); //sets the map zoom level to 15
			}
		});
		map.getOverlays().add(userLocationOverlay); //adds the users location overlay to the overlays being displayed
	}
	
	/*Given a filter, a list of items and an overlay, it filters the items and then adds the filtrate to the overlay
	 * The filter is happy or sad (1 or 0)
	 * The list is a list of emotion bottles
	 * The overlay may be an overlay of happy or sad faces
	 */ 
	private static void emotionOverlayFiller(int emotion, ArrayList<HappyBottle> plottables, ItemizedEmotionOverlay itemizedoverlay){ 
		for(int i = 0; i<plottables.size();i++) { //loops for every bottle in the list
			HappyBottle element = plottables.get(i); //sets the element to the current bottle for easy reference
			if (element.getEmo()==emotion){ //enters only id the bottle passes the filter
				int latitude =  (int) (element.getLat()*1E6); //converts latitude from float to integer in microdegrees
				int longitude =  (int) (element.getLong()*1E6); //converts longitude from float to integer in microdegrees
				GeoPoint point = new GeoPoint(latitude,longitude); //creates geopoint (a type of point required for map overlays)
				String S = (String) new Timestamp(element.getTime()).toLocaleString(); //creates a string of bottle time that is human readable
				itemizedoverlay.addToOverlay(new OverlayItem(point, S+emotion, element.getMsg())); //adds the bottle to the overlay	- emotion is appended to date in the title	        
			}
		}   
	}
	
	//Required method for mapView
	protected boolean isRouteDisplayed() {
		return false;
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

