package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

/**
 * Creates a text-based list of a user's updates, sorted by date created
 * @author HappyTrack
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
	
	//shows the updates
	private void showUpdates(ArrayList<HappyBottle> l){
		//for each Bottle pull data that will be placed in the hash map
		ArrayList<HashMap<String,String>> newList = new ArrayList<HashMap<String,String>>();
		for (HappyBottle b : l){
			HashMap<String, String> m = new HashMap<String, String>();
			String e = (b.getEmo()>0)?"Happy":"Sad";
			m.put("line1", e + ": " +  b.getMsg());
			m.put("line2", new Timestamp(b.getTime()).toLocaleString() );

			newList.add(m);
		}
		//the adapter makes the updates look nice
		adapter = new SimpleAdapter(this, newList, R.layout.item, FROM, TO); 
		setListAdapter(adapter);
	}
}