Codex Prompt: Update PrawnHub Growth Trend and Default Sensor Conditions

I want you to update the existing Growth screen and Farmer Dashboard in my PrawnHub Android application.

Inspect the current Android project, Firebase Realtime Database structure, pond settings, sensor calibration logic, and ESP32 code before making changes.

Important: Modify the existing files and functionality. Do not create duplicate screens, Activities, layouts, Firebase nodes, or sensor-calibration logic. Preserve the existing navigation, admin/farmer permissions, dark mode, culture start date, dynamic harvest prediction, and Harvest workflow.

1. Growth Screen — Rename Sensor Condition Trend to Growth Trend

In the Growth screen, rename the existing Sensor Condition Trend title to:

Growth Trend

Keep the existing chart component and layout where possible. Change its data and calculation logic so the trend represents the development of pond conditions relevant to prawn growth, based on the actual sensor readings and calibration settings configured by the farmer or administrator.

Data sources

Inspect and use the existing Firebase paths:

/ShrimpHub/temperature — current temperature.

/ShrimpHub/tds — current TDS/salinity reading.

/ShrimpHub/turbidity — current turbidity.

/ShrimpHub/waterLevel — current water-level reading.

/ShrimpHub/history — historical sensor readings.

/settings — pond settings, calibrated values, safe ranges, and sensor thresholds.

/growth/start_date — actual culture start date.

Confirm the real field names and data types in the existing project. Do not assume that the paths contain additional fields that have not been implemented.

Growth Trend calculation

The chart should reflect whether pond conditions are moving toward or staying within the configured ranges suitable for the culture.

Read the calibration and safe-range values from Firebase pond settings.

Use the same configured values and condition-classification logic as the Farmer Dashboard.

Recalculate the condition results when pond settings or calibration values change.

Use actual historical sensor readings to calculate each historical data point.

Compare historical readings with the calibration and safe ranges applicable to the relevant readings.

Use consistent rules for temperature, TDS/salinity, turbidity, and water level wherever these measurements are part of the configured growth-condition calculation.

Do not use hardcoded target values if the corresponding settings already exist in Firebase.

Do not create fake readings, sample chart points, or fabricated growth percentages.

If calibration settings are missing or invalid, display a clear configuration message instead of producing a misleading trend.

If sensor history is missing or insufficient, display an empty state explaining that more valid sensor data is required.

If a culture start date has not been saved, keep the chart empty and do not display a line chart.

Once a valid culture start date is saved, include only valid history records from that date onward.

Important distinction

The chart should represent a calculated growth-condition trend, not directly measured prawn size, weight, biomass, or biological growth.

If the project does not have a validated model connecting sensor conditions to actual prawn growth, do not label a condition score as measured growth. Use the Growth Trend title, with a clear subtitle such as:

Pond conditions based on calibrated sensor readings

If the existing project has a genuine machine-learning growth model, inspect its inputs and outputs and integrate it appropriately without replacing it with a simple condition score.

Chart behavior

Use real timestamps and historical Firebase records.

Ensure that a point represents the sensor-condition calculation for its recorded time.

Update the chart when new valid history arrives or pond settings change.

Handle missing values, invalid readings, duplicate timestamps, and out-of-order history records.

Keep the chart readable and consistent with the existing UI and dark mode.

If the chart uses a normalized condition score, clearly label the scale and explain its meaning.

Do not automatically increase the growth score just because time has passed.

Do not claim that improved sensor conditions guarantee faster growth or an earlier safe harvest.

2. Use Firebase Pond Settings as the Source of Calibration

The pond settings configured in the app must be the source of truth for sensor-condition calculations.

Inspect the existing Pond Settings screen and its Firebase save/load logic.

Ensure that:

The configured temperature range is saved to Firebase.

The configured TDS/salinity range is saved to Firebase.

The configured turbidity maximum or range is saved to Firebase.

The configured water-level limits are saved to Firebase if they are supported by the existing settings screen.

The Farmer Dashboard reads the saved settings.

The Growth screen reads the same saved settings.

The historical Growth Trend uses the appropriate calibration and range values consistently.

Do not create separate, conflicting thresholds for the Dashboard and Growth screen.

If the settings are changed, refresh the displayed sensor conditions and recalculate the trend according to a clearly defined rule. Inspect the project's history and settings structure before deciding whether historical readings should be recalculated using the current calibration or the calibration that was active when each reading was recorded. Preserve existing behavior where possible and document any necessary change.

If the project already records sensor calibration values, calibration status, or sensor-specific metadata, reuse those fields instead of creating duplicates.

3. Default Sensor Conditions to Normal

Update both the Farmer Dashboard and the Growth screen's Sensor Conditions section.

The initial UI should show these conditions as Normal:

Temperature: Normal

TDS/Salinity: Normal

Turbidity: Normal

Water Level: Normal

Apply this default display behavior consistently to both screens.

Requirements

On initial screen loading, display the Normal condition as the default UI state while sensor data is being initialized.

Do not overwrite valid Firebase readings with artificial values.

Continue reading the real sensor values from Firebase.

Once actual readings arrive, evaluate them against the pond settings and update the condition labels accordingly.

If readings are outside configured safe ranges, update the corresponding condition to Warning, Critical, or the existing appropriate status.

Use the same classification logic on the Dashboard and Growth screen.

Avoid showing conflicting statuses for the same reading.

Preserve the actual numerical sensor readings and units.

Do not hardcode normal temperature, TDS, turbidity, or water-level measurements just to make the UI appear healthy.

A default Normal display is only an initial UI state, not proof that the physical pond is safe.

Distinguish initialization from actual sensor status

Inspect how the app currently detects whether the ESP32 and sensors are online.

If the sensors have not reported yet, retain the requested initial Normal display but make the internal state distinguish between:

Initial UI state.

Valid sensor data received.

Sensor unavailable or stale.

Actual readings outside configured limits.

If the existing architecture supports a subtle loading or connection indicator, use it to prevent the farmer from mistaking an uninitialized reading for a verified safe pond condition. Once sensor status is known, display the correct live condition.

Do not allow the default Normal state to override a known sensor failure, stale-reading warning, or actual out-of-range reading.

4. Keep Pond Status Consistent with the Sensor Conditions

The overall Pond Status card on the Farmer Dashboard must continue to derive from the individual sensor conditions.

Healthy should be shown only when the available, valid readings meet the configured limits and the existing pond-status rules allow it.

Monitor conditions should be shown when the existing rules classify one or more readings as needing attention.

Action needed should be shown when critical readings or the existing critical-condition rules require action.

Do not permanently force the overall Pond Status to Healthy because the initial sensor-condition labels default to Normal.

Keep the existing pond-status rules unless inspection identifies a genuine inconsistency that needs correction.

If the existing project counts issues to determine overall status, preserve that logic and ensure it does not count uninitialized default UI states as verified sensor readings.

5. Preserve Existing Growth and Harvest Functionality

Do not break the existing features from the previous Growth Dashboard implementation:

Culture Start Date and /growth/start_date.

Culture-day calculation.

Dynamic harvest-date prediction using the existing supported model or configured harvest duration.

Harvest readiness notification.

Harvest confirmation button and completed-culture recording.

Growth Prediction and Growth Progress.

Culture Information.

Sensor Conditions.

Growth Trend empty state before culture starts.

Existing Firebase history and reporting functionality.

The Growth Trend must not replace or overwrite the machine-learning model's harvest prediction. These are separate features unless the existing model explicitly connects them.

6. Firebase and Code Quality Requirements

Inspect the actual Firebase schema before making changes.

Preserve /ShrimpHub, /ShrimpHub/history, /growth/start_date, and the existing settings structure.

Reuse the current data models, repositories, Firebase listeners, and condition-classification methods where possible.

Do not add duplicate Firebase listeners or redundant settings nodes.

Handle missing settings, permission failures, invalid readings, loading states, and network errors.

Ensure changes to pond settings propagate correctly to both screens.

Keep the UI responsive and compatible with the current Android SDK, app architecture, and dark mode.