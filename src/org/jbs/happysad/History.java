package org.jbs.happysad;

import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;


public class History extends ListActivity implements OnClickListener{
	
	private String TAG = "History";
	private HappyData dataHelper;
	//private static String[] FROM = { TIME, MSG, EMO,  };
	//private static int[] TO = { R.id.time, R.id.msg, R.id.emo, };
	private static int[] TO = {R.id.item_text1, R.id.item_text2 };
	private static String[] FROM = { "line1","line2" };	
	private SimpleAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		View refreshButton = findViewById(R.id.refresh_button);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
	    //showUpdatesCursor(dataHelper.getMyHistoryCursor());
		ArrayList<HappyBottle> list = dataHelper.getMyHistory();
		showUpdates(list);
	}

	public void onClick(View v) {
		switch(v.getId()) {		
		case R.id.refresh_button:	
		    //showUpdatesCursor(dataHelper.getMyHistoryCursor());
			//dataHelper.syncDown();
			ArrayList<HappyBottle> list = dataHelper.getMyHistory();
			showUpdates(list);
			adapter.notifyDataSetChanged();
			break;
		}
	}
	
	private void showUpdates(ArrayList<HappyBottle> l){
		ArrayList<HashMap<String,String>> newList = new ArrayList<HashMap<String,String>>();
		for (HappyBottle b : l){
			Log.d("History", "adding bottle with msg " + b.getMsg());
			HashMap<String, String> m = new HashMap<String, String>();
			String e = (b.getEmo()>.5)?"Happy":"Sad"; //notice this assumes that emo is still a float. We will change this later.
			Log.d(TAG, "time:"+new Timestamp(b.getTime()).toString()  );
			Log.d(TAG, "other:"+e + " : " +  b.getMsg());
			m.put("line1", new Timestamp(b.getTime()).toString() );
			m.put("line2", e + " : " +  b.getMsg());
			//m.put(TIME, new Timestamp(b.getTime()).toString());
			//m.put(MSG, b.getMsg());
			//m.put(EMO, e);
			
			newList.add(m);
		}
		adapter = new SimpleAdapter(this, newList, R.layout.item_two, FROM, TO); //item_two
		setListAdapter(adapter);
		
	}
	
	private void showUpdatesCursor(Cursor cursor){
		startManagingCursor(cursor);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, FROM, TO);
		
		setListAdapter(adapter);
		
		
	}
}
