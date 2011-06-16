package org.jbs.happysad;

import static android.provider.BaseColumns._ID;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.TIME;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	private static String[] FROM = { _ID, LAT, LONG, EMO, MSG, TIME, };
	private static String ORDER_BY = TIME + " DESC";
	private HappyData updates;
	int emotion = -1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
		Intent sender = getIntent();
		String extradata = sender.getExtras().getString("Clicked");
		TextView t = (TextView)findViewById(R.id.more_text);
		t.setText(extradata);
		emotion = sender.getExtras().getInt("Emotion");
		
		
		
		
		EditText textField = (EditText)findViewById(R.id.more_textbox);
		textField.setOnKeyListener(this);
		updates = new HappyData(this);
		
		
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
			
			try {
				saveUpdate(userstring); 
		        
			} finally {
				updates.close(); 
			}


;			i.putExtra("textboxmessage", userstring);
			Log.d(TAG, "adding " + userstring +	" to intent");
			startActivity(i);		
			break;
		}
	}
	
	private void saveUpdate(String msg){
		SQLiteDatabase db = updates.getWritableDatabase();
		ContentValues values = basicValues(emotion);
		values.put(MSG, msg);
		db.insertOrThrow(TABLE_NAME, null, values);
		Log.d(TAG, "saved update to db");
		updates.close();
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
	
	private ContentValues basicValues(int emo){
		//for basic updates	
			
			ContentValues values = new ContentValues();
		    values.put(TIME, System.currentTimeMillis());
		    //values.put(LAT, <latitude>);
		    //values.put(LONG, <longitude>);
		    values.put(EMO, emo);
		    return values;
		 
		}
}