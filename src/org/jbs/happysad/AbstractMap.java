package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public abstract class AbstractMap extends MapActivity  {
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

	//Starts tracking the users position on the map. 
	protected void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
		map.getOverlays().add(userLocationOverlay);  //adds the users location overlay to the overlays being displayed
	}
	
	//helper method for showHappy and showSad onClick cases
	protected void invalidateOverlay() {
		map.getOverlays().add(userLocationOverlay);
	}

	
	
	
	

	
	//Required method to make the map work
	protected boolean isRouteDisplayed() {
		return false;
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
	
	//Finds and initializes the map view.
	protected void initMapView() {
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
	
//-----------DATE AND TIME STUFF---------------------------------------------------------
	
	protected Time timeForView = new Time();
	protected long epochChecker; // used to check if time changed
	protected int year;
	protected int month;
	protected int day;
	protected int hour;
	protected int minute;
	protected long epochTime;
	
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
	
	protected void goToMyLocation() {
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
	
	
	
    // updates the date in the TextView
    protected void dateTimeUpdate() {
    	timeForView.set(0,minute,hour,day,month,year);
    	epochTime = timeForView.normalize(true);
    	((Button) setDate).setText(new StringBuilder().append(month + 1).append(" - ").append(day).append(" - ").append(year).append(" "));
    	((Button) setTime).setText(new StringBuilder().append(pad(convertAMPM(hour))).append(":").append(pad(minute)).append(" "+checkAMPM(hour)));
    	//Toast.makeText(getBaseContext(), "Time reference: "+epochTime, Toast.LENGTH_LONG).show();
    }
    
    protected void setTimeObjectValues(){
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
    
   
    
    protected boolean isTimeChanged(){
    	if(!(epochChecker == epochTime)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
	
    //-----------DONE DATE AND TIME STUFF----------------------------------------------------	

    
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
	
	
    
}
