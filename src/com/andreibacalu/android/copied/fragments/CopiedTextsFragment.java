package com.andreibacalu.android.copied.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.adapters.CopiedTextsAdapter;
import com.andreibacalu.android.copied.application.CopiedApplication;
import com.andreibacalu.android.copied.dialogs.AddTextDialogFragment;
import com.andreibacalu.android.copied.dialogs.AddTextDialogFragment.AddTextDialogFragmentActionResponse;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;
import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

public class CopiedTextsFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener,
		AddTextDialogFragmentActionResponse {

	private static final String TAG_LOG = CopiedTextsFragment.class
			.getSimpleName();
	private static final String TAG_DIALOG_ADD_TEXT = "dialog_add_text";

	private CopiedTextsAdapter adapter;
	private ListView listView;
	private CutTextBroadcastReceiver cutTextBroadcastReceiver;
	private AddTextDialogFragment dialogFragment;

	public CopiedTextsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG_LOG, "onCreateView");
		return inflater.inflate(R.layout.activity_list_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.e(TAG_LOG, "onViewCreated");
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
						int position = -1;
						for (int postionIteratable : reverseSortedPositions) {
							adapter.remove(adapter.getItem(postionIteratable));
							position = postionIteratable;
							break;
						}
						if (position != -1) {
							adapter.notifyDataSetChanged();
							sendCommandRemove(position);
						}
					}
				});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		listView.setEmptyView(view.findViewById(R.id.empty_view));

		view.findViewById(R.id.list_view_add).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogFragment = new AddTextDialogFragment(AddTextDialogFragment.TYPE_CREATE, "", CopiedTextsFragment.this);
						dialogFragment.show(getActivity()
								.getSupportFragmentManager(),
								TAG_DIALOG_ADD_TEXT);
					}
				});

		refreshList(false);
		cutTextBroadcastReceiver = new CutTextBroadcastReceiver();
		getActivity().registerReceiver(cutTextBroadcastReceiver,
				new IntentFilter(getString(R.string.action_cut_performed)));

		if (savedInstanceState != null) {
			dialogFragment = (AddTextDialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_ADD_TEXT);
			if (dialogFragment != null) {
				dialogFragment.setListener(this);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(cutTextBroadcastReceiver);
	}

	private void refreshList(boolean withCheck) {
		List<String> textsList = CopiedApplication.getList();
		if (withCheck) {
			textsList = CopiedApplication.getNumberOfTextsInClipboard() > 0 ? textsList
					: new ArrayList<String>(SharedPreferencesUtil.getInstance(
							getActivity().getApplicationContext())
							.getTextsList());
		}
		adapter = new CopiedTextsAdapter(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				textsList);
		listView.setAdapter(adapter);
	}

	private void sendCommandChangeNotif() {
		Intent intent = new Intent(getActivity().getBaseContext(),
				ChangeNotificationTextService.class);
		intent.putExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
				ChangeNotificationTextService.INTENT_COMMAND_TYPE_CHANGE_NOTIF);
		getActivity().getBaseContext().startService(intent);
	}

	private void sendCommandCopy() {
		Intent intent = new Intent(getActivity().getBaseContext(),
				ChangeNotificationTextService.class);
		intent.putExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
				ChangeNotificationTextService.INTENT_COMMAND_TYPE_COPY);
		getActivity().getBaseContext().startService(intent);
	}

	private void sendCommandRemove(int position) {
		Intent intent = new Intent(getActivity().getBaseContext(),
				ChangeNotificationTextService.class);
		intent.putExtra(ChangeNotificationTextService.INTENT_COMMAND_TYPE,
				ChangeNotificationTextService.INTENT_COMMAND_TYPE_REMOVE);
		intent.putExtra(ChangeNotificationTextService.INTENT_REMOVE_POSITION,
				position);
		getActivity().getBaseContext().startService(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position < CopiedApplication.getNumberOfTextsInClipboard()) {
			CopiedApplication.setCurrentSelectedString(CopiedApplication
					.getList().get(position));
			sendCommandCopy();
		} else {
			refreshList(false);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		dialogFragment = new AddTextDialogFragment(
				AddTextDialogFragment.TYPE_EDIT, adapter.getItem(position),
				this);
		dialogFragment.show(getActivity().getSupportFragmentManager(),
				TAG_DIALOG_ADD_TEXT);
		return true;
	}

	private class CutTextBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& getString(R.string.action_cut_performed).equals(
							intent.getAction())) {
				refreshList(false);
			}
		}
	}

	@Override
	public void onTextUpdated() {
		adapter.clear();
		adapter.addAll(CopiedApplication.getList());
		sendCommandChangeNotif();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onTextAdded(String textAdded) {
		adapter.add(textAdded);
		sendCommandCopy();
		adapter.notifyDataSetChanged();
	}
}
