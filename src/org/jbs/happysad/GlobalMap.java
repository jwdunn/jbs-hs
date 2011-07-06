package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GlobalMap extends MapActivity implements OnClickListener{
   private MapView map;
   private MapController controller;
   int checkHappy = 1;
   int checkSad = 1;
   MyLocationOverlay overlay;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.globalmap);
      initMapView();
      initMyLocation();
      
      // Add ClickListener for the button
      View sadButton = findViewById(R.id.showSad);
      sadButton.setOnClickListener(this);
      
      // Add ClickListener for the button
      View happyButton = findViewById(R.id.showHappy);
      happyButton.setOnClickListener(this); 
      
      // Add ClickListener for the button
      View switchButton = findViewById(R.id.switchView);
      switchButton.setOnClickListener(this); 

   }
   
   @Override
   public void onClick(View v) {
	   
	   
	   Drawable drawable = this.getResources().getDrawable(R.drawable.mapsmile);
	   ItemizedEmotionOverlay itemizedoverlay = new ItemizedEmotionOverlay(drawable, this);
	   Drawable drawable2 = this.getResources().getDrawable(R.drawable.mapfrown);
	   ItemizedEmotionOverlay itemizedoverlay2 = new ItemizedEmotionOverlay(drawable2, this);
	      
	   HappyData datahelper = new HappyData(this);
	   ArrayList<HappyBottle> plottables = datahelper.getAllHistory();
	   List<Overlay> mapOverlays = map.getOverlays();
	      
	   emotionOverlayMaker(1,plottables,itemizedoverlay);
	   emotionOverlayMaker(0,plottables,itemizedoverlay2);
	   
	   
       switch(v.getId()){
           case R.id.switchView:		
        	   if (map.isStreetView()==false){
        		   map.setStreetView(true);
        		   map.setSatellite(false);
        		   map.invalidate();
        	   }
        	   else{
        		   map.setStreetView(false);
        		   map.setSatellite(true);
        	   }
        	   
               break;
           
           case R.id.showHappy:
        	   if (this.checkHappy==1){
	        	   mapOverlays.add(itemizedoverlay);
	        	   checkHappy = 0;
        	   }
        	   else{
	        	   mapOverlays.clear();
	        	   if (checkSad == 0){
		        	   mapOverlays.add(itemizedoverlay2);
	        	   }
        		   checkHappy = 1;
        	   }
        	   break;
           
           case R.id.showSad:		
        	   if (checkSad == 1){
	        	   mapOverlays.add(itemizedoverlay2);
	        	   checkSad = 0;	        	   
        	   }	   
        	   else{
        		   mapOverlays.clear();
        		   if (this.checkHappy==0){
    	        	   mapOverlays.add(itemizedoverlay);
            	   }
        		   checkSad = 1;
        	   }
               break;
       }
	   map.getOverlays().add(overlay);
	   map.invalidate();       
   }
   
   
   /** Find and initialize the map view. */
   private void initMapView() {
      map = (MapView) findViewById(R.id.map);
      controller = map.getController();
      map.setSatellite(true);
      map.setBuiltInZoomControls(true);
   }
   
   /** Start tracking the position on the map. */
   private void initMyLocation() {
      overlay = new MyLocationOverlay(this, map);
      overlay.enableMyLocation();
      overlay.runOnFirstFix(new Runnable() {
         public void run() {
            // Zoom in to current location
            controller.setZoom(8);
            controller.animateTo(overlay.getMyLocation());
         }
      });
      map.getOverlays().add(overlay);
   }
   	
   /** Creates and returns overlay Item*/
   private static void emotionOverlayMaker(int emotion, ArrayList<HappyBottle> plottables, ItemizedEmotionOverlay itemizedoverlay){
	   Iterator<HappyBottle> itr = plottables.iterator(); 
	   while(itr.hasNext()) {
		     HappyBottle element = itr.next();
		     if (element.getEmo()==emotion){
		     	int latitude =  (int) (element.getLat()*1E6);
		        int longitude =  (int) (element.getLong()*1E6);
		        GeoPoint point = new GeoPoint(latitude,longitude);
		        float time = element.getTime();
		        String S ="";
		        S = S + new Timestamp((long)time);
		        itemizedoverlay.addOverlay(new OverlayItem(point, S, element.getMsg()));		        
		     }
	   }   
   }
   
   
   /** Required method for mapView*/
   @Override
   protected boolean isRouteDisplayed() {
      // Required by MapActivity
      return false;
   }
   
   //Disables MyLocation
   protected void onPause() {
	   super.onPause();
	   overlay.disableMyLocation();  
   }
   
   //Enables MyLocation
   protected void onResume() {
	   super.onResume();
	   overlay.enableMyLocation();
   }
}