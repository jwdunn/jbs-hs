package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class More extends Activity {

	 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.more);
     Intent sender = getIntent();
     String extradata = sender.getExtras().getString("Clicked");
     TextView t = (TextView)findViewById(R.id.more_text);
     t.setText(extradata);
 }

}
