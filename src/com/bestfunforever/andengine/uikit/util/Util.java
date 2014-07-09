package com.bestfunforever.andengine.uikit.util;

public class Util {
	public static int min(int x, int y, int m, int n) {
		int min = x;
		if (y < min)
			min = y;
		if (m < min)
			min = m;
		if (n < min)
			min = n;
		return min;
	}

	public static int max(int x, int y, int m, int n) {
		int min = x;
		if (y > min)
			min = y;
		if (m > min)
			min = m;
		if (n > min)
			min = n;
		return min;
	}
	
	public static float max(float x, float y, float m, float n) {
		float min = x;
		if (y > min)
			min = y;
		if (m > min)
			min = m;
		if (n > min)
			min = n;
		return min;
	}
}
