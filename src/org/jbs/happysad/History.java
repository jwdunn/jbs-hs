package org.jbs.happysad;

import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TIME;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;


public class History extends ListActivity implements OnClickListener{
	

	private HappyData dataHelper;
	private static String[] FROM = { TIME, MSG, EMO,  };
	private static int[] TO = { R.id.time, R.id.msg, R.id.emo, };
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		View refreshButton = findViewById(R.id.refresh_button);
    	refreshButton.setOnClickListener(this);

		dataHelper = new HappyData(this);
	    showUpdatesCursor(dataHelper.getMyHistoryCursor());
	}

	public void onClick(View v) {
		switch(v.getId()) {		
		case R.id.refresh_button:	
		    showUpdatesCursor(dataHelper.getMyHistoryCursor());
			dataHelper.syncDown();
			break;
		}
	}
	
	private void showUpdatesCursor(Cursor cursor){
		startManagingCursor(cursor);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, FROM, TO);
		setListAdapter(adapter);
		
	}
}
