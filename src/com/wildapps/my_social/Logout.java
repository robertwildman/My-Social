package com.wildapps.my_social;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Logout extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.loginstyle);
		setContentView(R.layout.login);

		UserLogout();
	}

	public void UserLogout() {
		File f = getBaseContext().getFileStreamPath("Myinfo");
		f.delete();
		Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT);
		startActivity(new Intent(getBaseContext(), Login.class));
	}

}
