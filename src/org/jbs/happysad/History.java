package org.jbs.happysad;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;


public class History extends ListActivity implements OnClickListener{
	
	private String TAG = "History";
	private HappyData dataHelper;
	//private static String[] FROM = { TIME, MSG, EMO,  };
	//private static int[] TO = { R.id.time, R.id.msg, R.id.emo, };
	private static int[] TO = {R.id.item_text1, R.id.item_text2 };
	private static String[] FROM = { "line1","line2" };	
	private SimpleAdapter adapter;
	StringBuilder result;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		View refreshButton = findViewById(R.id.refresh_button);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
	   
		ArrayList<HappyBottle> list = dataHelper.getMyHistory(); 
		showUpdates(list);
	}

	public void onClick(View v) {
		switch(v.getId()) {		
		case R.id.refresh_button:	
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
			String e = (b.getEmo()>0)?"Happy":"Sad";
			//Double lat = (double) b.getLat();
			//Double longi = (double) b.getLong();
			//getAddress(lat, longi);
			m.put("line1", e + " : " +  b.getMsg());
			m.put("line2", new Timestamp(b.getTime()).toLocaleString() );
			
			newList.add(m);
		}
		adapter = new SimpleAdapter(this, newList, R.layout.item_two, FROM, TO); //item_two
		setListAdapter(adapter);
		
	}
	/*
	public void getAddress(double lat, double longi){
        try{
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = 
                gcd.getFromLocation(lat, longi,100);
            if (addresses.size() > 0) {
                result = new StringBuilder();
                for(int i = 0; i < addresses.size(); i++){
                    Address address =  addresses.get(i);
                    int maxIndex = address.getMaxAddressLineIndex();
                    for (int x = 0; x <= maxIndex; x++ ){
                        result.append(address.getAddressLine(x));
                        result.append(",");
                    }               
                    result.append(address.getLocality());
                    result.append(",");
                    result.append(address.getPostalCode());
                    result.append("\n\n");
                }
            }
        }
        catch(IOException ex){
        }
    } */
	
}
