package com.example.prawnhub_v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Boundary for a validated biological-growth model. This app currently has no bundled trained
 * model, so the default implementation reports an unavailable prediction instead of estimating one.
 */
public interface GrowthPredictionService {
    PredictionResult predict(PredictionInput input);

    final class PredictionInput {
        public final int cultureDays;
        public final int targetDays;
        public final List<HistorySample> history;

        public PredictionInput(int cultureDays, int targetDays, List<HistorySample> history) {
            this.cultureDays = cultureDays;
            this.targetDays = targetDays;
            this.history = history == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(history));
        }
    }

    final class HistorySample {
        public final long timestampMillis;
        public final float temperature;
        public final float tds;
        public final float turbidity;
        public final float waterLevel;

        public HistorySample(long timestampMillis, float temperature, float tds, float turbidity, float waterLevel) {
            this.timestampMillis = timestampMillis;
            this.temperature = temperature;
            this.tds = tds;
            this.turbidity = turbidity;
            this.waterLevel = waterLevel;
        }
    }

    final class PredictionResult {
        public final boolean available;
        public final String message;
        public final String modelVersion;

        private PredictionResult(boolean available, String message, String modelVersion) {
            this.available = available;
            this.message = message;
            this.modelVersion = modelVersion;
        }

        public static PredictionResult unavailable(String message) {
            return new PredictionResult(false, message, "");
        }
    }
}
