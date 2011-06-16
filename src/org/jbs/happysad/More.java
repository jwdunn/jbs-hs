package org.jbs.happysad;
//joseph is my names
import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.os.Bundle;

public class More extends Activity implements OnKeyListener, OnClickListener{
	private static final String TAG = "there's more screen";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
		Intent sender = getIntent();
		String extradata = sender.getExtras().getString("Clicked");
		TextView t = (TextView)findViewById(R.id.more_text);
		t.setText(extradata);
		
		EditText textField = (EditText)findViewById(R.id.more_textbox);
		textField.setOnKeyListener(this);

		
		
		View submitButton = findViewById(R.id.more_to_dash);
		submitButton.setOnClickListener(this);
	}
 
	public void onClick(View v) {
		Log.d(TAG, "clicked" + v.getId());
		System.out.println(TAG + "clicked" + v.getId());
		switch(v.getId()) {
		case R.id.more_to_dash:
			Intent i = new Intent(this, Dashboard.class);
			
			String userstring = 
				(
						(TextView)
						findViewById(R.id.more_textbox)
				).getText().toString();
			
			i.putExtra("textboxmessage", userstring);
			Log.d(TAG, "adding " + userstring +	" to intent");
			startActivity(i);		
			break;
		}
	}
		
	//got following code from :http://stackoverflow.com/questions/2004344/android-edittext-imeoptions-done-track-finish-typing
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
	    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	        (keyCode == KeyEvent.KEYCODE_ENTER))
	    {
	       // Done pressed!  Do something here.
	    	EditText t = (EditText) v;
	    	Log.d(TAG, "text entered: " + t.getText() );
	    	this.onClick(findViewById(R.id.more_to_dash));
	    	//Intent i = new Intent(this, prompt.class);
			//startActivity(i);
	    	
	    	
	    }
	    // Returning false allows other listeners to react to the press.
	    return false;
	}
}