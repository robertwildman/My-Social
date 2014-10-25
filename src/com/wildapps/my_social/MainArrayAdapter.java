package com.wildapps.my_social;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	public String tweetoutput;
	public TextView tweets;
	private final String[] Network, Networkuser;

	public MainArrayAdapter(Context context, String[] Network,
			String[] Networkuser) {
		super(context, R.layout.rowlayout, Network);
		this.context = context;
		this.Network = Network;
		this.Networkuser = Networkuser;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView tvNetwork = (TextView) rowView.findViewById(R.id.tvrownetwork);
		tvNetwork.setText(Network[position]);
		// if(Network[position].equals("Twitter"))
		// {

		// tweets = (TextView)rowView.findViewById(R.id.tvtweets);
		// new gettweets().execute(Networkuser[position].toString());
		// }
		TextView tvNetworkuser = (TextView) rowView
				.findViewById(R.id.tvrowuser);
		tvNetworkuser.setText(Networkuser[position]);
		// Change the icon for Windows and iPhone

		return rowView;
	}

	/*
	 * private class gettweets extends AsyncTask<String,Void,String> {
	 * 
	 * 
	 * protected void onPostExecute(String outtweets) {
	 * tweets.setText(outtweets);
	 * 
	 * } protected String doInBackground(String...strings) {
	 * 
	 * return tweets(strings[0].toString()); } }
	 * 
	 * 
	 * public String tweets(final String Username) {
	 * 
	 * //Makes sure that the HTTP request can get thought the android system
	 * StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	 * .permitAll().build(); StrictMode.setThreadPolicy(policy);
	 * 
	 * 
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * try { ConfigurationBuilder cb = new ConfigurationBuilder();
	 * cb.setDebugEnabled(true) .setOAuthConsumerKey("Cad025k2XxkWz9YhvMK1w")
	 * .setOAuthConsumerSecret("brR62bZtMH87RIarNiaC5BziP3k3zPIdE0Bswr0r3s")
	 * .setOAuthAccessToken
	 * ("270078398-spaCcdJg7Byr8T6CoF4VEAJQ9sD8uOvhAJKIW2wU")
	 * .setOAuthAccessTokenSecret
	 * ("EfZmsk1dKnA5mHi90idclC0Q5AI8xwYw6plsUVYhPPFRz")
	 * .setHttpConnectionTimeout(100000); TwitterFactory tf = new
	 * TwitterFactory(cb.build()); Twitter twitter = tf.getInstance();
	 * 
	 * 
	 * String user; user = "robertwildman"; Paging paging = new Paging(1, 3);
	 * statuses = twitter.getUserTimeline(Username,paging);
	 * Log.i("Status Count", statuses.size() + " Feeds");
	 * tweetoutput="Latest Tweets: "+ System.getProperty("line.separator"); for
	 * (Status status : statuses) { tweetoutput = tweetoutput + status.getText()
	 * + System.getProperty("line.separator")+
	 * System.getProperty("line.separator"); }
	 * 
	 * 
	 * 
	 * 
	 * } catch (TwitterException te) { te.printStackTrace(); } catch (Exception
	 * te) { te.printStackTrace(); } } }).start(); return tweetoutput;
	 * 
	 * }
	 */

}