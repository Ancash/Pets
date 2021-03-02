package de.ancash.pets.utils;

public class MathsUtils {

	public static double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }
}
