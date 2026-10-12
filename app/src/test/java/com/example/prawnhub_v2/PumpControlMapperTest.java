package com.example.prawnhub_v2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PumpControlMapperTest {
    @Test
    public void mapsEachPumpToItsOwnFirebaseKey() {
        assertEquals("pump1", PumpControlMapper.firebaseKeyFor(1));
        assertEquals("pump2", PumpControlMapper.firebaseKeyFor(2));
        assertEquals("pump3", PumpControlMapper.firebaseKeyFor(3));
        assertEquals("pump4", PumpControlMapper.firebaseKeyFor(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnsupportedPumpNumbers() {
        PumpControlMapper.firebaseKeyFor(5);
    }
}
