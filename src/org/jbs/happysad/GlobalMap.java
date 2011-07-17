package org.jbs.happysad;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
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
	int checkHappy = 1;
	int checkSad = 1;
	MyLocationOverlay userLocationOverlay;
	ItemizedEmotionOverlay happyOverlay; 
	ItemizedEmotionOverlay sadOverlay; 
	
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
		
		//instantiates HappyData and creates an arraylist of all the bottles
		HappyData datahelper = new HappyData(this);
		ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
		
		//adds items to overlays
		emotionOverlayFiller(1,plottables,happyOverlay);
		emotionOverlayFiller(0,plottables,sadOverlay);
		
		//initialize and display map view and user location
		initMapView();
		initMyLocation();
		
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
			}
			newOverlay(); //method call
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
			}
			newOverlay(); //method call
			break;
		
		case R.id.map:
			startActivity(new Intent(this, MyMap.class));
			break;
		
		case R.id.myTrack_button:
			startActivity(new Intent(this, History.class));
			break;
		}
	}
	
	//helper method for showHappy and showSad onClick cases
	private void newOverlay() {
		map.getOverlays().add(userLocationOverlay);
		map.invalidate();
	}
	
	//Finds and initializes the map view.
	private void initMapView() {
		map = (MapView) findViewById(R.id.themap);
		//adds the sad and happy overlays to the map
		map.getOverlays().add(sadOverlay);
		map.getOverlays().add(happyOverlay);
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons
	}
	
	//Starts tracking the users position on the map. 
	private void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
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