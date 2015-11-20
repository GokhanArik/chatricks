package com.net;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.net.provider.DataProvider;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MessagesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat[] df = new DateFormat[] {
		DateFormat.getDateInstance(), DateFormat.getTimeInstance()};

	private OnFragmentInteractionListener mListener;
	private SimpleCursorAdapter adapter;
	private Date now;
	private MessagesAdapter mAdapter;
	private ListView lvMessageList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messaging_fragment, null);
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		now = new Date();
		
/*		
		adapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.chat_list_item, 
				null, 
				new String[]{DataProvider.COL_MSG, DataProvider.COL_AT}, 
				new int[]{R.id.text1, R.id.text2},
				0);
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				int id = view.getId();
				if (id == R.id.text1) {
					LinearLayout root = (LinearLayout) view.getParent().getParent();
					if (cursor.getString(cursor.getColumnIndex(DataProvider.COL_FROM)) == null) {
						root.setGravity(Gravity.RIGHT);
						root.setPadding(50, 10, 10, 10);
					} else {
						root.setGravity(Gravity.LEFT);
						root.setPadding(10, 10, 50, 10);
					}
				} else if (id == R.id.text2) {
					TextView tv = (TextView) view;
					tv.setText(getDisplayTime(cursor.getString(columnIndex)));
					return true;
				}
				return false;
			}
		});	
*/
	//	setListAdapter(adapter);
	}	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lvMessageList = (ListView) getView().findViewById(R.id.lvMessageList);
		lvMessageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

	//	getListView().setDivider(null);
		
		Bundle args = new Bundle();
		args.putString(DataProvider.COL_EMAIL, mListener.getProfileEmail());
		getLoaderManager().initLoader(0, args, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		public String getProfileEmail();
	}
	
	private String getDisplayTime(String datetime) {
		try {
			Date dt = sdf.parse(datetime);
			if (now.getYear()==dt.getYear() && now.getMonth()==dt.getMonth() && now.getDate()==dt.getDate()) {
				return df[1].format(dt);
			}
			return df[0].format(dt);
		} catch (ParseException e) {
			return datetime;
		}
	}
	
	//----------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String profileEmail = args.getString(DataProvider.COL_EMAIL);
		CursorLoader loader = new CursorLoader(getActivity(), 
				DataProvider.CONTENT_URI_MESSAGES, 
				null, 
				DataProvider.COL_FROM + " = ? or " + DataProvider.COL_TO + " = ?",
				new String[]{profileEmail, profileEmail}, 
				DataProvider.COL_AT + " ASC"); 
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter = new MessagesAdapter(getActivity(), data, 1 );
		lvMessageList.setAdapter(mAdapter);
	//	mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	

}
