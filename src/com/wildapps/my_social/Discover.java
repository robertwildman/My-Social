package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class Discover extends Fragment {

	public String Result, Username, Maintext;
	public EditText ETUsername;
	public GridView grid;
	public File myDir;
	public TextView Search, Main;
	public AmazonS3Client s3client;
	public ArrayList<String> allfilenames, svallfilenames;
	public ArrayList<String> Discoverback;
	public LayoutInflater mInflater;
	public ArrayList<Bitmap> allBitMap, svallBitMap;
	public ListView LVeditor, LVDiscover;
	public View InputFragmentView;

	// This class will be set up to deal with the nav drawers
	// Will have the following in the Nav Drawers
	// Search
	// My Profile
	// Discover
	// Logout
	// About Us
	// This will also deal with the ListView and the getting of data for them
	// Will connect up to the EditorDB and will display the approved data on it.
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		InputFragmentView = inflater.inflate(R.layout.frontpage, container,
				false);
		mInflater = inflater;
		setup();
		return InputFragmentView;
	}

	// Setup the whole program
	public void setup() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		File root = Environment.getExternalStorageDirectory();
		myDir = new File(root, "MySocial/cache/discover");
		myDir.mkdirs();
		// AWS S3
		s3client = Login.clientManager.s3();
		

		Discoverback = new ArrayList<String>();
		LVeditor = (ListView) InputFragmentView.findViewById(R.id.lvmaineditor);
		grid = (GridView) InputFragmentView.findViewById(R.id.grid);

		allfilenames = new ArrayList<String>();
		allBitMap = new ArrayList<Bitmap>();
		svallfilenames = new ArrayList<String>();
		svallBitMap = new ArrayList<Bitmap>();
		getfromfile();
		if(isNetworkAvaiable() == true)
		{
			getfromserver();
		}else
		{
			toast("No Internet, My-Social will work, Please try again when you have interent");
		}
		onclicklistener();

	}

	// When user clicks on the profile dealing with opening and closing
	public void onclicklistener() {
		// Opens profile discover page when clicked

	}

	public void getfromserver() {
		// Will collect the data from the servers

		new Loadusername().execute();

		// getfromfile();
	}

	public void getusernamesfromserver() {

	}

	public void savedata(ArrayList<Bitmap> bitmap, ArrayList<String> filenames) {
		// Saves the data to file
		;

		for (int i = 0; bitmap.size() < i; i++) {
			File picturefile = new File(myDir, filenames.get(i));

			if (picturefile.exists()) {
				picturefile.delete();
			}
			try {
				picturefile.createNewFile();
				FileOutputStream out = new FileOutputStream(picturefile);
				bitmap.get(i).compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void getfromfile() {
		// Will pick it up from the cache memory

		allfilenames.clear();
		allBitMap.clear();

		try {
			File usernamefile = new File(myDir, "Usernames");

			InputStream inputStream = new FileInputStream(usernamefile);
		
			if (inputStream != null) {
				;
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = bufferedReader.readLine();

				while (receiveString != null) {

					allfilenames.add(receiveString);
					
					receiveString = bufferedReader.readLine();

				}

				inputStream.close();

			}

		} catch (FileNotFoundException e) {
			Log.e("My-Social", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("My-Social", "Can not read file: " + e.toString());
		}
		display();
	}

	public void display() {
		// Display the data into the gridview.
		grid.setAdapter(null);
		grid.setAdapter(new HomepageAdapter());

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

	private class DownloadProfilepic extends AsyncTask<String, Void, Bitmap> {

		public Bitmap bitmap;
		public String fileusernames;
		public Integer position;

		public DownloadProfilepic(String fileusername, Integer i) {
			fileusernames = fileusername;
			position = i + 1;
		}

		@Override
		protected Bitmap doInBackground(String... fileusername) {
		
			S3Object object = s3client.getObject(new GetObjectRequest(
					"Profile_Pictures/Discover", fileusernames.toString()));
			InputStream objectData = object.getObjectContent();
			bitmap = BitmapFactory.decodeStream(objectData);
			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap bm) {
			svallBitMap.add(bm);
			saveimage(fileusernames, bm);
			if (position < svallfilenames.size()) {
				new DownloadProfilepic(svallfilenames.get(position), position)
						.execute();
			} else {
				getfromfile();
			}
		}
	}

	public void saveimage(String Filename, Bitmap bm) {
		File picturefile = new File(myDir, Filename);

		try {

			picturefile.createNewFile();
			FileOutputStream out = new FileOutputStream(picturefile);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public class HomepageAdapter extends BaseAdapter {

		public HomepageAdapter() {

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View result = mInflater.inflate(R.layout.grid_item3, parent, false);

			// Bind the data
			TextView subtitle = (TextView) result
					.findViewById(R.id.gridsubtitle1);
			subtitle.setText(allfilenames.get(position).toString());
			ImageView im = (ImageView) result.findViewById(R.id.gridimage);
			new LoadImage(im, position).execute();
			return result;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allfilenames.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	}

	private class LoadImage extends AsyncTask<Void, Void, Bitmap> {

		public String filename;
		public ImageView im1;

		public LoadImage(ImageView im, Integer Position) {
			im1 = im;
			filename = allfilenames.get(Position).toString();
		}

		@Override
		protected Bitmap doInBackground(Void... fileusername) {


			Bitmap bitmap = null;
			File f = new File(myDir, filename);

			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			setimage(result, im1);
		}
	}

	public static void setimage(Bitmap bm, ImageView im) {
		if (bm != null) {
			im.setImageBitmap(Bitmap.createScaledBitmap(bm, 350, 350, false));

		}
	}

	private class Loadusername extends AsyncTask<Void, Void, Void> {

		public Loadusername() {

		}

		@Override
		protected Void doInBackground(Void... fileusername) {
			svallfilenames.clear();
			svallBitMap.clear();
			// Will collect the text file with usernames
			S3Object object = s3client.getObject(new GetObjectRequest(
					"Profile_Pictures/Discover", "Usernames.txt"));
			S3ObjectInputStream objectData = object.getObjectContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					objectData));
			String line;

			try {
				while ((line = br.readLine()) != null) {
					// process the line.
					svallfilenames.add(line);
					

				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File usernamefile = new File(myDir, "Usernames");

			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(usernamefile));
				
				for (int i = 0; svallfilenames.size() > i; i++) {
					
					writer.write(svallfilenames.get(i));

					writer.newLine();
				}
				writer.flush();
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception E) {
				E.printStackTrace();
			}
			
			new DownloadProfilepic(svallfilenames.get(0), 0).execute();

			return null;

		}
	}

	public boolean isNetworkAvaiable()
	{
		
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activenetworkinfo = cm.getActiveNetworkInfo();
		return activenetworkinfo != null;
	}
}
