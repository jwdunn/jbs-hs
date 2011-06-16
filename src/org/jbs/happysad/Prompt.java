package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.TextView;
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
  	  
  	  Intent sender = getIntent();
  //	  TextView t = (TextView)findViewById(R.id.welcome_title);
  	  
  	//  String extradata = "\nwelcome!";
  	  

  	  try {
  	  	Log.d(TAG, "getting data from previous intent: bundle");
  	  	Bundle b = sender.getExtras();
  	  	Log.d(TAG, "getting data from previous intent: db");
  
  	
  	  //	extradata = b.getString("textboxmessage");
  	  	
  	  	
  	  	
  	  	
  	  }
  	  catch (Exception e) {
  	  		//do nothing
  	  		Log.d(TAG, "no worries - the first time you run this activity of course you will have no DB to pull from");
  	  		//no worries - the first time you run this activity of course you will have no extra data.
  	  		Log.d(TAG, e.toString());
  	  }
  	  finally{
  	  	//t.append("\n"+ extradata);
  	  }
  	  
  }
		public void onClick(View v) {
		
			Log.d(TAG, "clicked" + v.getId());
			System.out.println(TAG + "clicked" + v.getId());
			switch(v.getId()) {
			case R.id.happy_button:
				Log.d(TAG, "case" + v.getId()); 
				Intent i = new Intent(this, More.class);
				i.putExtra("Clicked", "Happy");
				startActivity(i);
				break;
			case R.id.sad_button:
				Log.d(TAG, "case" + v.getId());
				Intent j = new Intent(this, More.class);
				j.putExtra("Clicked", "Sad");
				startActivity(j);
				break;
			
			}
		}
}