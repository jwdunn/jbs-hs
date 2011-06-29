package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PersonalMap extends MapActivity {
   private MapView map;
   private MapController controller;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.personalmap);
      initMapView();
      
      List<Overlay> mapOverlays = map.getOverlays();
      Drawable drawable = this.getResources().getDrawable(R.drawable.mapsmile);
      HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
      Drawable drawable2 = this.getResources().getDrawable(R.drawable.mapfrown);
      HelloItemizedOverlay itemizedoverlay2 = new HelloItemizedOverlay(drawable2, this);
      

      
      HappyData datahelper = new HappyData(this);
      ArrayList<HappyBottle> plottables = datahelper.getAllHistory();
      
      Iterator<HappyBottle> itr = plottables.iterator(); 
      while(itr.hasNext()) {
	     HappyBottle element = itr.next();
	     if (element.getEmo()==1){
	    	Log.e("Happy","Happy");
	     	int latitude =  (int) (element.getLat()*1E6);
	        int longitude =  (int) (element.getLong()*1E6);
	        GeoPoint point = new GeoPoint(latitude,longitude);
	        float time = element.getTime();
	        String S ="";
	        S = S + new Timestamp((long)time);
	        itemizedoverlay.addOverlay(new OverlayItem(point, S, element.getMsg()));
	     }
      }
      
      
      Iterator<HappyBottle> itr2 = plottables.iterator(); 
      while(itr2.hasNext()) {
	     HappyBottle element = itr2.next();
	     if (element.getEmo()==0){
	        Log.e("Sad","Sad");
	     	int latitude =  (int) (element.getLat()*1E6);
	        int longitude =  (int) (element.getLong()*1E6);
	        GeoPoint point = new GeoPoint(latitude,longitude);
	        float time = element.getTime();
	        String S ="";
	        S = S + new Timestamp((long)time);
	        itemizedoverlay2.addOverlay(new OverlayItem(point, S, element.getMsg()));
	     }
      }  
      mapOverlays.add(itemizedoverlay);
      mapOverlays.add(itemizedoverlay2);
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
      final MyLocationOverlay overlay = new MyLocationOverlay(this, map);
      overlay.enableMyLocation();
      //overlay.enableCompass(); // does not work in emulator
      overlay.runOnFirstFix(new Runnable() {
         public void run() {
            // Zoom in to current location
            controller.setZoom(8);
            controller.animateTo(overlay.getMyLocation());
         }
      });
      map.getOverlays().add(overlay);
   }
   	

   
   @Override
   protected boolean isRouteDisplayed() {
      // Required by MapActivity
      return false;
   }
}

