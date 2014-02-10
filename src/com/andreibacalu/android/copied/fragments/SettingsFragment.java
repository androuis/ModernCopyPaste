package com.andreibacalu.android.copied.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.andreibacalu.android.copied.R;
import com.andreibacalu.android.copied.services.ChangeNotificationTextService;
import com.andreibacalu.android.copied.utils.SharedPreferencesUtil;

public class SettingsFragment extends Fragment implements
		OnCheckedChangeListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setViewChecked(R.id.settings_display_toast);
		if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 16) {
			setViewChecked(R.id.settings_display_notification);
			setViewChecked(R.id.settings_notification_big_view);
		} else {
			view.findViewById(R.id.settings_display_notification)
					.setVisibility(View.GONE);
			view.findViewById(R.id.settings_display_notification_delimiter)
					.setVisibility(View.GONE);
			view.findViewById(R.id.settings_notification_big_view)
					.setVisibility(View.GONE);
			view.findViewById(R.id.settings_notification_big_view_delimiter)
					.setVisibility(View.GONE);
		}
		setViewChecked(R.id.settings_service_close_app);
		setViewChecked(R.id.settings_service_start_at_boot);
		setViewChecked(R.id.settings_notification_no_clear);
	}

	private void updateSettingFromView(int settingViewId, boolean isChecked) {
		SharedPreferencesUtil
				.getInstance(getActivity().getApplicationContext()).setSetting(
						getSettingKeyForViewId(settingViewId), isChecked);
		if (settingViewId == R.id.settings_display_notification
				|| settingViewId == R.id.settings_notification_no_clear
				|| settingViewId == R.id.settings_notification_big_view) {
			Intent intent = new Intent(getActivity().getBaseContext(),
					ChangeNotificationTextService.class);
			intent.putExtra(
					ChangeNotificationTextService.INTENT_COMMAND_TYPE,
					ChangeNotificationTextService.INTENT_COMMAND_TYPE_CHANGE_NOTIF);
			getActivity().getBaseContext().startService(intent);
		}
	}

	private void setViewChecked(int settingViewId) {
		View view = getView();
		if (view != null) {
			View checkBox = view.findViewById(settingViewId);
			if (checkBox != null && checkBox instanceof CheckBox) {
				((CheckBox) checkBox).setChecked(SharedPreferencesUtil
						.getInstance(getActivity().getApplicationContext())
						.getSetting(getSettingKeyForViewId(settingViewId)));
				((CheckBox) checkBox).setOnCheckedChangeListener(this);
			}
		}
	}

	private String getSettingKeyForViewId(int id) {
		if (id == R.id.settings_display_toast) {
			return SharedPreferencesUtil.SETTING_DISPLAY_TOAST;
		} else if (id == R.id.settings_display_notification) {
			return SharedPreferencesUtil.SETTING_DISPLAY_NOTIFICATION;
		} else if (id == R.id.settings_service_close_app) {
			return SharedPreferencesUtil.SETTING_SERVICE_CLOSE;
		} else if (id == R.id.settings_service_start_at_boot) {
			return SharedPreferencesUtil.SETTING_SERVICE_BOOT;
		} else if (id == R.id.settings_notification_no_clear) {
			return SharedPreferencesUtil.SETTING_NOTIFICATION_NO_CLEAR;
		} else if (id == R.id.settings_notification_big_view) {
			return SharedPreferencesUtil.SETTING_NOTIFICATION_BIG_VIEW;
		}
		return null;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean value) {
		updateSettingFromView(view.getId(), value);
	}
}
