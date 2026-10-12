package com.example.prawnhub_v2;

/**
 * Configured-duration fallback for projects without a validated biological growth model.
 * It represents the farmer's configured harvest criterion, not measured prawn biomass.
 */
public final class HarvestPolicy {
    private HarvestPolicy() { }

    public static Decision evaluate(boolean hasActiveCulture, int cultureDays, int targetDays) {
        if (!hasActiveCulture || targetDays < 1) {
            return new Decision(false, 0, false);
        }
        int progress = Math.min(100, Math.max(0, Math.round(cultureDays * 100f / targetDays)));
        return new Decision(true, progress, cultureDays >= targetDays);
    }

    public static final class Decision {
        public final boolean hasConfiguredTarget;
        public final int progressPercent;
        public final boolean readyToHarvest;

        private Decision(boolean hasConfiguredTarget, int progressPercent, boolean readyToHarvest) {
            this.hasConfiguredTarget = hasConfiguredTarget;
            this.progressPercent = progressPercent;
            this.readyToHarvest = readyToHarvest;
        }
    }
}
