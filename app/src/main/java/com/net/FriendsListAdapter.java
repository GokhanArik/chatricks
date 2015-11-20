package com.net;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;


public class FriendsListAdapter extends CursorAdapter implements SectionIndexer {

	private LayoutInflater mInflater;
	AlphabetIndexer alphaIndexer;

	static class ViewHolder {
		TextView name;
		int column1;
		int column2;
	}

	public FriendsListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		alphaIndexer = new AlphabetIndexer(c,
				c.getColumnIndex(Contacts.DISPLAY_NAME),
				" ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) view.getTag();

		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.text1);
			// holder.tvTime = (TextView) view.findViewById(R.id.text2);
			holder.column1 = cursor
					.getColumnIndexOrThrow(Contacts.DISPLAY_NAME_PRIMARY);
			view.setTag(holder);
		}
		holder.name.setText(cursor.getString(holder.column1));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.v("gokhan", "a: " + getSectionForPosition( cursor.getPosition())  + " " + getSections() );
		return mInflater.inflate(R.layout.contact_item, parent, false);
	}

	@Override
	public int getPositionForSection(int section) {
		return alphaIndexer.getPositionForSection(section); // use the indexer
	}

	@Override
	public int getSectionForPosition(int position) {
		return alphaIndexer.getSectionForPosition(position); // use the indexer
	}

	@Override
	public String[] getSections() {
		return (String[]) alphaIndexer.getSections(); // use the indexer
	}
}