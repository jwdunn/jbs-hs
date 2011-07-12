package org.jbs.happysad;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedEmotionOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext;
	
	public ItemizedEmotionOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	public ItemizedEmotionOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
		  populate();
	}

	
	@Override
	protected boolean onTap(int index) {
		
		  OverlayItem item = mOverlays.get(index);
		  Log.e("Checking","NPWL1");
		  AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(mContext);
		  Log.e("Checking","NPWL2");
		  String date = item.getTitle().substring(0, item.getTitle().length()-1);
		  char emotion = item.getTitle().charAt(item.getTitle().length()-1);
		  if (emotion == '1'){
			  dialogbuilder.setIcon(R.drawable.mapsmile);
		  }
		  else{
			  dialogbuilder.setIcon(R.drawable.mapfrown);
		  }
		  dialogbuilder.setTitle(date);
		  dialogbuilder.setMessage(item.getSnippet());
		  Dialog dialog = dialogbuilder.create();
		  dialog.setCanceledOnTouchOutside(true);
		  dialog.show();
		  
		return true;
	}
	
	
}
