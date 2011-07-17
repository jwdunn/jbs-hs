package org.jbs.happysad;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class GlobalMap extends MapActivity implements OnClickListener {
	//fields
	private MapView map;
	private MapController controller;
	int checkHappy = 1;
	int checkSad = 1;
	MyLocationOverlay userLocationOverlay;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay;
	HappyData datahelper = new HappyData(this);
	ArrayList<HappyBottle> plottables;
	int zoomLevel = -1;
	GeoPoint center = new GeoPoint(0,0);
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		//Defines the drawable items for the happy and sad overlays
		Drawable happyface = this.getResources().getDrawable(R.drawable.pinhappy);
		Drawable sadface = this.getResources().getDrawable(R.drawable.pinsad);
		
		//initializes the happy and sad overlays
		happyOverlay = new ItemizedEmotionOverlay(happyface, this);	
		sadOverlay = new ItemizedEmotionOverlay(sadface, this);
		
		//initialize and display map view and user location
		initMapView();
		initMyLocation();	
		//updateToView();
		
		//adds items to overlays
		//emotionOverlayFiller(1,plottables,happyOverlay);
		//emotionOverlayFiller(0,plottables,sadOverlay);
		
		//Finds the show_sad view
		View sadButton = findViewById(R.id.showSad);
		sadButton.setOnClickListener(this);
		
		//Finds the show_happy view
		View happyButton = findViewById(R.id.showHappy);
		happyButton.setOnClickListener(this); 
		
		//Finds the switch_view
		View switchButton = findViewById(R.id.switchView);
		switchButton.setOnClickListener(this); 
		
		//Finds the chart_button view
		View chartButton = findViewById(R.id.myTrack_button);
		chartButton.setOnClickListener(this);  	
		
		//Finds the history_button view
		View histButton = findViewById(R.id.myChart_button);
		histButton.setOnClickListener(this);
		
		//Finds the my_map view
		View myButton = findViewById(R.id.map);
		((Button) myButton).setText("MyMap");
		myButton.setOnClickListener(this);
	}

	/**
	 * Invoked when a view is clicked
	 */
	@Override
	public void onClick(View v) {
		
		//updateToView();
		//adds items to overlays
		//emotionOverlayFiller(1,plottables,happyOverlay);
		//emotionOverlayFiller(0,plottables,sadOverlay);
		
		switch(v.getId()){
		
		//checks what current view is, then switches it off and starts the alternate view
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
				newOverlay(); //method call
			}
			break;
		
		//used to show/hide the sad faces
		case R.id.showSad:		
			if (checkSad == 0){
				map.getOverlays().add(sadOverlay); //adds sad face overlay to visible overlays
				checkSad = 1; 
				} else{
					map.getOverlays().clear(); //clears all overlays
				if (this.checkHappy==1){
					map.getOverlays().add(happyOverlay); //if happy faces should be visible, it adds them back
				}
				checkSad = 0;
				newOverlay(); //method call
			}
			break;
		
		case R.id.map:
			startActivity(new Intent(this, MyMap.class));
			break;
		
		case R.id.myTrack_button:
			startActivity(new Intent(this, History.class));
			break;
		}
		map.invalidate();
	}
	
	//helper method for showHappy and showSad onClick cases
	private void newOverlay() {
		map.getOverlays().add(userLocationOverlay);
	}
	
	//Finds and initializes the map view.
	private void initMapView() {
		map = (MapView) findViewById(R.id.themap);
		controller = map.getController();
		map.setStreetView(true); //sets default view to street view
		//adds the sad and happy overlays to the map
		map.getOverlays().add(sadOverlay);
		map.getOverlays().add(happyOverlay);
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons
	}
	
	//Starts tracking the users position on the map. 
	private void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
		userLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				// Zoom in to current location
				controller.animateTo(userLocationOverlay.getMyLocation());
				controller.setZoom(15); //sets the map zoom level to 15
			}
		});
		map.getOverlays().add(userLocationOverlay); //adds the users location overlay to the overlays being displayed
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
	
	/**
	 * This method updates the overlays for only the current the current view
	 */
	private void updateToView(){
		//Log.w("updateToView", "ERROR in new method");
		GeoPoint center = map.getMapCenter(); //gets coordinates for map view's center
		int centerLat = center.getLatitudeE6(); //finds center's latitude
		int centerLong = center.getLongitudeE6(); //finds center's longitude
		int width = map.getLongitudeSpan(); //gets width of view in terms of longitudes shown on screen
		int height = map.getLatitudeSpan(); //gets height of view in terms of latitudes shown on screen
		int minLong = centerLong-width/2; //gets the left most longitude shown
		int maxLong = centerLong+width/2; //gets the right most longitude shown
		int maxLat = centerLat+height/2; //gets the top most latitude shown
		int minLat = centerLat-height/2; //gets the bottom most latitude shown
		plottables = datahelper.getLocalRecent(minLat,maxLat,minLong,maxLong,100);
	}

	//to sync and update bottles with mapview
	private void updater(GeoPoint center, int zoomLevel){
		if(!(map.getMapCenter() == center && map.getZoomLevel()==zoomLevel)){
			this.center = map.getMapCenter();
			this.zoomLevel = map.getZoomLevel();
			updateToView();
		}
	}
	
	class Async extends AsyncTask <MapView, Integer, Boolean>{
		//standard methods you have to implement to use AsyncTask
		protected void onProgressUpdate(Integer... progress) {
			//logo.log("async.onProgressUpdate");
		}
		//standard methods you have to implement to use AsyncTask
		protected void onPostExecute(Long result) {
			//logo.log("async.onProgressUpdate");
		}
		//this is the method that runs in background and will be very useful
		@Override
		protected Boolean doInBackground(MapView... params) {
			//I defined a max bound for cycle time.... every cycle must have a way out condition!
			int maxcount = 100;
			//It takes maps from parameters
			MapView mappa = params[0];
			//every time it runs
			for (int i = 0; i < maxcount; i++) {
				//testing about getLatitudeSpan and getLongitudeSpan fake values
				if (mappa.getLatitudeSpan() == 0 & mappa.getLongitudeSpan() == 360000000)
				{
					try
					{
						// fake values, thread will sleep awhile!
						Thread.sleep(80);
					}
					catch (InterruptedException ex)
					{
						Log.e("async.doInBackground. " + ex.getMessage(), null);
					}
				}
				else //good values, we can use them 
				{
					updater(center,zoomLevel);
					happyOverlay.emptyOverlay();
					sadOverlay.emptyOverlay();
					emotionOverlayFiller(1,plottables,happyOverlay);
					emotionOverlayFiller(0,plottables,sadOverlay);
					return true;
				}
			}
			return false;
		}
	}
	
	//Required method to make the map work
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
		Async sync = new Async();
		sync.execute(map);
	}
}