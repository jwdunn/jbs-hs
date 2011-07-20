//my failed java part 2 (sunday)

package org.jbs.happysad;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.text.format.Time;
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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Creates a Global Map view with Google Maps API with everyone's HappyBottles
 * @author HappyTrack
 */
public class GlobalMap extends MapActivity implements OnClickListener {
	//fields
	private static final String TAG = "GlobalMap";
	private MapView map; 
	int checkHappy;
	int checkSad;
	boolean goToMyLocation;
	boolean check;
	int streetView;
	MyLocationOverlay userLocationOverlay;
	private MapController controller;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay;
	HappyData datahelper = new HappyData(this);
	//	private volatile ArrayList<HappyBottle>  plottables;
	ArrayList<HappyBottle> newBottles;
	int zoomLevel;
	GeoPoint center;
	private Handler handler;
	Runnable latestThread;
	ZoomPanListener zpl;
	boolean enableChart;
	HashSet<HappyBottle> filter = new HashSet<HappyBottle>();
	int bottlesPerView = 10;
	
	//---------------For Date and Time------------------------------------------------------------------------------------//
	  
	private Time timeForView = new Time();
	private long epochChecker; // used to check if time changed
	private long timeReference = 0; //used to move forward in time
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private long epochTime;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	
	View setDate;// = findViewById(R.id.date_button);
	View setTime;// = findViewById(R.id.time_button);
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int new_year, int new_month,
				int new_day) {
			year = new_year;
			month = new_month;
			day = new_day;
			dateTimeUpdate();
		}
	};

	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int new_hour, int new_minute) {
			hour = new_hour;
			minute = new_minute;
			dateTimeUpdate();
		}
	};
	
	//---------------Done for Date and Time-------------------------------------------------------------------------------//	

	
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

	//helper method for showHappy and showSad onClick cases
	private void invalidateOverlay() {
		map.getOverlays().add(userLocationOverlay);
	}

	//Finds and initializes the map view.
	private void initMapView() {
		map = (MapView) findViewById(R.id.themap);
		controller = map.getController();
		
		//checks streetView
		if (streetView == 1) {
			map.setStreetView(true);
			map.setSatellite(false);
		} else {
			map.setStreetView(false);
			map.setSatellite(true);	
		}
		map.invalidate();	

		//adds the sad and happy overlays to the map
		if (checkSad == 1)
			map.getOverlays().add(sadOverlay);
		if (checkHappy == 1) 
			map.getOverlays().add(happyOverlay);
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons

	}

	//Starts tracking the users position on the map. 
	private void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
		map.getOverlays().add(userLocationOverlay);  //adds the users location overlay to the overlays being displayed
	}
	
	private void goToMyLocation() {
		if (goToMyLocation == true) {
			userLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					// Zoom in to current location
					controller.animateTo(userLocationOverlay.getMyLocation());
					controller.setZoom(15); //sets the map zoom level to 15
				}
			});
		}
		map.getOverlays().add(userLocationOverlay); //adds the users location overlay to the overlays being displayed
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
	
	public void chartEnable(ArrayList<HappyBottle> plottables) {
		Iterator<HappyBottle> itr = plottables.iterator(); 
		
		while(itr.hasNext()) {     
			HappyBottle element = itr.next();
			
			int x = new Timestamp (element.getTime()).getMonth() + 1;
			int y = new Timestamp (element.getTime()).getYear() + 1900;
			int z = new Timestamp (element.getTime()).getDate();
			
			int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int date = Calendar.getInstance().get(Calendar.DATE);
		     
		   	if (x == month && y == year && z == date){
		   		this.enableChart = true;
		   		break;
		   	}	
		   	this.enableChart = false;
		}
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
					});
					
				}
				if(isMoved() || isTimeChanged()){
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
			
			Log.d(TAG, "You moved!:" + center.toString() + " zoom: " + zoomLevel);
			return true;
		}
		else{
			return false;
		}
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
	    	goToMyLocation = true;
	        goToMyLocation();
	        return true;
	    case R.id.new_update:
	    	startActivity(new Intent(this, Prompt.class));
	        
	    default:
	        return super.onOptionsItemSelected(item);
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
		timeForView.setToNow();
		epochChecker = timeForView.normalize(true);
		userLocationOverlay.enableMyLocation();
		Random r = new Random();
		center = new GeoPoint(-10, r.nextInt()); //fake a move so that updater thinks we've moved and populates the initial screen.
		zpl = new ZoomPanListener();
		zpl.execute(null);
		
		stablePainter();
		
	
	}
	
	//-----------DATE AND TIME STUFF---------------------------------------------------------
	
    // updates the date in the TextView
    private void dateTimeUpdate() {
    	timeForView.set(0,minute,hour,day,month,year);
    	epochTime = timeForView.normalize(true);
    	((Button) setDate).setText(new StringBuilder().append(month + 1).append(" - ").append(day).append(" - ").append(year).append(" "));
    	((Button) setTime).setText(new StringBuilder().append(pad(convertAMPM(hour))).append(":").append(pad(minute)).append(" "+checkAMPM(hour)));
    	//Toast.makeText(getBaseContext(), "Time reference: "+epochTime, Toast.LENGTH_LONG).show();
    }
    
    private void setTimeObjectValues(){
    	month = timeForView.month;
    	year = timeForView.year;
    	day = timeForView.monthDay;
    	hour = timeForView.hour;
    	minute = timeForView.minute;
    	((Button) setDate).setText(new StringBuilder().append(month + 1).append(" - ").append(day).append(" - ").append(year).append(" "));
    	((Button) setTime).setText(new StringBuilder().append(pad(convertAMPM(hour))).append(":").append(pad(minute)).append(" "+checkAMPM(hour)));
    }
    
    private static int convertAMPM (int convertedhour){
    	if(convertedhour>12){
    		convertedhour = convertedhour-12;
    	}
    	return (convertedhour);
    }
    
    private static String checkAMPM (int hour){
    	if(hour<12){
    		return ("AM");
    	}
    	else{
    		return ("PM");
    	}
    }
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    timeSetListener, hour, minute, false);
        case DATE_DIALOG_ID:
    		return new DatePickerDialog(this,
                    dateSetListener,
                    year, month, day);
        }
        return null;
    }
    
    protected boolean isTimeChanged(){
    	if(!(epochChecker == epochTime)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
	
    //-----------DONE DATE AND TIME STUFF----------------------------------------------------	

    

	
}
