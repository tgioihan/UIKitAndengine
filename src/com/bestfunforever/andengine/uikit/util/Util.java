package com.bestfunforever.andengine.uikit.util;

import java.util.Random;

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

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
