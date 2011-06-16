package org.jbs.happysad;

/***
 * Modified code from Hello Android
***/

import android.provider.BaseColumns;
import android.net.Uri;

public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "localhistory";

  
	   
   // Columns in the Events database

   public static final String LAT = "lat";
   public static final String LONG = "long";
   public static final String EMO = "emo";
   public static final String MSG = "msg";
   public static final String TIME = "time";
}
