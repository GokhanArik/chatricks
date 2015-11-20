package com.net.util;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;

public class Common extends Application {
	
	public static final String PROFILE_ID = "profile_id";
	
	public static final String ACTION_REGISTER = "com.net.REGISTER";
	public static final String EXTRA_STATUS = "status";
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILED = 0;
	
	//parameters recognized by demo server
	public static final String FROM = "email";
	public static final String REG_ID = "regId";
	public static final String MSG = "msg";
	public static final String TO = "email2";	
	
	public static String[] email_arr;
	
	private static SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		List<String> emailList = getEmailList();
		email_arr = emailList.toArray(new String[emailList.size()]);
	}
	
	private List<String> getEmailList() {
		List<String> lst = new ArrayList<String>();
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
		    if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
		        lst.add(account.name);
		    }
		}
		return lst;
	}
	
	public static String getPreferredEmail() {
		return "arik.gokhan@yahoo.com.tr";
	}
	
	public static String getDisplayName() {
		String email = getPreferredEmail();
		return prefs.getString("display_name", email.substring(0, email.indexOf('@')));
	}
	
	public static boolean isNotify() {
		return prefs.getBoolean("notifications_new_message", true);
	}
	
	public static String getRingtone() {
		return prefs.getString("notifications_new_message_ringtone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}
	
	public static String getServerUrl() {
		return Constants.SERVER_URL;
	}
	
	public static String getSenderId() {
		return Constants.SENDER_ID;
	}	
    	
}
