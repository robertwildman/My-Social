package com.wildapps.my_social;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class EditProfileAdapter extends ArrayAdapter<String> {

	private final Context context;
	public String tweetoutput;
	public TextView tweets;
	public Context basecontext;
	public static ImageView profile;
	public ImageView imageback;
	private final String[] Network, Networkuser;

	public EditProfileAdapter(Context context, String[] Network,
			String[] Networkuser) {
		super(context, R.layout.grid_item2, Network);
		this.context = context;
		basecontext = context;
		this.Network = Network;
		this.Networkuser = Networkuser;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.grid_item2, parent,
				false);
		RelativeLayout g2 = (RelativeLayout) rowView.findViewById(R.id.grid2);
		ImageView improfile = (ImageView) rowView
				.findViewById(R.id.ivprofilegrid);
		Typeface face1 = Typeface.createFromAsset(basecontext.getAssets(), "fonts/Roboto-Light.ttf");
		Typeface face2 = Typeface.createFromAsset(basecontext.getAssets(), "fonts/Roboto-Medium.ttf");
		TextView tvNetwork = (TextView) rowView.findViewById(R.id.gridtitle);
		TextView tvNetworkuser = (TextView) rowView.findViewById(R.id.gridsubtitle);
		tvNetwork.setTypeface(face2);
		tvNetworkuser.setTypeface(face1);
		
		improfile.setVisibility(View.INVISIBLE);
		improfile.setImageResource(getcolor(Network[position]));
		g2.setBackgroundResource(getcolor(Network[position]));

		tvNetwork.setText(Network[position]);
		if (Network[position].contains("Profile")) {
			improfile.setVisibility(View.VISIBLE);
			File f = context.getApplicationContext().getFileStreamPath(
					"My-Social Profile Picture");
			profile = improfile;
			if (f.canRead()) {

				improfile.setImageBitmap(BitmapFactory.decodeFile(f
						.getAbsolutePath()));

			}
			tvNetwork.setVisibility(View.GONE);
			tvNetworkuser.setVisibility(View.GONE);
		} else if (Network[position].contains("Bio")) {
			tvNetwork.setText("My-Social");
			tvNetworkuser.setText("Bio:   " + Networkuser[position]);

		} else if (Network[position].contains("Website")) {

			tvNetworkuser.setText("URL:  " + Networkuser[position]);
		} else {

			tvNetworkuser.setText("Username:  " + Networkuser[position]);
		}
		// if(Network[position].equals("Twitter"))
		// {

		// tweets = (TextView)rowView.findViewById(R.id.tvtweets);
		// new gettweets().execute(Networkuser[position].toString());
		// }

		return rowView;
	}

	public int getcolor(String networkname) {
		if (networkname.equalsIgnoreCase("Facebook")){
		return R.color.Facebook ;
		}
		else if (networkname.equalsIgnoreCase("Twitter")){
		return R.color.Twitter;
		}
		else if (networkname.equalsIgnoreCase("Snapchat")){
		return R.color.Snapchat;
		}
		else if (networkname.equalsIgnoreCase("Tumblr")){
		return R.color.Tumblr;
		}
		else if (networkname.equalsIgnoreCase("Google +")){
		return R.color.Google_plus;
		}
		else if (networkname.equalsIgnoreCase("Flickr")){
		return R.color.Flickr;
		}
		else if (networkname.equalsIgnoreCase("Bebo")){
		return R.color.Bebo;
		}
		else if (networkname.equalsIgnoreCase("Last FM")){
		return R.color.Last_FM;
		}
		else if (networkname.equalsIgnoreCase("Spotify")){
		return R.color.Spotify;
		}
		else if (networkname.equalsIgnoreCase("Foursquare")){
		return R.color.Foursquare;
		}
		else if (networkname.equalsIgnoreCase("GitHub")){
		return R.color.GitHub;
		}
		else if (networkname.equalsIgnoreCase("Stackoverflow")){
		return R.color.Stackoverflow;
		}
		else if (networkname.equalsIgnoreCase("Youtube")){
		return R.color.Youtube;
		}
		else if (networkname.equalsIgnoreCase("Linkedin")){
		return R.color.Linkedin;
		}
		else if (networkname.equalsIgnoreCase("Vimeo")){
		return R.color.Vimeo;
		}
		else if (networkname.equalsIgnoreCase("Pinterest")){
		return R.color.Pinterest;
		}
		else if (networkname.equalsIgnoreCase("Blogger")){
		return R.color.Blogger;
		}
		else if (networkname.equalsIgnoreCase("Yahoo")){
		return R.color.Yahoo;
		}
		else if (networkname.equalsIgnoreCase("Skype")){
		return R.color.Skype;
		}
		else if (networkname.equalsIgnoreCase("Soundcloud")){
		return R.color.Soundcloud;
		}
		else if (networkname.equalsIgnoreCase("Reddit")){
		return R.color.Reddit;
		}
		else if (networkname.equalsIgnoreCase("KickStarter")){
		return R.color.KickStarter;
		}
		else if (networkname.equalsIgnoreCase("Twitch")){
		return R.color.Twitch;
		}
		else if (networkname.equalsIgnoreCase("Photobucket")){
		return R.color.Photobucket;
		}
		else if (networkname.equalsIgnoreCase("Vine")){
		return R.color.Vine;
		}
		else if (networkname.equalsIgnoreCase("Email")){
		return R.color.Email;
		}
		else if (networkname.equalsIgnoreCase("Website 1")){
		return R.color.Website_1;
		}
		else if (networkname.equalsIgnoreCase("Website 2")){
		return R.color.Website_2;
		}
		else if (networkname.equalsIgnoreCase("Website 3")){
		return R.color.Website_3;
		}
		else if (networkname.equalsIgnoreCase("Website 4")){
		return R.color.Website_4;
		}
		else if (networkname.equalsIgnoreCase("Website 5")){
		return R.color.Website_5;
		}
		else if (networkname.equalsIgnoreCase("We Heart It")){
		return R.color.We_Heart_It;
		}
		else if (networkname.equalsIgnoreCase("Ustream")){
		return R.color.Ustream;
		}
		else if (networkname.equalsIgnoreCase("Instagram")){
			return R.color.Instagram;
		}
		return 0;
	}

	public void upload(Bitmap bm) {
		// AWS S3

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		AmazonS3Client s3client = null;
		try {
			AWSCredentials credentials;
			credentials = new PropertiesCredentials(
					EditProfileAdapter.class
							.getResourceAsStream("AwsCredentials.properties"));
			s3client = new AmazonS3Client(credentials);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileOutputStream out = null;
		try {
			File f = context.getApplicationContext().getFileStreamPath(
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

		File f = context.getApplicationContext().getFileStreamPath(
				"My-Social Profile Picture");
		String fpath = f.getAbsolutePath();
		PutObjectRequest por = new PutObjectRequest("Profile_Pictures",
				getusername(), new java.io.File(fpath));
		s3client.putObject(por);
	}

	public String getusername() {
		// Picks it up from file
		try {
			InputStream inputStream = context.getApplicationContext()
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
