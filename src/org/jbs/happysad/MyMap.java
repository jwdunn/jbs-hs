package org.jbs.happysad;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import android.widget.Button;
import android.widget.Toast;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;






/**
 * Creates a My Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class MyMap extends AbstractMap implements OnClickListener {
	//Note that you have access to the following variables:
	/*	
	protected MapView map; 
	int checkHappy;
	int checkSad;
	boolean goToMyLocation;
	int streetView;
	MyLocationOverlay userLocationOverlay;
	protected MapController controller;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay; 
	boolean enableChart;
	private final String TAG = "AbstractMap";

	int zoomLevel;
	GeoPoint center;
	Runnable latestThread;
	ArrayList<HappyBottle> newBottles;
	protected Handler handler = new Handler();
	HashSet<HappyBottle> filter = new HashSet<HappyBottle>();
	HappyData datahelper = new HappyData(this);
	 */
	private static final String TAG = "MyMap";
	ZoomPanListener zpl;



	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		readIntents();
		initOverlayStuff();

		//instantiates HappyData and creates an arraylist of all the bottles
		HappyData datahelper = new HappyData(this);
		ArrayList<HappyBottle> plottables = datahelper.getMyHistory();

		//adds items to overlays
		emotionOverlayAdder(1,plottables,happyOverlay);
		emotionOverlayAdder(0,plottables,sadOverlay);

		//initialize and display map view and user location
		initMapView();
		initMyLocation();
		goToMyLocation();

		setDate = findViewById(R.id.date_button);
		setDate.setOnClickListener(this);
		setTime = findViewById(R.id.time_button);
		setTime.setOnClickListener(this);
		
		//various setters of values
		initbuttons();
		initDateStuff();
		
		dateTimeUpdate();		

	
		center = new GeoPoint(-1,-1);
		zoomLevel = map.getZoomLevel();
	}

	protected void initbuttons(){
		//Finds the show_sad view
		View sadButton = findViewById(R.id.showSad);
		View happyButton = findViewById(R.id.showHappy);
		View switchButton = findViewById(R.id.switchView);
		View histButton = findViewById(R.id.myTrack_button);
		View chartButton = findViewById(R.id.myChart_button);
		
		sadButton.setOnClickListener(this);
		happyButton.setOnClickListener(this); 
		switchButton.setOnClickListener(this); 
		histButton.setOnClickListener(this);  	
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
			if (streetView==0) {
				map.setStreetView(true);
				streetView = 1;
				map.setSatellite(false);  
			} else{
				map.setStreetView(false);
				streetView = 0;
				map.setSatellite(true);
			}
			map.invalidate();
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
			j.putExtra("Street", streetView);
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
			HappyData datahelper = new HappyData(this);
			ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
			chartEnable(plottables);
			if (enableChart){
				startActivity(new Intent(this, ChartList.class));
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Please update your status before viewing the charts.", 100);
				toast.show();
			}
			break;

		case R.id.date_button:
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.time_button:
			showDialog(TIME_DIALOG_ID);
			break;		
		}
		map.invalidate();

	}



	//Our version of a listener - checks to see if the user moved.
	private class ZoomPanListener extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			while(true){
				if(zoomLevel != map.getZoomLevel()) {
					handler.post(new Runnable(){
						@Override
						public void run(){
							happyOverlay.emptyOverlay();
							sadOverlay.emptyOverlay();
							zoomLevel = map.getZoomLevel();
							filter.clear();	}
					});	}
				if(isMoved() || isTimeChanged()){
					drawRecentLocal();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}}}

	private void drawRecentLocal(){
		if (!(isMoved() || isTimeChanged())){return;}
		//so if we are here, we've moved, or time has changed.
		center = map.getMapCenter();
		zoomLevel = map.getZoomLevel();
		ArrayList<HappyBottle> temp = newBottles;
		newBottles = updateToView();
		/*if (newBottles != null && newBottles.size() != 0){
			timeReference = newBottles.get(0).getTime();
		}*/ //don't need this yet.
		if (!(newBottles.equals(temp))){

			handler.removeCallbacks(latestThread); 
			//new Runnable that updates the overlay
			latestThread = new Runnable(){
				@Override
				public void run(){
					emotionOverlayAdder(1,newBottles,happyOverlay);
					emotionOverlayAdder(0,newBottles,sadOverlay);
					//TODO change to emotionoverlayadder later
					map.invalidate();
				}};
				handler.postDelayed(latestThread, 10); //delay the posting of the new pins by a tiny fraction of a  second, because that way it will let you invalidate if you keep moving.
				return;
		}


	}
	/**
	 * This method updates the overlays for only the current the current view
	 */
	private ArrayList<HappyBottle> updateToView(){
		//Log.w("updateToView", "ERROR in new method");
		epochChecker = epochTime;
		GeoPoint center = map.getMapCenter(); //gets coordinates for map view's center
		int centerLat = center.getLatitudeE6(); //finds center's latitude
		int centerLong = center.getLongitudeE6(); //finds center's longitude
		int width = map.getLongitudeSpan(); //gets width of view in terms of longitudes shown on screen
		int height = map.getLatitudeSpan(); //gets height of view in terms of latitudes shown on screen
		int minLong = centerLong-width/2; //gets the left most longitude shown
		int maxLong = centerLong+width/2; //gets the right most longitude shown
		int maxLat = centerLat+height/2; //gets the top most latitude shown
		int minLat = centerLat-height/2; //gets the bottom most latitude shown
		Log.d("Coordinates", "minLong: "+minLong+"minLat: "+minLat+"maxLong"+maxLong+"maxLat"+maxLat);
		return datahelper.getMyLocalRecent(minLat,maxLat,minLong,maxLong,5);
	}


	//Disables MyLocation
	@Override
	protected void onPause() {
		super.onPause();
		userLocationOverlay.disableMyLocation();  
		synchronized (zpl){
			zpl.cancel(true);}
		super.onPause();
	}

	//Enables MyLocation
	@Override
	protected void onResume() {
		super.onResume();
		timeForView.setToNow();
		userLocationOverlay.enableMyLocation();
		zpl = new ZoomPanListener();
		zpl.execute(null);
	}


}
