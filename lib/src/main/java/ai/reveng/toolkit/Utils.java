package ai.reveng.toolkit;

import java.util.List;

public class Utils {

	public static String formatBinaryEmbeding(List<List<Double>> listList) {
		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < listList.size(); i++) {
			List<Double> innerList = listList.get(i);
			stringBuilder.append("[");

			for (int j = 0; j < innerList.size(); j++) {
				stringBuilder.append(innerList.get(j));

				if (j < innerList.size() - 1) {
					stringBuilder.append(", ");
				}
			}

			stringBuilder.append("]");

			if (i < listList.size() - 1) {
				stringBuilder.append(", ");
			}
		}

		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
