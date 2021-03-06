package com.andreibacalu.android.copied.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CopiedTextsAdapter extends ArrayAdapter<String> {

	private List<String> elements;
	
	public CopiedTextsAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		elements = objects;
	}
	
	public List<String> getElements() {
		return elements;
	}
}
