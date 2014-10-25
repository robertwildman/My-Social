package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class Register extends Activity {

	public EditText ETUsername, ETName, ETEmail;
	public Button Upload;
	String[] Name;
	public String Username, Email , Firstname , Secondname,Facebookuser;
	public AmazonDynamoDBClient client;
	public Session session;
	public MessageDigest md;
	public HashMap<String, AttributeValue> item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		getfacebookname();
		
	}

	

	public void setup() {
		
		// Setting up the TextView and EditTexts
		ETUsername = (EditText) findViewById(R.id.etregusername);
		
		ETName = (EditText) findViewById(R.id.etregname);
		ETEmail = (EditText) findViewById(R.id.etregemail);
		Upload = (Button) findViewById(R.id.bregdone);
		
		//Gets info from facebook
		getFacebookinfo();
		// setting up the onClickListener
		Upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Starts the upload()
				try {

					Gather();
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		});

		// Setting up the aws
		
		client = Login.clientManager.DDB();
		
	}

	private void getFacebookinfo() {
		// TODO Auto-generated method stub
		session = Session.getActiveSession();
		
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                         ETUsername.setText(user.getUsername());
                    	 ETName.setText(user.getName());
                    }   
                }   
            }   
        }); 
        Request.executeBatchAsync(request);
	}

	public boolean check() {
		// Makes sure that the Username pick by user has not been taken before

		try {

			// Makes sure that it can get around the HTTP from android
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			// Sets up a value to be a Username
			AttributeValue userNoAttr = new AttributeValue().withS(ETUsername
					.getText().toString());
			HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
			keyMap.put("Username", userNoAttr);

			// Sets up therequest with the tablename and what Attributes it
			// would like to get
			GetItemRequest request = new GetItemRequest()
					.withTableName("NetworkDB").withKey(keyMap)
					.withAttributesToGet("Username");

			GetItemResult result = client.getItem(request);
			// Return true if their is no result back from the database this
			// making sure there is no Username taken
			if (result.getItem() == null) {
				return true;
			} else {
				return false;
			}

		} catch (AmazonServiceException ex) {

		}
		return false;

	}
	public boolean checkfacebookuser(String user) {
		// Makes sure that the Username pick by user has not been taken before
    	// Setting up the aws
		
		client = Login.clientManager.DDB();
		try {

			// Makes sure that it can get around the HTTP from android
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			// Sets up a value to be a Username
			AttributeValue userNoAttr = new AttributeValue().withS(user);
			HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
			keyMap.put("Facebook", userNoAttr);

			// Sets up therequest with the tablename and what Attributes it
			// would like to get
			GetItemRequest request = new GetItemRequest()
					.withTableName("NetworkDB").withKey(keyMap)
					.withAttributesToGet("Facebook","Username");

			GetItemResult result = client.getItem(request);
			// Return true if their is no result back from the database this
			// making sure there is no Username taken
			if (result.getItem() == null) {
				printItem(result.getItem());
				return true;
			} else {
				return false;
			}

		} catch (AmazonServiceException ex) {

		}
		return false;

	}
	public void printItem(Map<String, AttributeValue> attributeList) {
		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			if(attributeName.equalsIgnoreCase("Username"))
			{
				
				Username = value.toString();
				setusername();
				startActivity(new Intent(getBaseContext(), MainActivity.class));
			}
		}
	}
	
	private void getfacebookname() {
	//Gets current user infomation and gets the id of user. 
		session = Session.getActiveSession();
		if(session != null)
		{
			Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	                // If the response is successful
	                if (session == Session.getActiveSession()) {
	                    if (user != null) {
	                    	Facebookuser = user.getUsername();
	                    	if(checkfacebookuser(user.getUsername()) == false)
	                    	{
	                    		setup();
	                    	}
	                    	 
	                    	
	                        
	                    }   
	                }   
	            }   
	        }); 
	        Request.executeBatchAsync(request);
		}
		//See if account is all ready made by this name if so it go back to made activity 
		
     }
		
	



	public void toast(String msg) {

		Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
	}

	public void Gather() throws Exception {
		// If it gets the ok from check it will upload the new Username and
		// Password(MD5)
		if (ETUsername.getText().length() == 0) {
			toast("Please Enter a Username");

		} else if (ETEmail.getText().length() == 0) {
			toast("Please Enter a Email Address");
		} else if (ETName.getText().length() == 0)
		{
			toast("Please enter your full name");
		}
		else {

			// Getting the Username and Email from the EditTexts
			Username = ETUsername.getText().toString();
			Name = ETName.getText().toString().split(" ");
			if(Name.length > 2 || Name.length < 2)
			{
				toast("Format First name SPACE Second Name");
			}
			else 
			{
			Firstname = Name[0];
			Secondname = Name[1];
			Email = ETEmail.getText().toString();

		
			// Making sure that the Username has not be taken
			if (check() == true) {
				toast("Creating your account on My-Social");
				// Start Login Screen
				upload();
				setusername();
				//Will tell the main acitivty to open the newprofile class so that the user can make account options. 
				Intent i = new Intent(getBaseContext(),MainActivity.class);
				i.putExtra("Registered", false);
				startActivity(i);
				
			} else if (check() == false) {
				// Gives error as the Username will be taken
				toast("Username has been taken please try a new one");
			
					}
			}
		}

	}

	public void upload() {
		// Start the upload to the LoginDB Database
		item = new HashMap<String, AttributeValue>();
		// Makes sure it can get pass the HTTP of android
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// Setting up the values of UserName and Password
		AttributeValue userAV = new AttributeValue().withS(Username);
		AttributeValue EmailAV = new AttributeValue().withS(Email);
		AttributeValue FirstnameAV = new AttributeValue().withS(Firstname);
		AttributeValue SecondnameAV = new AttributeValue().withS(Secondname);
		AttributeValue FacebookAV = new AttributeValue().withS(Facebookuser);
		// Adds the Collum name to the Vaule
		item.put("Username", userAV);
		item.put("Email",EmailAV );
		item.put("First Name", FirstnameAV);
		item.put("Second Name", SecondnameAV);
		item.put("Facebook", FacebookAV);
		
		
		// Starts the Upload to the Database name(LoginDB)
		PutItemRequest request = new PutItemRequest("NetworkDB", item);
		client.putItem(request);
	}

	public void setusername() {
		// Makes the String into a file
		File f = getBaseContext().getFileStreamPath("Myinfo");
		// Checks if the file is existing and If true it Deletes it(Re
		// write it) and then writes the Username then a new line the user's Facebook username into it.
		if (f.exists() == true) {
			f.delete();

			try {

				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(openFileOutput("Myinfo",
								Context.MODE_PRIVATE)));

				writer.write(Username);
				writer.newLine();
				writer.write(Facebookuser);
				writer.flush();
				writer.close();

			} catch (IOException ioe) {

			}

		} else {

			try {

				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(openFileOutput("Myinfo",
								Context.MODE_PRIVATE)));

				writer.write(Username);
				writer.newLine();
				writer.write(Facebookuser);
				writer.flush();
				writer.close();

			} catch (IOException ioe) {

			}

		}

	}



}
