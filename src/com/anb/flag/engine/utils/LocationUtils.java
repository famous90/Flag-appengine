package com.anb.flag.engine.utils;

public class LocationUtils {
	public static final double NEAR_DISTANCE_DEGREE = 0.0089524654;

	public static boolean isNearby(double latx, double lonx, double laty, double lony) {
		if (distance(latx, lonx, laty, lony) < NEAR_DISTANCE_DEGREE)
			return true;
		else
			return false;
	}

	public static double distance(double latx, double lonx, double laty, double lony) {
		double distance = 0;

		distance = Math.sqrt((latx - laty) * (latx - laty) + (lonx - lony) * (lonx - lony));

		return distance;
	}
}
