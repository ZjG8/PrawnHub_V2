package com.example.prawnhub_v2;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PondConditionEvaluatorTest {
    @Test public void configuredSafeReadingsProduceFullConditionScore() {
        PondConditionEvaluator.Result result=PondConditionEvaluator.evaluate(30f,20f,20f,120f,28,32,15,25,0,45,5);
        assertEquals(PondConditionEvaluator.Status.NORMAL,result.temperature);
        assertEquals(PondConditionEvaluator.Status.NORMAL,result.water);
        assertEquals(100f,result.score,0.01f);
    }
    @Test public void highTurbidityIsCriticalAndCountedInScore() {
        PondConditionEvaluator.Result result=PondConditionEvaluator.evaluate(30f,20f,50f,120f,28,32,15,25,0,45,5);
        assertEquals(PondConditionEvaluator.Status.CRITICAL,result.turbidity);
        assertEquals(75f,result.score,0.01f);
    }
}
