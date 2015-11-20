package com.net;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private SimpleCursorAdapter adapter;
	private AlertDialog disclaimer;
	CollectionPagerAdapter mCollectionPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		mCollectionPagerAdapter = new CollectionPagerAdapter(
				getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}

				});
		for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mCollectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}



		/*
		 * ArrayAdapter<CharSequence> dropdownAdapter =
		 * ArrayAdapter.createFromResource(this, R.array.dropdown_arr,
		 * android.R.layout.simple_list_item_1);
		 * actionBar.setListNavigationCallbacks(dropdownAdapter, new
		 * ActionBar.OnNavigationListener() {
		 * 
		 * @Override public boolean onNavigationItemSelected(int itemPosition,
		 * long itemId) { getLoaderManager().restartLoader(0,
		 * getArgs(itemPosition), MainActivity.this); return true; } });
		 */
		// disclaimer = Disclaimer.show(this);
	}

	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());

	}

	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_add) {
			AddContactDialog dialog = new AddContactDialog();
			dialog.show(getSupportFragmentManager(), "AddContactDialog");
			return true;
		} else if (itemId == R.id.action_settings) {
			// Intent intent = new Intent(this, SettingsActivity.class);
			// startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if (disclaimer != null)
			disclaimer.dismiss();
		super.onDestroy();
	}

	// ----------------------------------------------------------------------------

	public class CollectionPagerAdapter extends FragmentPagerAdapter {

		final int NUM_ITEMS = 3; // number of tabs

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int i) {
			Fragment fragment = null;
			
			switch(i){
				case 0:
					fragment =  new ChatsFragment();
					break;
				case 1:
					fragment =  new FriendsListFragment();
					break;
				case 2:
					fragment =  new SettingsFragment();
					break;
			}
			
			
			return fragment;

		}

		@Override
		public int getCount() {

			return NUM_ITEMS;

		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabLabel = "";
			
			switch(position){
				case 0:
					tabLabel = getString(R.string.dedikodu);
					break;
				case 1:
					tabLabel = getString(R.string.kankalar);
					break;
				case 2:
					tabLabel = getString(R.string.ayarlar);
					break;
			}
			
			return tabLabel;

		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	
}
