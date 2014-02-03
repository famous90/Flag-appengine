package com.flag.engine.utils;

public class LocationUtils {
	public static final double NEAR_DISTANCE_DEGREE = 0.0089524654;
	public static final double CLOSE_DISTANCE_DEGREE = 0.0044762327;
	public static final double AT_DISTANCE_DEGREE = 0.0022381163;

	public static boolean isNear(double latx, double lonx, double laty, double lony) {
		if (distance(latx, lonx, laty, lony) < NEAR_DISTANCE_DEGREE)
			return true;
		else
			return false;
	}

	public static boolean isClose(double latx, double lonx, double laty, double lony) {
		if (distance(latx, lonx, laty, lony) < CLOSE_DISTANCE_DEGREE)
			return true;
		else
			return false;
	}

	public static boolean isAt(double latx, double lonx, double laty, double lony) {
		if (distance(latx, lonx, laty, lony) < AT_DISTANCE_DEGREE)
			return true;
		else
			return false;
	}

	public static double distance(double latx, double lonx, double laty, double lony) {
		return Math.sqrt((latx - laty) * (latx - laty) + (lonx - lony) * (lonx - lony));
	}
}
