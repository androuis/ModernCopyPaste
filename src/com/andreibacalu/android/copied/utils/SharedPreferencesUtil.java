package com.andreibacalu.android.copied.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	private final String SHARED_PREFERENCES_FILE = "copiedOptions";

	public final static String SETTING_DISPLAY_TOAST = "displayToast";
	public final static String SETTING_DISPLAY_NOTIFICATION = "displayNotification";
	public final static String SETTING_SERVICE_CLOSE = "serviceClose";
	public final static String SETTING_SERVICE_BOOT = "serviceBoot";
	public final static String SETTING_NOTIFICATION_NO_CLEAR = "notificationNoClear";
	public final static String SETTING_NOTIFICATION_BIG_VIEW = "notificationBigView";
	
	public final static String NOTIFICATION_TEXTS_LIST = "notification_texts_list";

	private static Map<String, Boolean> defaultValues = new HashMap<String, Boolean>();
	static {
		Map<String, Boolean> m = new HashMap<String, Boolean>();
		m.put(SETTING_DISPLAY_TOAST, true);
		m.put(SETTING_DISPLAY_NOTIFICATION, true);
		m.put(SETTING_SERVICE_CLOSE, false);
		m.put(SETTING_SERVICE_BOOT, false);
		m.put(SETTING_NOTIFICATION_NO_CLEAR, false);
		m.put(SETTING_NOTIFICATION_BIG_VIEW, false);
		defaultValues = Collections.unmodifiableMap(m);
	}

	private SharedPreferences sharedPreferences;
	private static SharedPreferencesUtil instance;

	private SharedPreferencesUtil(Context context) {
		sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
	}

	public static SharedPreferencesUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SharedPreferencesUtil(context);
		}
		return instance;
	}

	public synchronized boolean getSetting(String key) {
		return sharedPreferences.getBoolean(key, defaultValues.get(key));
	}

	public synchronized void setSetting(String key, boolean booleanValue) {
		sharedPreferences.edit().putBoolean(key, booleanValue).commit();
	}
	
	public synchronized void setTextsList(Set<String> set) {
		sharedPreferences.edit().putStringSet(NOTIFICATION_TEXTS_LIST, set).commit();
	}
	
	public synchronized Set<String> getTextsList() {
		Set<String> hashSet = sharedPreferences.getStringSet(NOTIFICATION_TEXTS_LIST, new HashSet<String>());
		return hashSet;
	}
}