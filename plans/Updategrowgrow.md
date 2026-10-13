# PrawnHub Growth Screen — Machine Learning Integration and Sensor-Based Growth Prediction

You are working on my existing Android Studio project, **PrawnHub_V2**, for our BSIT capstone project:

**PrawnHub: Smart Aquaculture Monitoring and Control System for Indoor Prawn Farming in Brgy. Rizal, Lopez, Quezon.**

We are now preparing to deploy the Growth screen. I want the Growth UI to use real Firebase sensor data and a genuine, verifiable machine-learning model if one exists in the project.

Do not just design the interface. Inspect the existing code, determine whether machine learning is already implemented, connect the required data sources, and implement the missing parts without breaking the current application.

## 1. Inspect the existing project first

Inspect these files and related classes before making changes:

- `GrowthActivity.java`
- `activity_growth.xml`
- Existing growth models, prediction classes, and chart helpers
- Firebase configuration and database listeners
- Any existing ML model, inference code, training script, or model file
- ESP32 sensor data structure
- Settings and sensor-threshold implementation

Search the project for TensorFlow Lite, LiteRT, ML Kit, regression models, prediction functions, trained model files, and any existing growth-prediction logic.

Determine which of the following is true:

1. A trained ML model already exists and can be integrated.
2. Only rule-based calculations exist.
3. No real prediction model exists yet.

Do not claim machine learning is working unless a trained model or a properly implemented, validated ML pipeline is actually present.

## 2. Use actual Firebase data

Connect the Growth screen to the existing Firebase Realtime Database.

Use these paths:

- `/growth/start_date`
- `/growth/status`
- `/growth/harvested_at`
- `/growth/completed_cultures`
- `/growth/reset_cultures`
- `/settings/target_harvest_days`
- `/settings` for configured sensor thresholds
- `/ShrimpHub/temperature`
- `/ShrimpHub/tds`
- `/ShrimpHub/turbidity`
- `/ShrimpHub/waterLevel`
- `/ShrimpHub/history`

Read the actual data structures and types from Firebase before assuming how fields are stored.

Requirements:

- Use live Firebase values for current sensor conditions.
- Use chronological historical samples for trend analysis.
- Handle missing, stale, invalid, and disconnected sensor readings.
- Handle Unix timestamps stored in seconds or milliseconds.
- Do not generate fake training samples or substitute hardcoded readings for real values.
- Do not treat missing data as zero.
- Update the UI when Firebase values change.
- Remove Firebase listeners when the Activity is no longer active to avoid duplicate listeners and memory leaks.

If the existing Firebase security rules or data structure prevent reading a path, report the issue rather than hiding it.

## 3. Implement genuine ML-based growth prediction

The goal is to estimate when the current prawn culture may reach its target harvest period based on real sensor conditions and available historical data.

First, verify whether a usable trained model exists.

### If a trained model already exists

- Identify the model architecture, input features, output, preprocessing, and model version.
- Confirm that the model can run on Android or through the project's existing inference service.
- Feed it correctly prepared sensor data and relevant culture information.
- Validate input shapes, units, normalization, and missing-value handling.
- Connect its actual output to the Growth UI.
- Report model errors and unavailable predictions clearly.
- Do not silently replace model inference with a fixed formula if the model fails.

### If no trained model exists

Do not invent a trained model or claim the system has working ML.

Implement the appropriate integration structure and explain what is missing to make genuine ML predictions possible.

Prepare a clearly separated prediction interface, such as `GrowthPredictionService`, that can support a trained model later.

If a rule-based estimate is implemented temporarily, label it clearly as **rule-based**, not machine learning.

Explain what labeled training data is needed to predict actual growth or harvest readiness, such as culture duration, sensor history, species, stocking density, feeding information, survival data, and actual harvest outcomes, depending on what is available.

Do not train or evaluate a model using fabricated results.

## 4. Distinguish growth prediction from sensor-condition scoring

The app must distinguish these outputs:

**A. Culture duration and target date**

- Calculate elapsed culture days using the saved start date.
- Read `target_harvest_days` from Firebase settings.
- Estimate the target date from the start date and configured duration.
- Display the remaining days until the target date.
- Explain that the configured target date is not a biological guarantee.

**B. Sensor-condition score**

Evaluate current temperature, TDS/salinity, turbidity, and water level using verified species-appropriate thresholds configured in the app.

- Read thresholds from Firebase settings where available.
- Identify readings as Normal, Warning, Critical, or Unavailable.
- Make missing or stale readings visibly unavailable.
- Create a condition score only when the scoring method is defined and sufficient data is available.
- Explain how the score is calculated.
- Do not call this score a measured growth percentage or measured prawn biomass.

**C. ML growth prediction**

Only display an ML prediction when actual model inference succeeds and its inputs pass validation.

If the model supports it, display:

- Estimated harvest date or predicted culture duration
- Estimated remaining days
- Prediction confidence or uncertainty, if the model actually provides a valid measure
- Prediction timestamp and model version
- A short explanation of the data used

Never invent a confidence percentage or make a prediction solely from temperature, TDS, and turbidity if the model has not been trained and validated for that task.

## 5. Update the Growth UI

Preserve the existing PrawnHub design and bottom navigation.

### Growth Prediction

Display:

- Current culture status
- Culture progress based on elapsed days versus configured target duration
- Configured target harvest duration
- Estimated target harvest date
- Predicted harvest date, if a validated ML model is available
- Clear ML or rule-based labeling

The circular progress indicator must reflect the defined duration calculation, not an unsupported claim about actual prawn growth.

### Culture Information

Display:

- Culture start date
- Target harvest date
- Elapsed culture days
- Remaining days, when applicable
- Current culture stage
- Harvested status when a culture has been completed

### Sensor Conditions

Display live readings for:

- Temperature in °C
- TDS or salinity, using the correct unit and calibration
- Turbidity in NTU only if the sensor conversion is calibrated
- Water level using the actual configured unit

Show each sensor's condition and freshness. Do not describe raw TDS readings as salinity in ppt unless a valid conversion is implemented.

### Growth Analysis

Display an explanation based on actual available information.

Examples:

- “Target harvest date is estimated from the configured culture duration.”
- “Prediction unavailable because the trained model is not configured.”
- “Sensor-condition assessment is incomplete because turbidity data is unavailable.”
- “The ML estimate uses validated sensor history and culture information.”

Only show explanations that match the actual application state.

### Growth Trend

- Plot chronological sensor-condition scores or model predictions only when valid data exists.
- Label the y-axis and chart meaning clearly.
- Do not plot fake growth values.
- Do not present sensor-condition scores as actual biomass or prawn growth.
- Display a useful empty state when there is insufficient data.

## 6. Culture lifecycle actions

Implement or preserve these actions:

### Set Start Date / Start New Culture

- Validate the start date.
- Reject future dates.
- Save the date and active status to Firebase.
- Confirm before replacing an existing active culture.
- Preserve previous completed-culture records.

### Harvest

- Enable only when the configured duration has been reached and the application permits a harvest review.
- Show a confirmation dialog.
- Save the harvested status, harvest timestamp, and completed-culture record.
- Prevent duplicate harvest submissions.
- Do not claim the prawns are biologically ready solely because the target duration has elapsed.

### Reset Culture

- Require confirmation.
- Archive the reset event.
- Clear only the active culture fields that must be reset.
- Preserve sensor history and completed-culture records.

Use safe Firebase updates so that unrelated paths are not overwritten.

## 7. ML validation and deployment checks

Before claiming that ML works, verify the following:

- The model file or inference endpoint exists.
- Model inputs match the actual sensor units and expected feature order.
- Input preprocessing matches training.
- Missing sensor data is handled safely.
- Inference returns a valid output.
- The output is connected to the Growth UI.
- Predictions are timestamped and traceable to a model version.
- Model performance has been evaluated using genuine labeled validation data.
- Invalid or unavailable predictions do not fall back to fake values.

If no trained model or sufficient labeled data exists, finish the Firebase integration and UI behavior, clearly report ML as not yet validated, and document what is needed to complete it.

## 8. Error handling and logging

Add useful logs for:

- Firebase read and write failures
- Sensor readings and freshness
- Culture start and harvest transitions
- Prediction input validation
- ML inference success or failure
- Prediction timestamp and model version
- Missing model or insufficient data

Do not log passwords, API secrets, or private user information.

The UI should remain usable when Firebase is temporarily unavailable or the ML model cannot generate a prediction.

## 9. Acceptance criteria

The implementation is complete when:

1. GrowthActivity displays actual Firebase data.
2. Culture dates and duration calculations are correct.
3. Sensor readings update the UI without restarting the Activity.
4. The interface distinguishes culture duration, sensor-condition scoring, and ML predictions.
5. A genuine ML model is integrated if one exists and passes validation.
6. If no model exists, the app clearly reports that ML predictions are unavailable instead of faking them.
7. The culture lifecycle actions preserve historical data.
8. Empty, stale, and invalid states are handled correctly.
9. Existing navigation and other PrawnHub screens continue working.
10. The project builds successfully.

## 10. Final report

After implementation, provide:

- Files inspected and modified
- Firebase paths used
- How the culture duration and target harvest date are calculated
- Whether genuine machine learning is implemented, and the evidence
- Model inputs, output, and validation results, if available
- Any missing dataset, calibration, model, or Firebase configuration
- Steps to test the Growth screen with real Firebase readings
- Any remaining deployment blockers

Start by inspecting the existing project. Make the required code changes directly rather than returning only a proposed design or sample snippets.