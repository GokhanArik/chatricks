package com.net;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.net.provider.DataProvider;
import com.net.util.Common;
import com.net.util.GcmUtil;
import com.net.util.ServerUtilities;

public class ChatActivity extends Activity implements MessagesFragment.OnFragmentInteractionListener, EditContactDialog.OnFragmentInteractionListener {

	private EditText msgEdit;
	private Button sendBtn;
	private String profileId;
	private String profileName;
	private String profileEmail;
	private static final long DOUBLE_CLICK_TIME_DELTA = 600;//milliseconds
    long lastClickTime = 0;
	private GcmUtil gcmUtil;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getWindow().setBackgroundDrawableResource(R.drawable.piri_reis_bg); 
		
		MessagesFragment newFragment = new MessagesFragment();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.msg_list, newFragment);
		transaction.commit();
		
		profileId = getIntent().getStringExtra(Common.PROFILE_ID);
		msgEdit = (EditText) findViewById(R.id.msg_edit);
		sendBtn = (Button) findViewById(R.id.send_btn);
		
		sendBtn.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				if( msgEdit.getText() != null && msgEdit.getText().length() > 0){
					send(msgEdit.getText().toString());
					msgEdit.setText(null);	
				}
			}
			
		});
		msgEdit.addTextChangedListener( new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if( s.length() > start && s.charAt(start) == ' '){
					long clickTime = System.currentTimeMillis();
			        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
						Log.v("a", "a: " + s.charAt(before) + "-" + start + " - " + before);
						msgEdit.setText( s.subSequence(0, start-1) + " aq");
						msgEdit.setSelection(start + 2);
			        } else {
						Log.v("a", "SINGLE");
			        }
			        lastClickTime = clickTime;
				}
			}
			
		});
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Cursor c = getContentResolver().query(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), null, null, null, null);
		if (c.moveToFirst()) {
			profileName = c.getString(c.getColumnIndex(DataProvider.COL_NAME));
			profileEmail = c.getString(c.getColumnIndex(DataProvider.COL_EMAIL));
			actionBar.setTitle(profileName);
		}
		actionBar.setSubtitle("Bağlanıyor ...");
		
		registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));
		gcmUtil = new GcmUtil(getApplicationContext());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_edit) {
			EditContactDialog dialog = new EditContactDialog();
			Bundle args = new Bundle();
			args.putString(Common.PROFILE_ID, profileId);
			args.putString(DataProvider.COL_NAME, profileName);
			dialog.setArguments(args);
			dialog.show(getFragmentManager(), "EditContactDialog");
			return true;
		} else if (itemId == android.R.id.home) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onEditContact(String name) {
		getActionBar().setTitle(name);
	}	
	
	@Override
	public String getProfileEmail() {
		return profileEmail;
	}	
	
	private void send(final String txt) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    
        			ContentValues values = new ContentValues(2);
        			values.put(DataProvider.COL_MSG, txt);
        			values.put(DataProvider.COL_TO, profileEmail);
        			getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
        			
        			ServerUtilities.send(txt, profileEmail);
        			
                } catch (IOException ex) {
                    msg = "Mesaj gönderilemedi.";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	if (!TextUtils.isEmpty(msg)) {
            		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            	}
            }
        }.execute(null, null, null);		
	}	

	@Override
	protected void onPause() {
		ContentValues values = new ContentValues(1);
		values.put(DataProvider.COL_COUNT, 0);
		getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), values, null, null);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(registrationStatusReceiver);
		gcmUtil.cleanup();
		super.onDestroy();
	}
	
	//--------------------------------------------------------------------------------
	
	private BroadcastReceiver registrationStatusReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && Common.ACTION_REGISTER.equals(intent.getAction())) {
				switch (intent.getIntExtra(Common.EXTRA_STATUS, 100)) {
				case Common.STATUS_SUCCESS:
					getActionBar().setSubtitle("Çevrimiçi");
					sendBtn.setEnabled(true);
					break;
					
				case Common.STATUS_FAILED:
					getActionBar().setSubtitle("Çevrimdışı");					
					break;					
				}
			}
		}
	};	

}
