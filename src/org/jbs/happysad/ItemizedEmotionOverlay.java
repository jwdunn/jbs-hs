package org.jbs.happysad;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Class used to create itemized overlays of emotion bottles
 * @author tahaalibak
 *
 */
public class ItemizedEmotionOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> overlay = new ArrayList<OverlayItem>(); //creates a new overlay that
	Context context; //creates a reference to the context of this item
	
	/**
	 * default constructor that sets a face image for every item in the overlay
	 * also sets context
	 * @param defaultMarker
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
	    overlay.add(item); //adds item to overlay
	    populate();
	}
	
	/**
	 * method used to create an item given an overlay and a reference index number
	 */
	@Override
	protected OverlayItem createItem(int i) {
	  return overlay.get(i);
	}
	
	/**
	 * method used to return the number of items in an overlay
	 */
	@Override
	public int size() {
	  return overlay.size();
	}

	/**
	 * method used to create a dialog box every time an overlay item is tapped
	 */
	@Override
	protected boolean onTap(int index) {
		  
		OverlayItem item = overlay.get(index); //gets an item from the overlay by using the index number of the item
		String date = item.getTitle().substring(0, item.getTitle().length()-1); //creates the date string which had been passed into the item title
		char emotion = item.getTitle().charAt(item.getTitle().length()-1); //gets the emotion character which was also in the item title
		  
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context); //creates a blueprint for the dialog
		if (emotion == '1'){
			dialogbuilder.setIcon(R.drawable.mapsmile); //sets the image that appears in the dialog box for a happy face
		}
		else{
			dialogbuilder.setIcon(R.drawable.mapfrown); //sets the image that appears in the dialog box for a sad face
		}
		dialogbuilder.setTitle(date); //sets the date as the title of the dialog box
		dialogbuilder.setMessage(item.getSnippet()); //sets the message as part of the dialog box
		  
		Dialog dialog = dialogbuilder.create(); //uses the blueprint to create the dialog box
		dialog.setCanceledOnTouchOutside(true); //sets a property that dismisses the dialog box if a region outside it is tapped
		dialog.show();  //displays the dialog box
		
		return true;
	}
	
	
}
