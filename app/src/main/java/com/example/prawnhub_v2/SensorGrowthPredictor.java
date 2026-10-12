package com.example.prawnhub_v2;

import java.util.Collections;
import java.util.List;

/**
 * Legacy water-condition scoring utility. It scores readings against configured targets and
 * must not be presented as prawn growth, harvest readiness, or a machine-learning result.
 */
public final class SensorGrowthPredictor {
    private SensorGrowthPredictor() { }

    public static Prediction predict(List<SensorSample> history, int cultureDays, int targetDays,
                                     float minTemp, float maxTemp, float minTds, float maxTds,
                                     float maxTurbidity) {
        List<SensorSample> samples = history == null ? Collections.emptyList() : history;
        int temperatureGood = 0;
        int tdsGood = 0;
        int turbidityGood = 0;
        for (SensorSample sample : samples) {
            if (sample.temperature >= minTemp && sample.temperature <= maxTemp) temperatureGood++;
            if (sample.tds >= minTds && sample.tds <= maxTds) tdsGood++;
            if (sample.turbidity <= maxTurbidity) turbidityGood++;
        }
        int count = samples.size();
        float temperatureScore = percent(temperatureGood, count);
        float tdsScore = percent(tdsGood, count);
        float turbidityScore = percent(turbidityGood, count);
        float conditionScore = (temperatureScore + tdsScore + turbidityScore) / 3f;
        // Stable water speeds the estimate up to 1.30x; consistently poor water slows it to 0.70x.
        float growthRate = 0.70f + (conditionScore / 100f) * 0.60f;
        int safeTargetDays = Math.max(1, targetDays);
        int estimatedHarvestDays = Math.max(1, Math.round(safeTargetDays / growthRate));
        float progress = Math.min(100f, (Math.max(0, cultureDays) * 100f) / estimatedHarvestDays);
        return new Prediction(conditionScore, temperatureScore, tdsScore, turbidityScore,
                growthRate, estimatedHarvestDays, progress, count);
    }

    private static float percent(int good, int total) {
        return total == 0 ? 0f : (good * 100f) / total;
    }

    public static final class SensorSample {
        public final float temperature;
        public final float tds;
        public final float turbidity;

        public SensorSample(float temperature, float tds, float turbidity) {
            this.temperature = temperature;
            this.tds = tds;
            this.turbidity = turbidity;
        }
    }

    public static final class Prediction {
        public final float conditionScore;
        public final float temperatureScore;
        public final float tdsScore;
        public final float turbidityScore;
        public final float growthRate;
        public final int estimatedHarvestDays;
        public final float progress;
        public final int sampleCount;

        private Prediction(float conditionScore, float temperatureScore, float tdsScore,
                           float turbidityScore, float growthRate, int estimatedHarvestDays,
                           float progress, int sampleCount) {
            this.conditionScore = conditionScore;
            this.temperatureScore = temperatureScore;
            this.tdsScore = tdsScore;
            this.turbidityScore = turbidityScore;
            this.growthRate = growthRate;
            this.estimatedHarvestDays = estimatedHarvestDays;
            this.progress = progress;
            this.sampleCount = sampleCount;
        }
    }
}
