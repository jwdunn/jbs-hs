package org.jbs.happysad;

import android.provider.BaseColumns;

/**
 * List for string constants that are used for database related classes 
 * @author HS
 */
public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "localhistory";

   //columns in the events database
   public static final String LAT = "lat";
   public static final String LONG = "long";
   public static final String EMO = "emo";
   public static final String MSG = "msg";
   public static final String TIME = "time";
   public static final String UID = "user_id";
   public static final String SYNC = "sync";
}
