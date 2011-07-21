package org.jbs.happysad;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.maps.GeoPoint;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

/**
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class GlobalMap extends AbstractMap implements OnClickListener {
	Runnable running;
	private static final String TAG = "GlobalMap";
	boolean check;
	ZoomPanListener zpl;
	int bottlesPerView = 10;

	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		readIntents();

		initOverlayStuff();
		//initialize and display map view and user location
		initMapView();
		initMyLocation();	

		setDate = findViewById(R.id.date_button);
		setDate.setOnClickListener(this);
		setTime = findViewById(R.id.time_button);
		setTime.setOnClickListener(this);

		initbuttons();
		initDateStuff();

		dateTimeUpdate();		
		initLatestUpdateOverlay(justUpdated);//sahar edit.
		//Finds the my_map view

		center = new GeoPoint(-1,-1);
		zoomLevel = map.getZoomLevel();

	}

	protected void initbuttons(){
		View sadButton = findViewById(R.id.showSad);
		View happyButton = findViewById(R.id.showHappy);
		View chartButton = findViewById(R.id.myTrack_button);
		View histButton = findViewById(R.id.myChart_button);
		View backButton = findViewById(R.id.arrowLeft);
		View forwardButton = findViewById(R.id.arrowRight);
		View myButton = findViewById(R.id.map);

		sadButton.setOnClickListener(this);
		happyButton.setOnClickListener(this); 
		chartButton.setOnClickListener(this);  	
		histButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		forwardButton.setOnClickListener(this);
		myButton.setOnClickListener(this);

		
	}
	/**
	 * Invoked when a view is clicked
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			//used to show/hide the happy faces
		case R.id.showHappy:
			if (checkHappy==0){ 
				map.getOverlays().add(happyOverlay); //adds happy face overlay to visible overlays 
				checkHappy = 1; 
			} else{ 
				map.getOverlays().clear(); //clears all overlays
				map.getOverlays().add(recentOverlay);
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
				map.getOverlays().add(recentOverlay);
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
			HappyBottle b = justUpdated;
			j.putExtra("BottleLat", b.getLat());
			j.putExtra("BottleLong", b.getLong());
			j.putExtra("BottleMsg", b.getMsg());
			j.putExtra("BottleEmo", b.getEmo());
			j.putExtra("BottleTime", b.getTime());
			j.putExtra("id", b.getUID());
			startActivity(j);
			finish();
			break;

		case R.id.myTrack_button:
			startActivity(new Intent(this, History.class));
			break;

		case R.id.myChart_button:
			startActivity(new Intent(this, ChartList.class));
			break;

		case R.id.date_button:
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.time_button:
			showDialog(TIME_DIALOG_ID);
			break;

		case R.id.arrowLeft:
			if (newBottles == null || newBottles.size() == 0) {
				handler.removeCallbacks(running);
				Runnable runnable = new Runnable(){
					@Override
					public void run(){
						Toast.makeText(getApplicationContext(), "Sorry, There is Nothing More to Show", Toast.LENGTH_SHORT).show();
					}
				};
				running = runnable;
				handler.postDelayed(runnable, 1000);
				


			} if(newBottles != null && newBottles.size()>0){
				mapClear();
				epochTime = newBottles.get(newBottles.size()-1).getTime();
				timeForView.set(epochTime);
				setTimeObjectValues();
			}
			break;	

		case R.id.arrowRight:
			ArrayList<HappyBottle> temp = updateViewAfter();
			if(temp != null && temp.size()>1){
				epochTime = temp.get(temp.size()-1).getTime();
				timeForView.set(epochTime);
				mapClear();
				setTimeObjectValues();
				dateTimeUpdate();
			} else {
				handler.removeCallbacks(running);
				Runnable runnable = new Runnable(){
					@Override
					public void run(){
						Toast.makeText(getApplicationContext(), "Sorry, There is Nothing More to Show", Toast.LENGTH_SHORT).show();
					}
				};
				running = runnable;
				handler.postDelayed(runnable, 1000);
			}
			break;
		}
		map.invalidate();
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
					}	
				};
			}
		};
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
							mapClear();
							zoomLevel = map.getZoomLevel();
						}

					});	}
				if(isTimeChanged()){
					mapClear();
				}
				if(isMoved() || isTimeChanged()){
					stablePainter();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected ArrayList<HappyBottle> updateViewAfter(){
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
		return temp;
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
