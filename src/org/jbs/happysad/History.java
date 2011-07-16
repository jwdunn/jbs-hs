package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;

//this activity corresponds to the "mytrack" activity
//it's a nice text-based list of updates, sorted by date created.
public class History extends ListActivity implements OnClickListener{
	private HappyData dataHelper;
	private static int[] TO = {R.id.item_text1, R.id.item_text2 };
	private static String[] FROM = { "line1","line2" };	
	private SimpleAdapter adapter;
	StringBuilder result;

	@Override
	public void onCreate(Bundle savedInstanceState){
		//set up the oncreate. simple
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		View refreshButton = findViewById(R.id.refresh_button);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
		//buttons etc.
		ArrayList<HappyBottle> list = dataHelper.getMyHistory(); 
		showUpdates(list);
	}

	public void onClick(View v) {
		switch(v.getId()) {		
		//this refresh button requeries the database for you to see if you have new updates
		case R.id.refresh_button:	
			ArrayList<HappyBottle> list = dataHelper.getMyHistory(); 
			showUpdates(list);
			adapter.notifyDataSetChanged();
			break;
		}
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