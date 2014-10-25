package com.wildapps.my_social;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

public class Login extends Activity {

	public EditText ETUsername, ETPassword;
	public Button Register, Login;
	public static String PasswordMD5, Username, Password;
	public AmazonDynamoDBClient client;
	public AWSCredentials credentials;
	public MessageDigest md;
	public HashMap<String, AttributeValue> item;

	protected Button fbButton;
	protected TextView introText;

	private static final String LOG_TAG = "Login";
	
	public static AmazonClientManager clientManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.loginstyle);
		setContentView(R.layout.login);
		Log.i(LOG_TAG, "Started login activity");
		registercheck();
		
		
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			clientManager = new AmazonClientManager( getSharedPreferences("com.wildapps.my_social", Context.MODE_PRIVATE ), bundle);
		} catch (NameNotFoundException e) {
			displayErrorAndExit("Unable to load application bundle: " + e.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Login.this.finish();
		}
	}



	public void wireButtons() {
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
            	startActivityForResult(new Intent(Login.this, FacebookLogin.class), 1);}
        });
        
	}
	protected void displayErrorAndExit( String msg ) {
        AlertDialog.Builder confirm = new AlertDialog.Builder( this );
        if ( msg == null ) { 
            confirm.setTitle("Error Code Unknown" );
            confirm.setMessage( "Please review the README file." );
        } 
        else {
            confirm.setTitle( "Error" );
            confirm.setMessage( msg + "\nPlease review the README file."  );
        }

        confirm.setNegativeButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int which ) {
            	Login.this.finish();
            }
        } );
        confirm.show();                
    }
	
	 private void registercheck() {
			//See if there is a my-info file
	    	
	    	File f = getBaseContext().getFileStreamPath("Myinfo");
	    	if(f.exists() == false)
	    	{
	    		//User has just login in.
	    		//Start register class and check username. 
	    		fbButton = (Button) findViewById(R.id.bloginb1);
	    		wireButtons();
	    	}
	    	else
	    	{
	    		//User has been login in
	    		startActivityForResult(new Intent(Login.this, FacebookLogin.class), 1);
	    	}
			
		}

	public final boolean isInternetOn() {
		ConnectivityManager connec = null;
		connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec != null) {
			NetworkInfo result = connec
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (result != null && result.isConnectedOrConnecting()) {
				;
			}
			return true;
		}
		return false;
	}

}
