package com.example.prawnhub_v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HarvestPolicyTest {
    @Test
    public void configuredDurationProvidesProgressAndHarvestReadiness() {
        HarvestPolicy.Decision decision = HarvestPolicy.evaluate(true, 75, 75);

        assertTrue(decision.hasConfiguredTarget);
        assertEquals(100, decision.progressPercent);
        assertTrue(decision.readyToHarvest);
    }

    @Test
    public void activeCultureBeforeConfiguredDurationIsNotReady() {
        HarvestPolicy.Decision decision = HarvestPolicy.evaluate(true, 74, 75);

        assertEquals(99, decision.progressPercent);
        assertFalse(decision.readyToHarvest);
    }

    @Test
    public void missingStartOrTargetNeverFabricatesProgressOrReadiness() {
        HarvestPolicy.Decision noStart = HarvestPolicy.evaluate(false, 0, 75);
        HarvestPolicy.Decision noTarget = HarvestPolicy.evaluate(true, 10, 0);

        assertFalse(noStart.hasConfiguredTarget);
        assertFalse(noStart.readyToHarvest);
        assertFalse(noTarget.hasConfiguredTarget);
        assertFalse(noTarget.readyToHarvest);
    }
}
