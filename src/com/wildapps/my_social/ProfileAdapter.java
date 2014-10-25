package com.wildapps.my_social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileAdapter extends ArrayAdapter<String> {

	private final Context context;
	public String tweetoutput;
	public TextView tweets;
	public Context basecontext;
	public ImageView imageback;
	private final String[] Network, Networkuser;

	public ProfileAdapter(Context context, String[] Network,
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
		View rowView = inflater.inflate(R.layout.grid_item2, parent, false);

		Typeface face1 = Typeface.createFromAsset(basecontext.getAssets(), "fonts/Roboto-Light.ttf");
		Typeface face2 = Typeface.createFromAsset(basecontext.getAssets(), "fonts/Roboto-Medium.ttf");
		RelativeLayout g2 = (RelativeLayout) rowView.findViewById(R.id.grid2);
		TextView tvNetwork = (TextView) rowView.findViewById(R.id.gridtitle);
		TextView tvNetworkuser = (TextView) rowView.findViewById(R.id.gridsubtitle);
		tvNetwork.setTypeface(face2);
		tvNetworkuser.setTypeface(face1);
		ImageView improfile = (ImageView) rowView.findViewById(R.id.ivprofilegrid);

		g2.setBackgroundResource(getcolor(Network[position]));
		improfile.setImageResource(getcolor(Network[position]));
		tvNetwork.setText(Network[position]);
		if (Network[position].contains("Bio")) {
			tvNetwork.setText("My-Social");
			tvNetworkuser.setText("Bio:   " + Networkuser[position]);

		} else if (Network[position].contains("Website")) {

			tvNetworkuser.setText("URL:  " + Networkuser[position]);
			g2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String url = Networkuser[position];
					if (!url.startsWith("http://")
							&& !url.startsWith("https://")) {
						url = "http://" + url;
					}
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					basecontext.startActivity(browserIntent);

				}
			});
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
		return 0;
	}
}
