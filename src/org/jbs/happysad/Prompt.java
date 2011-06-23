package org.jbs.happysad;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.util.Log;

/**
 * Creates Prompt Activity
 * @author HS
 */
public class Prompt extends Activity implements OnClickListener{
	//for debugging purposes, delete after debugging.
	//private static final String TAG = "happy sad prompt";

	/**
	 * Initializes activity
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
    	//Finds the happy_button view
    	View happyButton = findViewById(R.id.happy_button);
    	happyButton.setOnClickListener(this);
    	
    	//Finds the sad_button view
    	View sadButton = findViewById(R.id.sad_button);
    	sadButton.setOnClickListener(this);
  	  
    }
    
    /**
     * Invoked when a view is clicked
     */
	public void onClick(View v) {
		Intent i = new Intent(this, More.class);

		switch(v.getId()) {		
		case R.id.happy_button:	
			i.putExtra("Clicked", "Happy");
			i.putExtra("Emotion", 1);
			startActivity(i);
			break;
		case R.id.sad_button:
			i.putExtra("Clicked", "Sad");
			i.putExtra("Emotion", 0);
			startActivity(i);
			break;
		}
	}
		
	/**
	 * Creates setting menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Invoked when a option is clicked
	 */
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
