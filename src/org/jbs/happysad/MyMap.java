package org.jbs.happysad;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Time;
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
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

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
	int streetView;
	MyLocationOverlay userLocationOverlay;
	private MapController controller;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay; 
	
	//---------------For Date and Time------------------------------------------------------------------------------------//
	
	private Time timeForView = new Time();
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	
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
		run = getIntent().getExtras().getBoolean("Run");
		streetView = getIntent().getExtras().getInt("Street");

		
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
		
		View setDate = findViewById(R.id.date_button);
		setDate.setOnClickListener(this);
		
		View setTime = findViewById(R.id.time_button);
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
			startActivity(new Intent(this, ChartList.class));
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
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
	}
	
	private void goToMyLocation() {
		if (run == true) {
			map.getOverlays().add(userLocationOverlay);
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
		timeForView.setToNow();
		userLocationOverlay.enableMyLocation();
	}
	
	//-----------DATE AND TIME STUFF---------------------------------------------------------
	
    // updates the timeForView Time object
    private void dateTimeUpdate() {
    	timeForView.set(0,minute,hour,day,month,year);
    	//Toast.makeText(getBaseContext(), "Time reference: "+timeForView.toString(), Toast.LENGTH_LONG).show();
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
	
    //-----------DONE DATE AND TIME STUFF----------------------------------------------------	
}