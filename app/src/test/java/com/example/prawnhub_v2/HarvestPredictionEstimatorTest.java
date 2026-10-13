package com.example.prawnhub_v2;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HarvestPredictionEstimatorTest {
    @Test public void requiresThreeCompletedCultures() {
        HarvestPredictionEstimator.Estimate estimate = HarvestPredictionEstimator.fromCompletedCultures(Arrays.asList(70, 75));
        assertFalse(estimate.available);
    }

    @Test public void usesMedianOfActualCompletedCultureDurations() {
        HarvestPredictionEstimator.Estimate estimate = HarvestPredictionEstimator.fromCompletedCultures(Arrays.asList(80, 70, 75));
        assertTrue(estimate.available);
        assertEquals(75, estimate.durationDays);
        assertEquals(3, estimate.sampleCount);
    }
}
