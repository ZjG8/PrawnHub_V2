package com.example.prawnhub_v2;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnavailableGrowthPredictionServiceTest {
    @Test
    public void doesNotFabricatePredictionWhenNoTrainedModelExists() {
        GrowthPredictionService.PredictionResult result = new UnavailableGrowthPredictionService().predict(
                new GrowthPredictionService.PredictionInput(12, 90, Collections.emptyList()));

        assertFalse(result.available);
        assertTrue(result.message.contains("no trained model"));
    }
}
