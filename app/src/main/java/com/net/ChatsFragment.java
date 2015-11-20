package com.net;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.net.provider.DataProvider;
import com.net.util.Common;

public class ChatsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter adapter;
	private AlertDialog disclaimer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		adapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.main_list_item,
				null, 
				new String[]{DataProvider.COL_NAME, DataProvider.COL_COUNT},
				new int[]{R.id.text1, R.id.text2},
				0);
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				int id = view.getId();
				if (id == R.id.text2) {
					int count = cursor.getInt(columnIndex);
					if (count > 0) {
						((TextView)view).setText(String.format("%d new message%s", count, count==1 ? "" : "s"));
					}
					return true;
				}
				return false;
			}
		});
		
		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, ChatsFragment.this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(Common.PROFILE_ID, String.valueOf(id));
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_add) {
			AddContactDialog dialog = new AddContactDialog();
			dialog.show(getFragmentManager(), "AddContactDialog");
			return true;
		} else if (itemId == R.id.action_settings) {
			//		Intent intent = new Intent(this, SettingsActivity.class);
	//		startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity(), 
				DataProvider.CONTENT_URI_PROFILE, 
				new String[]{DataProvider.COL_ID, DataProvider.COL_NAME, DataProvider.COL_COUNT}, 
				null, 
				null, 
				DataProvider.COL_ID + " DESC"); 
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	
}
