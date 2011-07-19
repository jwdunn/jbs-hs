package org.jbs.happysad;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Creates an itemized overlay of emotion bottles
 * @author HappyTrack
 */
public class ItemizedEmotionOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlay = new ArrayList<OverlayItem>();
	Context context;
	
	/**
	 * Sets a face image and context for every item in the overlay
	 * @param face
	 * @param context 
	 */
	public ItemizedEmotionOverlay(Drawable face, Context context) {
		  super(boundCenterBottom(face)); //places image so that the point it marks is at the center of its base
		  this.context = context; //passes the context
		  populate(); //method used to prevent an empty overlay from causing the map to crash on touch
	}
	
	/**
	 * method used to add an item to the overlay
	 * @param item
	 */
	public void addToOverlay(OverlayItem item) {
	    overlay.add(item);
	    populate();
	}
	
	/**
	 * method to empty out overlay
	 */
	protected void emptyOverlay(){
		overlay.clear();
		setLastFocusedIndex(-1);
		populate();
	}
	
	//method used to create an item given an overlay and a reference index number
	protected OverlayItem createItem(int i) {
	  return overlay.get(i);
	}
	
	/**
	 * Returns the number of items in an overlay
	 * @return the number of itmes in an overlay
	 */
	public int size() {
	  return overlay.size();
	}

	//method used to create a dialog box every time an overlay item is tapped
	protected boolean onTap(int index) {
		//set up helper statements for the dialog box
		OverlayItem item = overlay.get(index);
		String date = item.getTitle().substring(0, item.getTitle().length()-1);
		char emotion = item.getTitle().charAt(item.getTitle().length()-1);
		 
		//Builds the dialog box
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context);
		//sets the image that appears in the dialog box
		if (emotion == '1')
			dialogbuilder.setIcon(R.drawable.mapsmile); 
		else
			dialogbuilder.setIcon(R.drawable.mapfrown);
		dialogbuilder.setTitle(date);
		dialogbuilder.setMessage(item.getSnippet());
		  
		//Creates and shows the dialog box
		Dialog dialog = dialogbuilder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		
		return true;
	}
	
	
}
