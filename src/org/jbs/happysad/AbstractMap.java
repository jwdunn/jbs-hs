package org.jbs.happysad;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.text.format.Time;
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

    
}
