package org.jbs.happysad;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Creates Prompt Activity
 * @author HappySad
 */
public class Prompt extends Activity implements OnClickListener{
	
	private Syncer s;
	private Thread t;
	private int myID = 1;
	public static final String USER_DATA = "userdata";
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
    	
    	//Finds the submit_button view
    	View submitButton = findViewById(R.id.more_to_dash);
    	submitButton.setOnClickListener(this);
    	
    	s = new Syncer(myID, this);
    	t = new Thread(s);
    	t.start();
    	
    	final AccountManager manager = AccountManager.get(this);
	    final Account[] accounts = manager.getAccounts();   
	    
            if (accounts.length >=1){
            	String username = new String(accounts[0].name);
            	int UID = userNameHash(username);
            	SharedPreferences sp = getSharedPreferences(USER_DATA,0);
            	SharedPreferences.Editor editor = sp.edit();
            	editor.putLong("usernameint", UID);
            	editor.putString("usernameString", username); 
            	editor.commit();
            	
            	Context context = getApplicationContext();
            	String loginPrompt = getString(R.string.login_message);
              CharSequence loggedin = (loginPrompt +" " + sp.getString( "usernameString", ""));
            	Toast toast = Toast.makeText(context, loggedin, 1000);
            	toast.show();
            }
            else{
            	Context context = getApplicationContext();
            	String loginPrompt = getString(R.string.error_login_message);
            	CharSequence text = loginPrompt;
            	Toast toast = Toast.makeText(context, text, 1000);
            	toast.show();
            }
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
		case R.id.more_to_dash:
			startActivity(new Intent(this, GlobalMap.class));
			break;
		}
	}
	
	//Safes 
	public void onDestroy(){
		s.safeShutdown();
		super.onDestroy();
	}
	
	// here is the has function. it inputs the username and turns it into an integer.
    private Integer userNameHash(String username){
    	int id = 1;
   /* 	for  ( int j = 0; j <= username.length() - 1; j++){
    		  id= id*31+username.charAt(j);
   		}
    */return id;
    }
    private void addUID(long servUID){
    	SharedPreferences sp = getSharedPreferences(USER_DATA,0);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putLong("usernameint", servUID);
    }
    
    private boolean checkUID(Long servUID){
    	SharedPreferences sp = getSharedPreferences(Prompt.USER_DATA,0);
		if(sp.getLong( "usernameint", 0) != servUID){
			return false;
		}
		else{
			return true;
		}
    }
}
