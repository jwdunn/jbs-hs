package org.jbs.happysad;


import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class Updates extends Activity{
	
	private HappyData dataHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		dataHelper = new HappyData(this);
		ArrayList<HappyBottle> updates = getUpdates(); 
		showUpdates(updates); 
	    
	}
	
	private ArrayList<HappyBottle> getUpdates(){
		return dataHelper.getMyHistory();
		//this should CHANGE later
	}
	
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
			// More items go here (if any) ...
		}
	return false;
	}
}
