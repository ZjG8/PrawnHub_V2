package com.example.prawnhub_v2;

/** Requires consecutive out-of-range samples before issuing one alert. */
public final class SensorAlertDebouncer {
    private SensorAlertDebouncer() { }

    public static Decision evaluate(State previous, boolean outOfRange, int requiredConsecutiveSamples) {
        if (!outOfRange) return new Decision(State.initial(), false);
        int consecutive = previous.consecutiveOutOfRange + 1;
        boolean shouldAlert = !previous.alerted && consecutive >= requiredConsecutiveSamples;
        return new Decision(new State(consecutive, previous.alerted || shouldAlert), shouldAlert);
    }

    public static final class State {
        final int consecutiveOutOfRange;
        final boolean alerted;

        private State(int consecutiveOutOfRange, boolean alerted) {
            this.consecutiveOutOfRange = consecutiveOutOfRange;
            this.alerted = alerted;
        }

        public static State initial() { return new State(0, false); }
    }

    public static final class Decision {
        public final State state;
        public final boolean shouldAlert;

        private Decision(State state, boolean shouldAlert) {
            this.state = state;
            this.shouldAlert = shouldAlert;
        }
    }
}
