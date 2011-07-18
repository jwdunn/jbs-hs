//my failed java part 2 (sunday)

package org.jbs.happysad;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class GlobalMap extends MapActivity implements OnClickListener {
	//fields
	private static final String TAG = "GlobalMap";
	private MapView map;
	private MapController controller;
	int checkHappy = 1;
	int checkSad = 1;
	MyLocationOverlay userLocationOverlay;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay;
	HappyData datahelper = new HappyData(this);
	//	private volatile ArrayList<HappyBottle>  plottables;
	int zoomLevel;
	GeoPoint center;
	private Handler handler;
	Runnable latestThread;
	ZoomPanListener zpl;

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

		center = new GeoPoint(0,0);
		zoomLevel = -1;
		handler = new Handler();

	}

	/**
	 * Invoked when a view is clicked
	 */
	@Override
	public void onClick(View v) {

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
		case R.id.myChart_button:
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
	private static synchronized void emotionOverlaySetter(int emotion, ArrayList<HappyBottle> toshow, ItemizedEmotionOverlay overlay){ 

		if (toshow == null) {return; }///THIS IS A PROBLEM AND SHOULD NEVER HAPPEN
		overlay.emptyOverlay();
		for(HappyBottle bottle : toshow) {

			if (bottle.getEmo()==emotion) { //happy or sad filter
				int latitude = bottle.getLat();
				int longitude = bottle.getLong();
				GeoPoint point = new GeoPoint(latitude,longitude);
				String S = (String) new Timestamp(bottle.getTime()).toLocaleString();
				overlay.addToOverlay(new OverlayItem(point, S+emotion, bottle.getMsg()));
			}
		}
	}

	/**
	 * This method updates the overlays for only the current the current view
	 */
	private ArrayList<HappyBottle> updateToView(){
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
		Log.d(TAG, "we are now using local pins with updateToView");
		return datahelper.getLocalRecent(minLat,maxLat,minLong,maxLong,100);
	}

	//to sync and update bottles with mapview
	private ArrayList<HappyBottle> updater(){
		if(isMoved()){
			Log.d(TAG, "updatetoview from updater");
			return updateToView();
		}
		return null;
	}

	private void stablePainter(){
		//Creates a new runnable that stabalizes the screen
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				int maxcount = 1000;
				for (int i = 0; i < maxcount; i++) {
					//testing about getLatitudeSpan and getLongitudeSpan fake values
					if (map.getLatitudeSpan() == 0 & map.getLongitudeSpan() == 360000000){
						try{
							//thread will sleep awhile!
							Thread.sleep(80);
						}catch (InterruptedException ex){
							ex.printStackTrace();
						}
				//map is stabilized and if we get height and width will have be real values. 
				} else {
						//remove other tasks if queued for the handler
						handler.removeCallbacks(latestThread); 
						//new Runnable that updates the overlay
						latestThread = new Runnable(){
							@Override
							public void run(){
								Log.d(TAG, "running the thread that updates the overlays");
								ArrayList<HappyBottle> newBottles = updater();
								if (newBottles != null){
									emotionOverlaySetter(1,newBottles,happyOverlay);
									emotionOverlaySetter(0,newBottles,sadOverlay);
								}
							}
						};
						handler.postDelayed(latestThread, 10); //delay the posting of the new pins by a tiny fraction of a  second, because that way it will let you invalidate if you keep moving.
						maxcount = 0; //exit internal loop
						return;
					}	};}};

					new Thread(runnable).start();

	}

	private class ZoomPanListener extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			while(true){
				if(isMoved()){
					stablePainter();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}}}

	//Required method to make the map work
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean isMoved() {
		GeoPoint trueCenter =map.getMapCenter();
		int trueZoom = map.getZoomLevel();

		if(!((trueCenter.equals(center)) && (trueZoom == zoomLevel))){
			center = map.getMapCenter();
			zoomLevel = map.getZoomLevel();
			Log.d(TAG, "You moved!:" + center.toString() + " zoom: " + zoomLevel);
			return true;
		}
		else{
			return false;
		}
	}

	//Disables MyLocation
	@Override
	protected void onPause() {
		userLocationOverlay.disableMyLocation();  
		synchronized (zpl){
			zpl.cancel(true);}
		super.onPause();
	}

	//Enables MyLocation 
	@Override
	protected void onResume() {
		super.onResume();
		userLocationOverlay.enableMyLocation();
		zpl = new ZoomPanListener();
		zpl.execute(null);
		center = new GeoPoint(-1,-1); //fake a move so that updater thinks we've moved and populates the initial screen.
		stablePainter();
		
		
	}
}