package org.jbs.happysad;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Creates Prompt Activity
 * @author HS
 */
public class Prompt extends Activity implements OnClickListener{
	
	private Syncer s;
	private Thread t;
	private int myID = 1;
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
    	
    	s = new Syncer(myID, this);
    	t = new Thread(s);
    	t.start();
    	
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
	
	//Safes 
	public void onDestroy(){
		super.onDestroy();
		s.safeShutdown();
	}
}
