package org.jbs.happysad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbs.happysad.ChartOverall;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ChartList extends ListActivity {
	private ChartInterface[] mCharts = new ChartInterface[] { new ChartOverall(), new ChartYear(), new ChartMonth(), new ChartDay() };
	private String[] mMenuText;
	private String[] mMenuSummary;
	protected ArrayList<Integer> chartline;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//int length = mCharts.length;
		mMenuText = new String[4];
		mMenuSummary = new String[4];
		
  	  mMenuText[0] = "Overall";
  	  mMenuSummary[0] = "Click here to see your overall happiness %!";
  	  
  	  mMenuText[1] = "Year";
	  mMenuSummary[1] = "Click here to see your happiness % for this year!";
	  
	  mMenuText[2] = "Month";
  	  mMenuSummary[2] = "Click here to see your happiness % for this month!";
  	  
  	  mMenuText[3] = "Day";
	  mMenuSummary[3] = "Click here to see your happiness % for today!";
	  
  	  setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
  			  new String[] { ChartInterface.NAME, ChartInterface.DESC }, new int[] { android.R.id.text1,
            android.R.id.text2 }));
	}

	private List<Map<String, String>> getListValues() {
		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
	    int length = mMenuText.length;
	    for (int i = 0; i < length; i++) {
	    	Map<String, String> v = new HashMap<String, String>();
	    	v.put(ChartInterface.NAME, mMenuText[i]);
	    	v.put(ChartInterface.DESC, mMenuSummary[i]);
	    	values.add(v);
	    }
	    return values;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = null;
		intent = mCharts[position].execute(this);
		//intent = mCharts[position - 1].execute(this);
		//intent = mCharts[0].execute(this);
		startActivity(intent);
	}
}
