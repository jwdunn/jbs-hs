//FYI Sahar is a sexy beast.

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
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import android.widget.Button;
import android.widget.Toast;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;

/**
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class GlobalMap extends AbstractMap implements OnClickListener {
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


	//fields
	private static final String TAG = "GlobalMap";
	boolean check;
	ZoomPanListener zpl;
	int bottlesPerView = 10;
	HappyData datahelper = new HappyData(this);


	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		checkHappy = getIntent().getExtras().getInt("Happy");
		checkSad = getIntent().getExtras().getInt("Sad");
		goToMyLocation = getIntent().getExtras().getBoolean("GoToMyLocation");
		streetView = getIntent().getExtras().getInt("Street");

		//Defines the drawable items for the happy and sad overlays
		Drawable happyface = this.getResources().getDrawable(R.drawable.pinhappy);
		Drawable sadface = this.getResources().getDrawable(R.drawable.pinsad);

		//initializes the happy and sad overlays
		happyOverlay = new ItemizedEmotionOverlay(happyface, this);	
		sadOverlay = new ItemizedEmotionOverlay(sadface, this);
		//initialize and display map view and user location
		initMapView();
		initMyLocation();	

		View sadButton = findViewById(R.id.showSad);
		View happyButton = findViewById(R.id.showHappy);
		View switchButton = findViewById(R.id.switchView);
		View chartButton = findViewById(R.id.myTrack_button);
		View histButton = findViewById(R.id.myChart_button);
		View backButton = findViewById(R.id.arrowLeft);
		View forwardButton = findViewById(R.id.arrowRight);
		View myButton = findViewById(R.id.map);

		sadButton.setOnClickListener(this);
		happyButton.setOnClickListener(this); 
		switchButton.setOnClickListener(this); 
		chartButton.setOnClickListener(this);  	
		histButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		forwardButton.setOnClickListener(this);
		myButton.setOnClickListener(this);

		setDate = findViewById(R.id.date_button);
		setDate.setOnClickListener(this);
		setTime = findViewById(R.id.time_button);
		setTime.setOnClickListener(this);

		// get the current date
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		dateTimeUpdate();		
		//Finds the my_map view
		((Button) myButton).setText("MyMap");

		center = new GeoPoint(-1,-1);
		zoomLevel = map.getZoomLevel();
		
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
			Intent j = new Intent(this, MyMap.class);
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
			//Toast.makeText(getBaseContext(), "Time reference: "+epochTime, Toast.LENGTH_LONG).show();
			break;

		case R.id.time_button:
			showDialog(TIME_DIALOG_ID);
			//Toast.makeText(getBaseContext(), "Time reference: "+epochTime, Toast.LENGTH_LONG).show();
			break;

		case R.id.arrowLeft:
			if (newBottles == null){
				//Toast.makeText(getBaseContext(), "newBottles is null", Toast.LENGTH_LONG).show();
			}
			else if (newBottles.size()==0){
				//Toast.makeText(getBaseContext(), "newBottles has size 0", Toast.LENGTH_LONG).show();
			}
			if(newBottles != null && newBottles.size()>0){
				epochTime = newBottles.get(newBottles.size()-1).getTime();
				//epochTime = newBottles.get(0).getTime();
				timeForView.set(epochTime);
				setTimeObjectValues();
				happyOverlay.emptyOverlay();
				sadOverlay.emptyOverlay();
				//Toast.makeText(getBaseContext(), "Time reference: "+epochTime, Toast.LENGTH_LONG).show();
			}
			break;	

		case R.id.arrowRight:
			int centerLat = center.getLatitudeE6(); //finds center's latitude
			int centerLong = center.getLongitudeE6(); //finds center's longitude
			int width = map.getLongitudeSpan(); //gets width of view in terms of longitudes shown on screen
			int height = map.getLatitudeSpan(); //gets height of view in terms of latitudes shown on screen
			int minLong = centerLong-width/2; //gets the left most longitude shown
			int maxLong = centerLong+width/2; //gets the right most longitude shown
			int maxLat = centerLat+height/2; //gets the top most latitude shown
			int minLat = centerLat-height/2; //gets the bottom most latitude shown
			HappyData newdatahelper = new HappyData(this);
			ArrayList<HappyBottle> temp = newdatahelper.getLocalAfter(minLat,maxLat,minLong,maxLong,bottlesPerView,epochTime);
			if(temp != null && temp.size()!=0){
				if (newBottles == null || newBottles.size() == 0){
					epochTime = timeReference;
				}
				else{
					epochTime = temp.get(temp.size()-1).getTime();
				}
				timeForView.set(epochTime);
				happyOverlay.emptyOverlay();
				sadOverlay.emptyOverlay();
				setTimeObjectValues();
				dateTimeUpdate();
			}			
			else{
				Toast.makeText(getBaseContext(), "No future entries exist for this view.", Toast.LENGTH_LONG).show();
			}
			break;
		}
		map.invalidate();
	}


	private synchronized void emotionOverlayAdder(int emotion, ArrayList<HappyBottle> toshow, ItemizedEmotionOverlay overlay){ 
		if (toshow == null) {return; }///THIS IS A PROBLEM AND SHOULD NEVER HAPPEN
		//overlay.emptyOverlay();
		for(HappyBottle bottle : toshow) {
			if( !filter.contains(bottle) && bottle.getEmo() == emotion){
				//happy or sad filter^
				filter.add(bottle);
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
		//return datahelper.getLocalRecent(minLat,maxLat,minLong,maxLong,100);
		return datahelper.getLocalBefore(minLat,maxLat,minLong,maxLong,bottlesPerView,epochTime);
	}

	//to sync and update bottles with mapview
	private ArrayList<HappyBottle> updater(){
		if(isMoved() || isTimeChanged()){
			center = map.getMapCenter();
			zoomLevel = map.getZoomLevel();
			Log.d(TAG, "updatetoview from updater");
			return updateToView();
		}
		else{
			//Toast.makeText(getBaseContext(), "OldBottles: ", Toast.LENGTH_LONG).show();
			return newBottles;	
		}
	}

	private void stablePainter(){
		//Creates a new runnable that stabilizes the screen
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
								ArrayList<HappyBottle> temp = newBottles;
								newBottles = updater();
								if (newBottles != null && newBottles.size() != 0){
									timeReference = newBottles.get(0).getTime();
								}
								if (!(newBottles.equals(temp))){
									emotionOverlayAdder(1,newBottles,happyOverlay);
									emotionOverlayAdder(0,newBottles,sadOverlay);
								}
								map.invalidate();
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
					stablePainter();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}}}


	public boolean isMoved() {
		GeoPoint trueCenter =map.getMapCenter();
		int trueZoom = map.getZoomLevel();
		if(!((trueCenter.equals(center)) && (trueZoom == zoomLevel))){	
			Log.d(TAG, "You moved!:" + center.toString() + " zoom: " + zoomLevel);
			return true;
		}else{
			return false;
		}}



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
		timeForView.setToNow();
		epochChecker = timeForView.normalize(true);
		userLocationOverlay.enableMyLocation();
		Random r = new Random();
		center = new GeoPoint(-10, r.nextInt()); //fake a move so that updater thinks we've moved and populates the initial screen.
		zpl = new ZoomPanListener();
		zpl.execute(null);

		stablePainter();


	}




}
