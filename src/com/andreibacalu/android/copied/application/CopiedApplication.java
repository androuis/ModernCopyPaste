package com.andreibacalu.android.copied.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.andreibacalu.android.copied.R.string;

import android.app.Application;

public class CopiedApplication extends Application {

	public final static String DEFAULT_STRING = "You have no copied data yet.";
	
	private static List<String> clipboardStrings = new ArrayList<String>();
	private static String currentSelectedString = "";
	
	public static String getCurrentSelectedString() {
		if (clipboardStrings.size() > 0) {
			return currentSelectedString;
		} else {
			return DEFAULT_STRING;
		}
	}
	
	public static void setCurrentSelectedString(String string) {
		currentSelectedString = string;
	}
	
	public static String getClipboarString(int position) {
		if (clipboardStrings.size() > position && position >= 0) {
			return clipboardStrings.get(position);
		} else {
			return null;
		}
	}
	
	public static void addStringToClipboard(String string) {
		clipboardStrings.add(string);
		currentSelectedString = string;
	}
	
	public static int removeStringFromClipboard(String string) {
		int stringLocation = clipboardStrings.indexOf(string);
		clipboardStrings.remove(string);
		return stringLocation;
	}
	
	public static int getPositionOfCurrentSelectedString() {
		if (clipboardStrings.size() > 0) {
			return clipboardStrings.indexOf(currentSelectedString);
		}
		return -1;
	}
	
	public static int getNumberOfTextsInClipboard() {
		return clipboardStrings.size();
	}
	
	public static boolean clipboarStringsContain(String string) {
		return clipboardStrings.contains(string);
	}
	
	public static ArrayList<String> getList() {
		return new ArrayList<String>(clipboardStrings);
	}
	
	public static void replaceList(List<String> list) {
		clipboardStrings.clear();
		clipboardStrings.addAll(list);
		setCurrentSelectedString(clipboardStrings.size() > 0 ? clipboardStrings.get(0) : DEFAULT_STRING);
	}
}
