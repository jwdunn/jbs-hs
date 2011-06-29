package org.jbs.happysad;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;

public class Stats extends Activity implements OnClickListener {

	private HappyData data1;
	
	/**
	 * Initializes activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		
		data1 = new HappyData(this);
		ArrayList<Integer> counters = new ArrayList<Integer>(); 
		counters.add(data1.getSadcount());
		counters.add(data1.getHappycount());
		showUpdates(counters);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	

	private void showUpdates(ArrayList<Integer> a){
		// Stuff them all into a big string
    	StringBuilder builder = new StringBuilder( 
          "Stats:\n");
	    for (int i = 0; i < 2; i++) { 
	       // Could use getColumnIndexOrThrow() to get indexes
	       if (i == 0){
	    	   
	    	   builder.append("Total SAD's = ");
	    	   builder.append(a.get(i));
	    	   builder.append("\n");
	       
	       } else {
	    	   
	    	   builder.append("Total HAPPY's = ");
	    	   builder.append(a.get(i));
	    	   builder.append("\n");	    	   
	       }
	    }
	    // Display on the screen
	    TextView text = (TextView) findViewById(R.id.text); 
	    text.setText(builder);
	}
}