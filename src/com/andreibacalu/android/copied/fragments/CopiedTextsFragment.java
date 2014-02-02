package com.andreibacalu.android.copied.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.adapters.CopiedTextsAdapter;
import com.andreibacalu.android.copied.application.CopiedApplication;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.GeneralUtil;
import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

public class CopiedTextsFragment extends Fragment implements
		OnItemClickListener {

	private CopiedTextsAdapter adapter;
	private ArrayList<String> initialList;
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_list_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView) view.findViewById(R.id.listview);
		// Create a ListView-specific touch listener. ListViews are given
		// special treatment because
		// by default they handle touches for their list items... i.e. they're
		// in charge of drawing
		// the pressed state (the list selector), handling list item clicks,
		// etc.
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							adapter.remove(adapter.getItem(position));
						}
						adapter.notifyDataSetChanged();
						replaceListAndNotify();
					}
				});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
		listView.setOnItemClickListener(this);
		listView.setEmptyView(view.findViewById(R.id.empty_view));
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter = new CopiedTextsAdapter(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				initialList = CopiedApplication.getList());
		listView.setAdapter(adapter);
	}

	private void replaceListAndNotify() {
		CopiedApplication.replaceList(adapter.getElements());
		Intent intent = new Intent(getActivity().getBaseContext(),
				ChangeNotificationTextService.class);
		intent.putExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
				ChangeNotificationTextService.INTENT_COMMAND_TYPE_CHANGE_NOTIF);
		getActivity().getBaseContext().startService(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CopiedApplication.setCurrentSelectedString(CopiedApplication.getList()
				.get(position));
		Intent intent = new Intent(getActivity().getBaseContext(),
				ChangeNotificationTextService.class);
		intent.putExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
				ChangeNotificationTextService.INTENT_COMMAND_TYPE_COPY);
		getActivity().getBaseContext().startService(intent);
		Toast.makeText(getActivity(), getString(R.string.text_copied),
				Toast.LENGTH_SHORT).show();
	}
}
