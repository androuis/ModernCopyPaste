package com.andreibacalu.android.copied.activities;

import java.util.HashSet;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.application.CopiedApplication;
import com.andreibacalu.android.copied.fragments.CopiedTextsFragment;
import com.andreibacalu.android.copied.fragments.SettingsFragment;
import com.andreibacalu.android.copied.fragments.TutorialFragment;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;

public class MainActivity extends FragmentActivity {

	private final static String TAG_LOG = MainActivity.class.getSimpleName();
	
	private DrawerLayout drawerLayout;
	private ListView slidingMenuListView;
	private ActionBarDrawerToggle drawerToggle;

	private String[] menuOptions;

	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuOptions = getResources().getStringArray(
				R.array.sliding_menu_options);
		slidingMenuListView = (ListView) findViewById(R.id.menu_content);
		slidingMenuListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuOptions));
		slidingMenuListView
				.setOnItemClickListener(new DrawerClickItemListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(getString(R.string.app_name));
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		serviceIntent = new Intent(getBaseContext(),
				ChangeNotificationTextService.class);
		getBaseContext().startService(serviceIntent);
		Intent intent = getIntent();
		if (intent != null) {
			int command = intent.getIntExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
					ChangeNotificationTextService.INTENT_COMMAND_TYPE_UNKNOWN);
			if (command == ChangeNotificationTextService.INTENT_COMMAND_TYPE_OPEN_ACTIVITY) {
				selectItem(0);
			}
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    protected void onDestroy() {
		Log.e(TAG_LOG, "onDestroy");
    	if (SharedPreferencesUtil.getInstance(getApplicationContext()).getSetting(SharedPreferencesUtil.SETTING_SERVICE_CLOSE)) {
    		getBaseContext().stopService(serviceIntent);
    	}
    	SharedPreferencesUtil.getInstance(getApplicationContext()).setTextsList(new HashSet<String>(CopiedApplication.getList()));
    	super.onDestroy();
    }

	private class DrawerClickItemListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// Create a new fragment and specify the planet to show based on
		// position
		Fragment fragment = getFragmentByPosition(position);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_content, fragment)
				.commit();

		// Highlight the selected item, update the title, and close the drawer
		slidingMenuListView.setItemChecked(position, true);
		setTitle(menuOptions[position]);
		drawerLayout.closeDrawer(slidingMenuListView);
	}

	private Fragment getFragmentByPosition(int position) {
		Fragment fragment = null;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			fragment = new CopiedTextsFragment();
			break;
		case 1:
			fragment = new SettingsFragment();
			break;
		case 2:
			//TODO: TBD!
			fragment = new TutorialFragment();
			break;
		default:
			break;
		}
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}
}
