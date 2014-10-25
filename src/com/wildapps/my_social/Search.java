package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.s3.AmazonS3Client;

public class Search extends Fragment {
	public String Result, Username, Maintext;
	public EditText ETUsername;
	public Button searchbutton;
	public TextView Search, Main;
	static AmazonDynamoDBClient client;
	public ArrayList<String> Searchback;
	public ListView LVmain;
	public ImageView imgPreview;
	public File photo;
	protected String _path;
	public View InputFragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		InputFragmentView = inflater.inflate(R.layout.search, container, false);
		_path = Environment.getExternalStorageDirectory()
				+ "/images/make_machine_example.jpg";
		setup();
		return InputFragmentView;

	}

	public void setup() {

		// EditText
		ETUsername = (EditText) InputFragmentView
				.findViewById(R.id.etsearchuser);

		// ListView
		LVmain = (ListView) InputFragmentView.findViewById(R.id.lvsearchmain);

		// Button
		searchbutton = (Button) InputFragmentView
				.findViewById(R.id.bsearchdone);
		// Main = (TextView)findViewById(R.id.tvsearchmain);
		// Setting up arraylist
		Searchback = new ArrayList<String>();

		searchbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ETUsername.length() == 0) {
					toast("Please enter a Username!");
				} else {
					toast("Starting search");
					search();
				}
			}
		});

		LVmain.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Fragment fragment = new Profile();
				FragmentManager fragmentManager = getFragmentManager();

				getActivity().setTitle(arg0.getItemAtPosition(arg2).toString());
				Bundle arguments = new Bundle();
				arguments.putString("Username", arg0.getItemAtPosition(arg2)
						.toString());
				fragment.setArguments(arguments);
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();

			}
		});

	}

	public void search() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			Username = ETUsername.getText().toString();
			client = Login.clientManager.DDB();
			AttributeValue userNoAttr = new AttributeValue().withS(Username);
			HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
			keyMap.put("Username", userNoAttr);

			GetItemRequest request = new GetItemRequest()
					.withTableName("NetworkDB").withKey(keyMap)
					.withAttributesToGet("Username");

			GetItemResult result = client.getItem(request);
			if (result.getItem() == null) {
				Searchback.add("No Username is found");
			} else {
				Searchback.add(Username);
			}

		} catch (AmazonServiceException ex) {
			ex.printStackTrace();
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, Searchback);
		LVmain.setAdapter(arrayAdapter);

	}

	public void printItem(Map<String, AttributeValue> attributeList) {

		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			Searchback.add(attributeName + "  " + value.getS().toString());

			// System.out.println(attributeName + " "
			// + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
			// + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
			// + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
			// + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
			// + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
			// + (value.getBS() == null ? "" : "BS=[" + value.getBS() +
			// "] \n"));
		}

	}

	public void toast(String msg) {
		Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG)
				.show();
	}

	// Gets Username from file
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
					return receiveString;

				}

				inputStream.close();

			}
		} catch (FileNotFoundException e) {
			Log.e("My-Social", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("My-Social", "Can not read file: " + e.toString());
		}
		return "";
	}

}
