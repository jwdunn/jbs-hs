package org.jbs.happysad;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Provides a mechanism to store and access configuration data in a hierarchical way
 * @author HS
 */
public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
