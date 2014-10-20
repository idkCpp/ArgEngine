package com.rundumsweb.servers.argengine;

public class ArgEngineArrayUtils {

	public static String join(String[] strs) {
		return join(strs, " ");
	}

	public static String join(String[] strs, String blankFiller) {
		StringBuffer sb = new StringBuffer();
		for (String s : strs) {
			sb.append(blankFiller);
			sb.append(s);
		}
		return sb.toString().substring(blankFiller.length());
	}

	public static boolean contains(String[] strs, String str) {
		return contains(strs, str, false);
	}

	public static boolean contains(String[] strs, String str, boolean ignoreCase) {
		boolean contains = false;
		for (String s : strs) {
			if (ignoreCase) {
				if (s.equalsIgnoreCase(str))
					contains = true;
			} else {
				if (s.equals(str))
					contains = true;
			}
		}
		return contains;
	}
}
