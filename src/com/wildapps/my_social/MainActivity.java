package com.wildapps.my_social;

import java.io.File;

import com.facebook.Session;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
 
    private String[] drawerListViewItems;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String LOG_TAG = "FB_LOGIN";
    private boolean loggingIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);
        
        registercheck();
        
        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.nav_drawer_items);
        // get ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);
 
        // Set the adapter for the list view
        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, drawerListViewItems));
 
        // 2. App Icon 
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
 
        // 2.1 create ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                );
 
        // 2.2 Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
 
        // 2.3 enable and show "up" arrow
        getActionBar().setDisplayHomeAsUpEnabled(true); 
        
        
        
 
        
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        Bundle e = getIntent().getExtras();
        if(e != null)
        {
        	 
        	 if(e.getInt("Page Number")> -1)
        	 {
        		 
        		 displayView(e.getInt("Page Number"));
        	 }
        	 
        }
        displayView(0);

    }
 
    private void registercheck() {
		//See if there is a my-info file
    	
    	File f = getBaseContext().getFileStreamPath("Myinfo");
    	if(f.exists() == false)
    	{
    		//User has just login in.
    		//Start register class and check username. 
    		startActivity(new Intent(getBaseContext(), Register.class));
    	}
    	
		
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         actionBarDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
         // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
 
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
 
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            drawerLayout.closeDrawer(drawerListView);
            displayView(position);
 
        }
    }
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
        //Home
        case 0:
            fragment = new Frontpage();
            break;
        //Discover
        case 1:
        	fragment = new Discover();
            break;
        //Edit Profile
        case 2:
        	 fragment = new EditProfile();
             break;
        //Search
        case 3:
        	fragment = new Search();
        	break;
        //Settings
        case 4:
        	//fragment = new Settings();
        	break;
        //Log Out
        case 5:
        	startActivity(new Intent(getBaseContext(), Logout.class));
        default:
            break;
        }
 
       
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
 
            // update selected item and title, then close the drawer
            drawerListView.setItemChecked(position, true);
            drawerListView.setSelection(position);
            setTitle(drawerListViewItems[position]);
            drawerLayout.closeDrawer(drawerListView);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
 
}
