package com.example.prawnhub_v2;

/** Maps a physical pump number to its Firebase control key. */
public final class PumpControlMapper {
    private PumpControlMapper() {
    }

    public static String firebaseKeyFor(int pumpNumber) {
        switch (pumpNumber) {
            case 1:
                return "pump1";
            case 2:
                return "pump2";
            case 3:
                return "pump3";
            case 4:
                return "pump4";
            default:
                throw new IllegalArgumentException("Unsupported pump number: " + pumpNumber);
        }
    }
}
