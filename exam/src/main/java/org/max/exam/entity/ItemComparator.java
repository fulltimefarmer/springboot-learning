package org.max.exam.entity;

import java.util.Comparator;

/**
 * @author: Max zhou
 * @date: March 8th, 2021
 */
public class ItemComparator implements Comparator<Item> {

	@Override
	public int compare(Item i1, Item i2) {
		int result = 0;
		if(i1.getId() > i2.getId()) {
			result = 1;
		} else if (i1.getId() < i2.getId()) {
			result = -1;
		}
		return result;
	}

}
