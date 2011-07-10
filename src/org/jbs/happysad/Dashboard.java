package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Creates the Dashboard Activity, which is composed of multiple buttons that go to other Activities
 * @author HappySad
 */
public class Dashboard extends Activity implements OnClickListener{
	
	/**
	 * Initializes activity
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      	setContentView(R.layout.dashboard);
      
		//Finds the update_button view
		View updateButton = findViewById(R.id.update_button);
		updateButton.setOnClickListener(this);

		//Finds the history_button view
  	  	View histButton = findViewById(R.id.history_button);
  	  	histButton.setOnClickListener(this);
  	  	
  	  	//Finds the my_map view
  	  	View myButton = findViewById(R.id.my_map);
  	  	myButton.setOnClickListener(this);

  	  	//Finds the global_map view
  	  	View globalButton = findViewById(R.id.global_map);
  	  	globalButton.setOnClickListener(this);
  	  	
  	  	//Finds the chart_button view
  	  	View chartButton = findViewById(R.id.chart_button);
  	  	chartButton.setOnClickListener(this);  	
	}
    
    /**
     * Invoked when a view is clicked
     */
	public void onClick(View v) {
		switch(v.getId()) {

		case R.id.update_button:
			startActivity(new Intent(this, Prompt.class));
			break;

		case R.id.history_button:
			startActivity(new Intent(this, History.class));
			break;
		
		case R.id.my_map:
			startActivity(new Intent(this, MyMap.class));
			break;
		
		case R.id.global_map:
			startActivity(new Intent(this, GlobalMap.class));
			break;
			
		case R.id.chart_button: 
			startActivity(new Intent(this, ChartList.class));
    		break;
		}
	}
	
	/**
	 * Creates a setting menu
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
		case R.id.exit:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			finish();
			startActivity(intent);
		
		}
		return false;
	}
}
