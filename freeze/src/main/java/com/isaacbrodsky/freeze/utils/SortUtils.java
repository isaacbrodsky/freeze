/**
 * 
 */
package com.isaacbrodsky.freeze.utils;

import java.util.List;

/**
 * @author isaac
 * 
 */
public final class SortUtils {
	private SortUtils() {

	}

	private static void swap(List arr, int a, int b) {
		// this is the implementation in Collections.swap
		arr.set(b, arr.set(a, arr.get(b)));
	}

	private static int partition(List<? extends Comparable> arr, int left,
			int right) {
		int l = left, r = right;
		Comparable pivot = arr.get((left + right) / 2);

		while (l <= r) {
			while (arr.get(l).compareTo(pivot) < 0)
				l++;

			while (arr.get(r).compareTo(pivot) > 0)
				r--;

			if (l <= r) {
				swap(arr, l, r);
				l++;
				r--;
			}
		}

		return l;
	}

	public static void quickSort(List<? extends Comparable> arr) {
		quickSort(arr, 0, arr.size() - 1);
	}

	/**
	 * Why write my own quicksort? Why not! Collections.sort is quite possibly a
	 * faster sort algorithm, it mentions sorting linked list is quite
	 * inefficient and that is precisely what this code does.
	 * 
	 * @param arr
	 * @param left
	 * @param right
	 */
	public static void quickSort(List<? extends Comparable> arr, int left,
			int right) {
		if (arr.size() < 1)
			return;
		int index = partition(arr, left, right);
		if (left < index - 1)
			quickSort(arr, left, index - 1);
		if (index < right)
			quickSort(arr, index, right);
	}
}
