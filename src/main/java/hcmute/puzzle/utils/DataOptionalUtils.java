package hcmute.puzzle.utils;

import java.util.List;

public class DataOptionalUtils<T> {

	public T getFirstElementFromList(List<T> list) {
		T firstElement = getElementFromListWithIndex(0, list);
		return firstElement;
	}

	public T getElementFromListWithIndex(int index, List<T> list) {
		T firstElement = null;
		if (list != null && !list.isEmpty() && list.size() > index) {
			firstElement = list.get(0);
		}
		return firstElement;
	}

	public T getObject(int index, List<T> list) {
		T firstElement = null;
		if (list != null && !list.isEmpty() && list.size() > index) {
			firstElement = list.get(0);
		}
		return firstElement;
	}
}
