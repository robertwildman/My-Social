package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class EditProfile extends Fragment {

	public EditText netuser, etdialog;
	public Button add, finish, bdialog;
	public Spinner spdialog;
	public GridView LVnetworks;
	public AlertDialog alertDialog;
	public ArrayList<String> ALNetworks, ALNetworkuser;
	public int arraysize;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static int RESULT_LOAD_IMAGE = 2;
	public String[] Networks, Networksuser;
	public String Username;
	public Integer netarraysize, socialnumber;
	public HashMap<String, AttributeValue> item;
	static AmazonDynamoDBClient client;
	public View InputFragmentView;

	// When the screen first loads
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Give the user a dialog so they know it is loading

		InputFragmentView = inflater.inflate(R.layout.editprofile, container,
				false);
		setHasOptionsMenu(true);
		setup();
		return InputFragmentView;

	}

	// Setup of the whole project
	public void setup() {
		// Used for when uploading to aws
		item = new HashMap<String, AttributeValue>();
		// Making sure http request can bypass firewall
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Setting up ArrayList
		ALNetworks = new ArrayList<String>();
		ALNetworkuser = new ArrayList<String>();
		// Buttons
		// TextViews
		arraysetup();
		// Spinners

		// ListView
		LVnetworks = (GridView) InputFragmentView
				.findViewById(R.id.lveditnetworks);

		// Starting up subs
		// Sets up the connections to aws
		awssetup();
		// Gets the username from file set at login
		getusername();
		// Gets all the data held on about the user
		getNetworkuser();
		// Sets the ListView
		ListViewsetup();
	}

	// The setup of aws
	public void awssetup() {

		client = Login.clientManager.DDB();
	}

	// Sets up the String Arrays
	public void arraysetup() {
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

	// Gets username from file
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

	// Gets everything ready to be uploaded
	public void upload() throws IOException {

		AttributeValue userNo = new AttributeValue().withS(Username);
		item.put("Username", userNo);
		PutItemRequest request = new PutItemRequest("NetworkDB", item);
		client.putItem(request);

	}

	// Having the Network current username sent back
	public String setnetworkuser(String network) {
		// Setting up the EditText
		String NetworkUser = getnetworkusername(getvalidnetworkname(network));
		return NetworkUser;

	}

	// Adds to the Network and Network Usernames to an array
	public void addtousernetworks(String network, String networkus) {
		// Checks to make sure it is not empty
		if (TextUtils.isEmpty(networkus) == false) {

			// Adds to a arraylist for networks and networkuser
			ALNetworks.add(network);
			ALNetworkuser.add(networkus);
		}

	}

	// Gets the infomation from the database
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

		} else {
			// Starts the PrintItem sub to get the Username
			// data from the database
			printItem(result.getItem());
			addingeverythingtonetworks();
		}

	}

	// Sets up the ListViews
	public void ListViewsetup() {
		// Setting up of the array to be set of to the custom adapter
		final String[] adapternetwork = ALNetworks
				.toArray(new String[ALNetworks.size()]);
		final String[] adapternetworkuser = ALNetworkuser
				.toArray(new String[ALNetworkuser.size()]);

		EditProfileAdapter adapter = new EditProfileAdapter(getActivity()
				.getApplicationContext(), adapternetwork, adapternetworkuser);
		// Setting the adapter to the custom adapter
		LVnetworks.setAdapter(adapter);

		LVnetworks.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg) {

				// TODO Auto-generated method stub
				if (position == 0) {
					takepicture();
				} else {
					customdialog(adapter.getItemAtPosition(position).toString());
				}
			}
		});

	}

	// Sub is used when the user clicks to edit or add
	public void customdialog(String network) {
		final Dialog dialog = new Dialog(InputFragmentView.getContext());
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Adding Network");

		// Sets up the Dialog UI
		// Spinner
		spdialog = (Spinner) dialog.findViewById(R.id.spdialog);
		// EditText
		etdialog = (EditText) dialog.findViewById(R.id.eteditdialog);
		// Button
		bdialog = (Button) dialog.findViewById(R.id.bdialog);
		if (!network.equalsIgnoreCase("n/a")) {
			spdialog.setSelection(getIndex(spdialog, network));
		}
		// The spinner looking out for what has been selected
		spdialog.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v,
					int position, long id) {
				// On selecting a spinner item
				etdialog.setText(setnetworkuser(adapter.getItemAtPosition(
						position).toString()));
				String dialogname = adapter.getItemAtPosition(position)
						.toString();
				if (dialogname.contains("Website")) {
					etdialog.setHint("URL");
				} else if (dialogname.contains("Email")) {
					etdialog.setHint("Email Address");
				} else if (dialogname.contains("Bio")) {
					etdialog.setHint("Bio");
				} else {
					etdialog.setHint("Username");
				}
				bdialog.setText("Add " + adapter.getItemAtPosition(position));

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Button Listener
		bdialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Adding username and network to array
				addnetworkuser(getvalidnetworkname(spdialog.getSelectedItem()
						.toString()), etdialog.getText().toString());

				addingeverythingtonetworks();
				// Closing the Dialog
				dialog.cancel();
				ListViewsetup();

			}
		});
		// Showing the dialog
		dialog.show();

	}

	public void addingeverythingtonetworks() {
		// Clears the Arraylists
		ALNetworks = new ArrayList<String>();
		ALNetworkuser = new ArrayList<String>();
		addtousernetworks("Profile", "profile");
		for (int i = 0; i < Networks.length; i++) {
			addtousernetworks(Networks[i], Networksuser[i]);
		}

	}

	// Saving of a Network Username
	public void addnetworkuser(String inNetwork, String text) {

		// Will pick off the network and have it add to the string
		loop: for (int i = 0; i < Networks.length; i++) {
			if (inNetwork.equalsIgnoreCase(Networks[i])) {
				Networksuser[i] = text;
				break loop;
			}
		}

	}

	// Sorting data from the Database
	public void printItem(Map<String, AttributeValue> attributeList) {
		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			// Will check to see if the attributeName is equal to any of the
			// information we need then save the value it gets back.
			loop: for (int i = 0; i < Networks.length; i++) {
				if (attributeName.equalsIgnoreCase(Networks[i])) {
					Networksuser[i] = value.getS();
					break loop;
				}
			}

		}
	}

	// Gets the right networkname from the spinner
	public String getvalidnetworkname(String spinnertext) {

		if (spinnertext.equalsIgnoreCase("Website (1)")) {
			return "Website1";
		} else if (spinnertext.equalsIgnoreCase("Website (2)")) {
			return "Website2";
		} else if (spinnertext.equalsIgnoreCase("Website (3)")) {
			return "Website3";
		} else if (spinnertext.equalsIgnoreCase("Website (4)")) {
			return "Website4";
		} else if (spinnertext.equalsIgnoreCase("Website (5)")) {
			return "Website5";
		} else {

			return spinnertext;
		}

	}

	// Adds the Networks to be sent to the server ready to be sent up
	public void addtoarraynetwork() {

		// Makes sure that if it it empty it will not be sent up to the database
		// and if it is filled then it will add it in

		loop: for (int i = 0; i < Networks.length; i++) {
			if (TextUtils.isEmpty(Networksuser[i]) == false) {
				AttributeValue awsnetwork1 = new AttributeValue()
						.withS(Networksuser[i]);
				item.put(Networks[i], awsnetwork1);
			}
		}

	}

	// Gets the network Username for that network
	public String getnetworkusername(String inNetwork) {

		loop: for (int i = 0; i < Networks.length; i++) {
			if (inNetwork.equalsIgnoreCase(Networks[i])) {
				return Networksuser[i];
			}
		}
		return inNetwork;

	}

	private int getIndex(Spinner spinner, String myString) {

		int index = 0;
		for (int i = 0; i < spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).equals(myString)) {
				index = i;
			}
		}
		return index;
	}

	// Works
	public void takepicture() {
		// TODO Auto-generated method stub

		final CharSequence[] items = { "Take Picture", "From Gallery" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Pick picture option");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);

				} else if (item == 1) {
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			}
		}).show();
	}

	// Works
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		getActivity();
		if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap bm = BitmapFactory.decodeFile(picturePath);
			bm = Bitmap.createScaledBitmap(bm, 400, 400, true);
			EditProfileAdapter.profile.setImageBitmap(bm);
			upload(bm);

		} else if (requestCode == 1) {
			Log.d("CameraDemo", "Pic saved");
			File f = new File(Environment.getExternalStorageDirectory()
					.toString());
			for (File temp : f.listFiles()) {
				if (temp.getName().equals("temp.jpg")) {
					f = temp;
					break;
				}
			}
			try {
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

				bm = BitmapFactory
						.decodeFile(f.getAbsolutePath(), btmapOptions);
				bm = Bitmap.createScaledBitmap(bm, 400, 400, true);
				EditProfileAdapter.profile.setImageBitmap(bm);
				upload(bm);
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// Updated and works
	public void upload(Bitmap bm) {
		// AWS S3

		AmazonS3Client s3client = null;

		s3client = Login.clientManager.s3();

		FileOutputStream out = null;
		try {
			File f = getActivity().getBaseContext().getFileStreamPath(
					"My-Social Profile Picture");
			out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}

		File f = getActivity().getBaseContext().getFileStreamPath(
				"My-Social Profile Picture");
		String fpath = f.getAbsolutePath();
		PutObjectRequest por = new PutObjectRequest("Profile_Pictures",
				getusername(), new java.io.File(fpath));
		por.withCannedAcl(CannedAccessControlList.AuthenticatedRead);
		s3client.putObject(por);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.main, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// handle item selection

		switch (item.getItemId()) {

		case R.id.action_add:
			customdialog("n/a");
			return true;
		case R.id.action_finished:
			try {

				addtoarraynetwork();
				upload();
				Intent i = new Intent(getActivity().getBaseContext(),MainActivity.class);
				i.putExtra("Page Number", 0);
				startActivity(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		default:

			return super.onOptionsItemSelected(item);

		}

	}
}
