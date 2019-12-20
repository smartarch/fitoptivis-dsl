package cz.cuni.mff.fitoptivis.modelExtractor;

import java.util.List;

public class ListUtils {
	public static <T> T getLast(List<T> list) {
		if (list.isEmpty())
			return null;
		
		return list.get(list.size() - 1);
	}
}
