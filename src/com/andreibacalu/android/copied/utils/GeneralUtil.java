package com.andreibacalu.android.copied.utils;

import java.util.List;

public class GeneralUtil {

	public static boolean areListsContainingSameElements(List<String> list1, List<String> list2) {
		if (list1.size() != list2.size()) {
			return false;
		} else {
			for (int i = 0; i < list1.size(); i++) {
				if (!list1.get(i).equals(list2.get(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
}
