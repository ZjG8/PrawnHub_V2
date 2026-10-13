package com.example.prawnhub_v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A transparent historical estimate, not a machine-learning model. It derives an estimated
 * culture duration solely from real, completed culture records.
 */
public final class HarvestPredictionEstimator {
    private HarvestPredictionEstimator() { }

    public static Estimate fromCompletedCultures(List<Integer> durations) {
        List<Integer> valid = new ArrayList<>();
        if (durations != null) {
            for (Integer duration : durations) {
                if (duration != null && duration > 0) valid.add(duration);
            }
        }
        if (valid.size() < 3) {
            return Estimate.unavailable("Growth Progress: Unavailable - more completed culture and harvest data is needed.");
        }
        Collections.sort(valid);
        int middle = valid.size() / 2;
        int median = valid.size() % 2 == 0 ? Math.round((valid.get(middle - 1) + valid.get(middle)) / 2f) : valid.get(middle);
        return Estimate.available(median, valid.size());
    }

    public static final class Estimate {
        public final boolean available;
        public final int durationDays;
        public final int sampleCount;
        public final String message;

        private Estimate(boolean available, int durationDays, int sampleCount, String message) {
            this.available = available;
            this.durationDays = durationDays;
            this.sampleCount = sampleCount;
            this.message = message;
        }

        private static Estimate available(int durationDays, int sampleCount) {
            return new Estimate(true, durationDays, sampleCount, "Historical harvest estimate (not ML)");
        }

        private static Estimate unavailable(String message) {
            return new Estimate(false, 0, 0, message);
        }
    }
}
