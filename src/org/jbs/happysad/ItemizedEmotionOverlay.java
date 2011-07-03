package org.jbs.happysad;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedEmotionOverlay extends ItemizedOverlay {
	
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
	}

	
	@Override
	protected boolean onTap(int index) {
		
		//if (mContext==null){
		//	AlertDialog.Builder dialog = new AlertDialog.Builder(map.this);
		//}
		//else{
		  OverlayItem item = mOverlays.get(index);
		  Log.e("Checking","NPWL1");
		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		  Log.e("Checking","NPWL2");
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();
		//}
		return true;
	}
	
	
}
