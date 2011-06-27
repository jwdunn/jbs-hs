package org.jbs.happysad;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class History extends Activity implements OnClickListener{

	private HappyData dataHelper;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		View refreshButton = findViewById(R.id.refresh);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
		ArrayList<HappyBottle> updates = getUpdates(); 
		showUpdates(updates); 

	}




	public void onClick(View v) {
		Intent i = new Intent(this, History.class);

		switch(v.getId()) {		
		case R.id.refresh:	
			dataHelper.syncDown();
			startActivity(i);
			break;

		}}






	/**
	 * Returns an ArrayList of HappyBottles of MyHistory
	 * @return
	 */
	private ArrayList<HappyBottle> getUpdates(){
		return dataHelper.getMyHistory();
	}

	/**
	 * Shows the ArrayList of HappyBottles on the Screen via a big string
	 * @param a
	 */
	private void showUpdates(ArrayList<HappyBottle> a){
		// Stuff them all into a big string
    	StringBuilder builder = new StringBuilder( 
          "Saved updates:\n");
	    for (HappyBottle b : a) { 
	       // Could use getColumnIndexOrThrow() to get indexes
	       builder.append(b.toString());
	       builder.append("\n");

	    }
	    // Display on the screen
	    TextView text = (TextView) findViewById(R.id.text); 
	    text.setText(builder);
	}
}