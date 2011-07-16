package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;

/**
 * Creates a text-based list of a user's updates, sorted by date created
 * @author HappySad
 */
public class History extends ListActivity {
	private HappyData dataHelper;
	private static int[] TO = {R.id.item_text1, R.id.item_text2};
	private static String[] FROM = { "line1","line2" };	
	private SimpleAdapter adapter;
	StringBuilder result;
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		dataHelper = new HappyData(this);
		ArrayList<HappyBottle> list = dataHelper.getMyHistory(); 
		showUpdates(list);
	}

	private void showUpdates(ArrayList<HappyBottle> l){
		//use adapters to make a nice looking list of updates
		ArrayList<HashMap<String,String>> newList = new ArrayList<HashMap<String,String>>();
		for (HappyBottle b : l){
			//go through the arraylist of happybottles to display, and add them to the adapter. 
			Log.d("History", "adding bottle with msg " + b.getMsg());
			HashMap<String, String> m = new HashMap<String, String>();
			String e = (b.getEmo()>0)?"Happy":"Sad";
			m.put("line1", e + ": " +  b.getMsg());
			m.put("line2", new Timestamp(b.getTime()).toLocaleString() );

			newList.add(m);
		}
		//the adapter will make things look nice and everything.
		adapter = new SimpleAdapter(this, newList, R.layout.item, FROM, TO); 
		setListAdapter(adapter);

	}    
}