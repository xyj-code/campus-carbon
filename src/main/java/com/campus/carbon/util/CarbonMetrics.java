package com.campus.carbon.util;

public final class CarbonMetrics {

    private static final double STEP_TO_CARBON_KG = 0.005 / 100;
    private static final double SPORT_TO_CARBON_KG = 0.1;

    private CarbonMetrics() {
    }

    public static double carbonFromSteps(int steps) {
        return Math.max(steps, 0) * STEP_TO_CARBON_KG;
    }

    public static double carbonFromSportDistance(double distanceKm) {
        return Math.max(distanceKm, 0D) * SPORT_TO_CARBON_KG;
    }

    public static int pointsFromCarbon(double carbonKg) {
        return (int) Math.round(Math.max(carbonKg, 0D) * 100);
    }

    public static double roundCarbon(double carbonKg) {
        return Math.round(carbonKg * 100) / 100.0;
    }
}
