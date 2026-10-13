package com.example.prawnhub_v2;

/** Default service until a trained, evaluated model and its preprocessing contract are supplied. */
public final class UnavailableGrowthPredictionService implements GrowthPredictionService {
    @Override
    public PredictionResult predict(PredictionInput input) {
        if (input.history.isEmpty()) {
            return PredictionResult.unavailable("ML prediction unavailable: no trained model is configured; sensor history is also unavailable.");
        }
        return PredictionResult.unavailable("ML prediction unavailable: no trained model is configured in this app build.");
    }
}
