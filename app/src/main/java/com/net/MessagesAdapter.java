package com.net;

import com.net.provider.DataProvider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessagesAdapter extends CursorAdapter {
	 
	 private LayoutInflater mInflater;
	 
	 static class ViewHolder{
		 TextView tvMessage;
	//	 TextView tvTime;
		 int column1;
		 int column2;
	 }
	 public MessagesAdapter(Context context, Cursor c, int flags) {
		 super(context, c, flags);
		 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 
	 @Override
	 public void bindView(View view, Context context, Cursor cursor) {
	 
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		ViewHolder holder = (ViewHolder) view.getTag();
		
		if (holder == null) {
		    holder = new ViewHolder();
		    holder.tvMessage = (TextView) view.findViewById(R.id.text1);
		 //   holder.tvTime = (TextView) view.findViewById(R.id.text2);
		    holder.column1 = cursor.getColumnIndexOrThrow(DataProvider.COL_MSG);
		    holder.column2 = cursor.getColumnIndexOrThrow(DataProvider.COL_AT);
		    view.setTag(holder);
		}
		holder.tvMessage.setMaxWidth(display.getWidth()/2);
		holder.tvMessage.setText(cursor.getString(holder.column1));
	//	holder.tvTime.setText(cursor.getString(holder.column2));
	        
		
		if (cursor.getString(cursor.getColumnIndex(DataProvider.COL_FROM)) == null) {
			((LinearLayout) view).setGravity(Gravity.RIGHT);
		//	((LinearLayout) view).setPadding(50, 10, 10, 10);
		} else {
			((LinearLayout) view).setGravity(Gravity.LEFT);
		//	((LinearLayout) view).setPadding(10, 10, 50, 10);
		}	 
	 }
	 
	 @Override
	 public View newView(Context context, Cursor cursor, ViewGroup parent) {
		 return mInflater.inflate(R.layout.chat_list_item, parent, false);
	 }
	 
	}