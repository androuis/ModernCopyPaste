package com.andreibacalu.android.copied.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.adapters.CopiedTextsAdapter;
import com.andreibacalu.android.copied.application.CopiedApplication;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;
import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

public class CopiedTextsFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener {

	private static final String TAG_LOG = CopiedTextsFragment.class
			.getSimpleName();
	private static final String TAG_DIALOG_ADD_TEXT = "dialog_add_text";

	private CopiedTextsAdapter adapter;
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
						DialogFragment dialogFragment = new AddTextDialogFragment();
						dialogFragment.show(getActivity()
								.getSupportFragmentManager(),
								TAG_DIALOG_ADD_TEXT);
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshList();
	}

	private void refreshList() {
		List<String> textsList = CopiedApplication
				.getNumberOfTextsInClipboard() > 0 ? CopiedApplication
				.getList() : new ArrayList<String>(SharedPreferencesUtil
				.getInstance(getActivity().getApplicationContext())
				.getTextsList());
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
		if (position <= CopiedApplication.getNumberOfTextsInClipboard()) {
			CopiedApplication.setCurrentSelectedString(CopiedApplication
					.getList().get(position));
			sendCommandCopy();
		} else {
			refreshList();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		DialogFragment dialogFragment = new AddTextDialogFragment(
				AddTextDialogFragment.TYPE_EDIT, adapter.getItem(position));
		dialogFragment.show(getActivity().getSupportFragmentManager(),
				TAG_DIALOG_ADD_TEXT);
		return true;
	}

	private class AddTextDialogFragment extends DialogFragment implements
			android.content.DialogInterface.OnClickListener {

		public static final int TYPE_CREATE = 0;
		public static final int TYPE_EDIT = 1;

		private EditText editText;
		private int type;
		private String textToBeEdited;

		public AddTextDialogFragment(int type, String textToBeEdited) {
			this.type = type;
			this.textToBeEdited = textToBeEdited;
		}

		public AddTextDialogFragment() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			editText = new EditText(getActivity());
			if (!TextUtils.isEmpty(textToBeEdited)) {
				editText.setText(textToBeEdited);
			}
			return new AlertDialog.Builder(getActivity())
					.setTitle(
							type == TYPE_CREATE ? R.string.add_new_text_title
									: R.string.edit_text_title)
					.setPositiveButton(getString(android.R.string.ok), this)
					.setNegativeButton(getString(android.R.string.cancel), null)
					.setView(editText).create();
		}

		@Override
		public void onClick(DialogInterface dialog, int position) {
			String textToBeAdded = editText.getText().toString();
			switch (type) {
			case TYPE_CREATE:
				addText(textToBeAdded);
				break;
			case TYPE_EDIT:
				updateText(textToBeAdded);
				break;
			}
			dialog.dismiss();
		}

		private void updateText(String textToBeAdded) {
			if (textToBeAdded != null && !textToBeAdded.trim().isEmpty()
					&& !CopiedApplication.clipboarStringsContain(textToBeAdded)) {
				Log.i(TAG_LOG, "updating text: " + textToBeAdded);
				CopiedApplication.replaceString(textToBeEdited, textToBeAdded);
				adapter.clear();
				adapter.addAll(CopiedApplication.getList());
				adapter.notifyDataSetChanged();
				CopiedApplication.setCurrentSelectedString(textToBeAdded);
				sendCommandChangeNotif();
			} else if (textToBeAdded != null && !textToBeAdded.trim().isEmpty()) {
				Log.i(TAG_LOG, "update already exists: " + textToBeAdded);
				Toast.makeText(getActivity(),
						getString(R.string.text_already_exists),
						Toast.LENGTH_LONG).show();
			} else {
				Log.i(TAG_LOG, "update empty");
				Toast.makeText(getActivity(), getString(R.string.text_empty),
						Toast.LENGTH_LONG).show();
			}
		}

		private void addText(String textToBeAdded) {
			if (textToBeAdded != null && !textToBeAdded.trim().isEmpty()
					&& !CopiedApplication.clipboarStringsContain(textToBeAdded)) {
				Log.i(TAG_LOG, "adding text: " + textToBeAdded);
				adapter.add(textToBeAdded);
				adapter.notifyDataSetChanged();
				CopiedApplication.addStringToClipboard(textToBeAdded);
				sendCommandCopy();
			} else if (textToBeAdded != null && !textToBeAdded.trim().isEmpty()) {
				Log.i(TAG_LOG, "already exists: " + textToBeAdded);
				Toast.makeText(getActivity(),
						getString(R.string.text_already_exists),
						Toast.LENGTH_LONG).show();
			} else {
				Log.i(TAG_LOG, "empty");
				Toast.makeText(getActivity(), getString(R.string.text_empty),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
