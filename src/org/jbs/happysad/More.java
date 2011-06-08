package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class More extends Activity implements OnClickListener{
	private static final String TAG = "there's more screen";
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.more);
			Intent sender = getIntent();
			String extradata = sender.getExtras().getString("Clicked");
			TextView t = (TextView)findViewById(R.id.more_text);
			t.setText(extradata);
     

			View submitButton = findViewById(R.id.more_to_dash);
			submitButton.setOnClickListener(this);
	 }
	 
		@Override
		public void onClick(View v) {
			Log.d(TAG, "clicked" + v.getId());
			System.out.println(TAG + "clicked" + v.getId());
			switch(v.getId()) {
			case R.id.more_to_dash:
				Intent i = new Intent(this, prompt.class);
				startActivity(i);
				break;
		}
	 }
}