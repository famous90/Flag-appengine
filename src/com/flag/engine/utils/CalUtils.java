package com.flag.engine.utils;

public class CalUtils {
	public static float toNumber(String str) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++)
			try {
				int n = Integer.valueOf(str.substring(i, i + 1));
				sb.append(n);
			} catch (NumberFormatException e) {
				if (str.charAt(i) == '.')
					sb.append('.');
			}

		if (sb.charAt(0) == '.')
			sb.insert(0, '0');

		if (sb.charAt(sb.length() - 1) == '.')
			sb.append(0);

		return Float.valueOf(sb.toString());
	}

	public static int discountRate(String oldPrice, String price) {
		float a = toNumber(oldPrice);
		float b = toNumber(price);
		return (int) (((a - b) / a) * 100);
	}
}
