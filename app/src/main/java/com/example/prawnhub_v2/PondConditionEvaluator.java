package com.example.prawnhub_v2;

/** Shared, settings-driven pond-condition rules for Dashboard and Growth Trend. */
public final class PondConditionEvaluator {
    private PondConditionEvaluator() { }

    public static Result evaluate(Float temperature, Float tds, Float turbidity, Float waterLevelCm,
                                  float minTemp, float maxTemp, float minTds, float maxTds,
                                  float minTurb, float maxTurb, float overflowFeet) {
        boolean settingsValid = minTemp <= maxTemp && minTds <= maxTds && minTurb <= maxTurb && overflowFeet >= 0;
        if (!settingsValid) return Result.unavailable();
        Status temp = range(temperature, minTemp, maxTemp, Status.WARNING);
        Status salinity = range(tds, minTds, maxTds, Status.WARNING);
        Status turbidityStatus = range(turbidity, minTurb, maxTurb, Status.CRITICAL);
        Float waterFeet = waterLevelCm == null ? null : waterLevelCm / 30.48f;
        Status water = waterFeet == null ? Status.UNAVAILABLE : waterFeet > overflowFeet ? Status.WARNING : Status.NORMAL;
        int available = count(temp, salinity, turbidityStatus, water);
        int good = good(temp) + good(salinity) + good(turbidityStatus) + good(water);
        return new Result(true, temp, salinity, turbidityStatus, water, available, available == 0 ? 0 : good * 100f / available);
    }

    private static Status range(Float value, float low, float high, Status bad) {
        return value == null ? Status.UNAVAILABLE : value >= low && value <= high ? Status.NORMAL : bad;
    }
    private static int count(Status... statuses) { int count=0; for (Status status:statuses) if(status!=Status.UNAVAILABLE) count++; return count; }
    private static int good(Status status) { return status==Status.NORMAL ? 1 : 0; }
    public enum Status { NORMAL, WARNING, CRITICAL, UNAVAILABLE }
    public static final class Result {
        public final boolean settingsValid; public final Status temperature, tds, turbidity, water; public final int availableCount; public final float score;
        private Result(boolean valid, Status temp, Status tds, Status turbidity, Status water, int available, float score) { settingsValid=valid; temperature=temp;this.tds=tds;this.turbidity=turbidity;this.water=water;availableCount=available;this.score=score; }
        private static Result unavailable() { return new Result(false,Status.UNAVAILABLE,Status.UNAVAILABLE,Status.UNAVAILABLE,Status.UNAVAILABLE,0,0); }
    }
}
