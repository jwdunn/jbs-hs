package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.util.Log;


public class Dashboard extends Activity implements OnClickListener{
	private static final String TAG = "happy sad prompt";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "created"); 
      super.onCreate(savedInstanceState);
      System.out.println(TAG + "started");
      setContentView(R.layout.dashboard);
      
      View updateButton = findViewById(R.id.update_button);
  	  updateButton.setOnClickListener(this);
  	  
  	  View histButton = findViewById(R.id.history_button);
 	  histButton.setOnClickListener(this);
 	  
 	  View mailButton = findViewById(R.id.mail_button);
	  mailButton.setOnClickListener(this);
 	 
  	  Intent sender = getIntent();
  	  TextView t = (TextView)findViewById(R.id.welcome_title);
  	  
  	  String extradata = "\nwelcome!";
  	  

  	  try {
  	  	Log.d(TAG, "getting data from previous intent: bundle");
  	  	Bundle b = sender.getExtras();
  	  	Log.d(TAG, "getting data from previous intent: extradata");
  	  	Log.d(TAG, b.getString("textboxmessage"));
  	  	extradata = b.getString("textboxmessage");
  	  	String happysaddata = b.getString("promptmessage");
  	  	
  	  	
  	  }
  	  catch (Exception e) {
  	  		//do nothing
  	  		Log.d(TAG, "no worries - the first time you run this activity of course you will have no extra data.");
  	  		//no worries - the first time you run this activity of course you will have no extra data.
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
			/*case R.id.mail_button:
				Log.d(TAG, "case" + v.getId());
				//Intent k = new Intent(this, Updates.class);
				startActivity(k);
				break;
			*/
			}
		}
}
