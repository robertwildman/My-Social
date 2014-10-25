package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Profile extends Fragment {

	public ArrayList<String> ALNetworks, ALNetworkname;
	public TextView TVUsername, TVNetuser, TVBio;
	public GridView LVNetworks;
	public String Username, reguser;
	public String[] Networks , Networksuser;
	public HashMap<String, AttributeValue> item;
	public AmazonDynamoDBClient client;
	public static Activity at;
	public AmazonS3Client s3client;
	public static View InputFragmentView;
	public AWSCredentials credentials;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		InputFragmentView = inflater
				.inflate(R.layout.profile, container, false);
		at = getActivity();
		Bundle extras = getArguments();
		if (extras != null) {
			String datas = extras.getString("Username");
			if (datas != null) {
				// do stuff
				Username = datas;
			}
		} else {
			getusername();
		}
		setup();

		return InputFragmentView;
		// Checks to make sure it got something from the intent and also what to
		// load up in the profile

	}

	public void arraysetup()
	{
		String[] TempNetworks = {"Bio","Facebook", "Twitter", "Instagram", "Email",
				"Website 1", "Snapchat", "Tumblr", "Google +", "Flickr",
				"Bebo", "Last FM", "Spotify", "Foursquare", "GitHub",
				"Stackoverflow", "Youtube", "Linkedin", "Vimeo", "Pinterest",
				"Blogger", "Yahoo", "Skype", "Soundcloud", "Reddit",
				"KickStarter", "Twitch", "Photobucket", "Vine", "Website 2",
				"Website 3", "Website 4", "Website 5", "We Heart It", "Ustream" };
		Networks = new String[TempNetworks.length];
		Networksuser = new String[TempNetworks.length];
		Networks = TempNetworks;
		
	}

	public void setup() {
		arraysetup();
		// Will have the TextView setup here
		TVUsername = (TextView) InputFragmentView
				.findViewById(R.id.tvhpusername);
		TVBio = (TextView) InputFragmentView.findViewById(R.id.tvhpbio);
		LVNetworks = (GridView) InputFragmentView.findViewById(R.id.LVNetworks);

		//AMazon DDB
		client = Login.clientManager.DDB();
		// AWS S3
		s3client = Login.clientManager.s3();
		
		LVNetworks.setAdapter(null);
		setprofilepictrue();
		getNetworkuser();

	}

	public void setprofilepictrue() {
		new DownloadProfilepic().execute();
	}

	private class DownloadProfilepic extends AsyncTask<Void, Void, Bitmap> {

		public Bitmap bitmap;

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			S3Object object = s3client.getObject(new GetObjectRequest(
					"Profile_Pictures", Username));
			InputStream objectData = object.getObjectContent();
			bitmap = BitmapFactory.decodeStream(objectData);
			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap bm) {

			ImageView profilepic = (ImageView) InputFragmentView
					.findViewById(R.id.ivprofile);
			profilepic.setImageBitmap(Bitmap.createScaledBitmap(bm, 350, 350,
					false));
		}
	}

	public void display() {

		// Set up listView
		TVUsername.setText(Username);
		String[] adapternetwork = ALNetworks.toArray(new String[ALNetworks.size()]);
		String[] adapternetworkuser = ALNetworkname
				.toArray(new String[ALNetworkname.size()]);

		ProfileAdapter adapter = new ProfileAdapter(getActivity()
				.getBaseContext(), adapternetwork, adapternetworkuser);
		LVNetworks.setAdapter(adapter);
	}

	public String getusername() {
		// Picks it up from file
		try {
			InputStream inputStream = getActivity().getBaseContext()
					.openFileInput("Myinfo");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = bufferedReader.readLine();

				while (receiveString != null) {
					Username = receiveString;
					reguser = receiveString;
					receiveString = bufferedReader.readLine();

				}

				inputStream.close();

			}
		} catch (FileNotFoundException e) {
			Log.e("My-Social", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("My-Social", "Can not read file: " + e.toString());
		}
		return Username;
	}

	public void getNetworkuser() {

		AttributeValue userNoAttr = new AttributeValue().withS(Username);
		// Starts a HashMap for the Request
		HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
		// Adding the Collum the data will be in for the search
		keyMap.put("Username", userNoAttr);
		// Making the rquest and adding the table name and also what value we
		// want back from the database.
		GetItemRequest request = new GetItemRequest()
				.withTableName("NetworkDB").withKey(keyMap);
		// Gets the request from the database back and then see if they is a
		// request but if they is not then it wil not cause and error and return
		// false
		GetItemResult result = client.getItem(request);
		if (result.getItem() == null) {
			// Starts the frontpage
			Fragment fragment = new Frontpage();
			FragmentManager fragmentManager = getFragmentManager();

			getActivity().setTitle("Home");

			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			toast("Profile not found");

		} else {
			// Starts the PrintItem sub to get the Username and the Password
			// data from the database
			printItem(result.getItem());
			display();

		}
	}

	public void toast(String msg) {
		// A basic setup for a toast making it easy to display them in code
		Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG)
				.show();
	}

	public void printItem(Map<String, AttributeValue> attributeList) {

		ALNetworks = new ArrayList<String>();
		ALNetworkname = new ArrayList<String>();
		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			// Will check to see if the attributeName is equal to any of the
			// information we need then save the value it gets back.]
			// New networks need to be add onto the loop with else if
			loop:
				for(int i = 0; i < Networks.length; i++)
				{
					if (attributeName.equalsIgnoreCase(Networks[i])) {
						Networksuser[i] = value.getS();
						break loop;
					}
				}

		}
		for(int i = 0; i < Networks.length;i++)	
		{
			addtousernetworks(Networks[i], Networksuser[i]);
		}

	}

	public void addtousernetworks(String network, String networkus) {
		if(network != null && networkus != null )
		{
		if (network.length() > 0 && networkus.length() > 0) {

			ALNetworks.add(network);
			ALNetworkname.add(networkus);
		}
		}
	}

}
