package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;


public class Prompt extends Activity implements OnClickListener{
	private static final String TAG = "happy sad prompt";


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "created"); 
      super.onCreate(savedInstanceState);
      System.out.println(TAG + "started");
      setContentView(R.layout.main);
      
      View happyButton = findViewById(R.id.happy_button);
  	  happyButton.setOnClickListener(this);
  	  View sadButton = findViewById(R.id.sad_button);
  	  sadButton.setOnClickListener(this);
  	  
  }
		public void onClick(View v) {

			Log.d(TAG, "clicked" + v.getId());
			System.out.println(TAG + "clicked" + v.getId());
			switch(v.getId()) {
			case R.id.happy_button:
				Log.d(TAG, "case" + v.getId()); 
				Intent i = new Intent(this, More.class);
				i.putExtra("Clicked", "Happy");
				i.putExtra("Emotion", 1);
				startActivity(i);
				break;
			case R.id.sad_button:
				Log.d(TAG, "case" + v.getId());
				Intent j = new Intent(this, More.class);
				j.putExtra("Clicked", "Sad");
				j.putExtra("Emotion", 0);
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
