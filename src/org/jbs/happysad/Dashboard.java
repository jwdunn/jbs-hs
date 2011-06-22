package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.util.Log;


public class Dashboard extends Activity implements OnClickListener{
	private static final String TAG = "Dashboard";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "created"); 
    	super.onCreate(savedInstanceState);
      
    	setContentView(R.layout.dashboard);
      
    	View updateButton = findViewById(R.id.update_button);
    	View histButton = findViewById(R.id.history_button);
    	TextView t = (TextView)findViewById(R.id.welcome_title);
    	
    	updateButton.setOnClickListener(this);
    	histButton.setOnClickListener(this);
 	  
  	  	Intent sender = getIntent();
  	  	String extradata = "\nwelcome!";
  	  

  	  	try {
			Log.d(TAG, "getting data from previous intent.");
			Bundle b = sender.getExtras();
			extradata = b.getString("textboxmessage");
  	  	}
  	  	catch (Exception e) {  
  	  		Log.d(TAG, e.toString());
  	  	}
  	  	finally{
  	  		t.append("\n"+ extradata);
  	  	}
  	  
    }
	public void onClick(View v) {
	
		Log.d(TAG, "clicked" + v.getId());
		System.out.println(TAG + "clicked" + v.getId());
		switch(v.getId()) {
		
		case R.id.update_button:
			Log.d(TAG, "case" + v.getId()); 
			Intent i = new Intent(this, Prompt.class);
			i.putExtra("Clicked", "Happy");
			startActivity(i);
			break;
		
		case R.id.history_button:
			Log.d(TAG, "case" + v.getId());
			Intent j = new Intent(this, Updates.class);
			startActivity(j);
			break;
		}
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

