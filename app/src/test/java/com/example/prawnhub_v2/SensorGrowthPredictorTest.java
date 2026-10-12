package com.example.prawnhub_v2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class SensorGrowthPredictorTest {
    @Test
    public void stableHistoryProducesFastGrowthAndFullConditionScore() {
        SensorGrowthPredictor.Prediction prediction = SensorGrowthPredictor.predict(
                Arrays.asList(new SensorGrowthPredictor.SensorSample(30f, 20f, 20f),
                        new SensorGrowthPredictor.SensorSample(29f, 18f, 35f)),
                30, 75, 28f, 32f, 15f, 25f, 45f);

        assertEquals(100f, prediction.conditionScore, 0.01f);
        assertEquals(1.30f, prediction.growthRate, 0.01f);
        assertEquals(58, prediction.estimatedHarvestDays);
        assertEquals(2, prediction.sampleCount);
    }

    @Test
    public void unstableHistoryReducesConditionScoreAndDelaysHarvest() {
        SensorGrowthPredictor.Prediction prediction = SensorGrowthPredictor.predict(
                Arrays.asList(new SensorGrowthPredictor.SensorSample(35f, 30f, 60f),
                        new SensorGrowthPredictor.SensorSample(30f, 20f, 20f)),
                30, 75, 28f, 32f, 15f, 25f, 45f);

        assertEquals(50f, prediction.conditionScore, 0.01f);
        assertEquals(1.0f, prediction.growthRate, 0.01f);
        assertEquals(75, prediction.estimatedHarvestDays);
    }

    @Test
    public void emptyHistoryIsExplicitlyConservative() {
        SensorGrowthPredictor.Prediction prediction = SensorGrowthPredictor.predict(
                Collections.emptyList(), 10, 75, 28f, 32f, 15f, 25f, 45f);

        assertEquals(0, prediction.sampleCount);
        assertEquals(0f, prediction.conditionScore, 0.01f);
        assertEquals(107, prediction.estimatedHarvestDays);
    }
}
