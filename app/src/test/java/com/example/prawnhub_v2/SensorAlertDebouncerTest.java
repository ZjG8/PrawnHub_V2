package com.example.prawnhub_v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SensorAlertDebouncerTest {
    @Test public void briefOutOfRangeReadingDoesNotAlert() {
        SensorAlertDebouncer.State state = SensorAlertDebouncer.State.initial();
        SensorAlertDebouncer.Decision first = SensorAlertDebouncer.evaluate(state, true, 2);
        SensorAlertDebouncer.Decision recovered = SensorAlertDebouncer.evaluate(first.state, false, 2);

        assertFalse(first.shouldAlert);
        assertFalse(recovered.shouldAlert);
    }

    @Test public void sustainedOutOfRangeReadingAlertsOnlyOnceUntilRecovery() {
        SensorAlertDebouncer.Decision first = SensorAlertDebouncer.evaluate(SensorAlertDebouncer.State.initial(), true, 2);
        SensorAlertDebouncer.Decision second = SensorAlertDebouncer.evaluate(first.state, true, 2);
        SensorAlertDebouncer.Decision third = SensorAlertDebouncer.evaluate(second.state, true, 2);

        assertFalse(first.shouldAlert);
        assertTrue(second.shouldAlert);
        assertFalse(third.shouldAlert);
    }
}
