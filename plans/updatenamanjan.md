Codex Prompt: Update PrawnHub Growth Dashboard and Harvesting Workflow

I want you to modify the existing Growth screen of my PrawnHub Android application. Inspect the current project before making changes. The main files may be GrowthActivity.java or GrowthActivity.kt and activity_growth.xml, but use the actual files found in the project.

Important: Modify the existing implementation. Do not create duplicate Activities, layouts, Firebase paths, or navigation components. Preserve the existing dashboard design, Firebase integration, role permissions, and dark mode.

1. Growth Prediction Section

Update the Growth Prediction section.

Keep the circular progress indicator if it is already implemented.

Remove the condition-based progress text or label.

Display only Growth Progress: followed by the actual calculated percentage when valid data and a supported calculation are available.

Do not display a fabricated percentage or use a sensor-condition score as actual prawn growth.

If the culture has not started, display Not Started and do not show a fabricated progress value.

Keep culture-day counting connected to the saved culture start date in /growth/start_date.

Recalculate displayed values when valid data changes.

2. Culture Information — Dynamic Harvest Date

Update the Culture Information section so the harvesting date is not a fixed or permanently hardcoded date.

The system should dynamically determine the recommended harvesting date using the existing machine-learning model or a properly implemented and validated model that uses actual culture and sensor data.

Data to inspect

Inspect the existing Firebase structure, Android implementation, and ESP32 code before assuming any field names or model behavior.

Use available data such as:

/growth/start_date — actual culture start date.

/ShrimpHub/temperature — current water temperature.

/ShrimpHub/tds — current TDS/salinity reading.

/ShrimpHub/turbidity — current turbidity reading.

/ShrimpHub/history — historical sensor readings.

/settings — configured sensor thresholds and any existing culture-duration or harvesting settings.

Existing machine-learning model outputs, if the project already has a real model.

Dynamic harvesting calculation

Inspect whether a machine-learning model already exists and determine its inputs, outputs, and prediction method.

If the model exists, integrate it with the Growth screen and use actual readings and culture data as required by that model.

Refresh the harvest-date prediction when valid new sensor readings or history records arrive, when the culture start date changes, and when relevant settings change.

Use historical readings as well as current readings when supported by the model.

The objective is to help the farmer identify when prawns may reach the appropriate harvest stage earlier, when supported by actual data. Do not automatically shorten the culture period simply because sensor readings change.

Display the dynamically calculated Harvest Date without using the word Estimate or Estimated in the UI.

Internally, treat the predicted date as a prediction rather than a guarantee.

If the model provides a harvest-readiness probability, maturity prediction, or other output, display it only if that output is genuinely supported by the model.

If the project has no actual machine-learning model, do not label a simple formula or hardcoded date as machine learning. Report that the model is missing and implement only a clearly documented fallback based on an existing, configured target culture duration if one is available.

If neither a valid model nor a configured harvest target exists, display Harvest date unavailable instead of inventing a date.

Handle missing, stale, invalid, or insufficient sensor data without fabricating a prediction.

Culture Information UI

Display:

Culture Start Date: [actual saved date]

Culture Days: [calculated days]

Harvest Date: [dynamic prediction or unavailable message]

Growth Stage: [supported model output or existing validated logic]

Do not show fabricated growth stages, culture days, or harvest dates before a valid culture start date is saved.

3. Sensor Conditions — Default to Zero Until Activated

Update the Sensor Conditions section for situations where the Arduino/ESP32 sensors have not started sending readings.

The displayed sensor values should initially be:

Temperature: 0

Salinity/TDS: 0

Turbidity: 0

Requirements

Use 0 as the initial displayed value when no reading has been received and the sensors have not been activated, as requested.

Keep the UI synchronized with the actual Firebase values under /ShrimpHub.

Do not overwrite a valid Firebase reading with zero simply because the screen reloads.

Distinguish between a true recorded zero and a sensor that has not reported yet internally, using a connection/activation flag, timestamp, or existing sensor-status field if available.

If no sensor-status field exists, inspect the ESP32 implementation and add a compatible status or last-update indicator only if necessary.

Display an appropriate state such as Waiting for sensor data when a sensor has not reported yet.

Do not use missing or inactive readings as real machine-learning inputs unless the model explicitly supports that representation.

Do not generate a misleading healthy condition when a sensor is inactive.

Ensure sensor status and safe-range calculations remain consistent with the Farmer Dashboard.

4. Growth Trend — Empty Before Culture Starts

The Growth Trend section must remain empty until the farmer saves the actual culture start date.

Before culture starts

Do not display a line chart.

Do not generate placeholder points, sample readings, dummy values, or a fake trend.

Display a simple message:

Growth trend will appear after the culture start date is set and sensor history is available.

After culture starts

Load actual records from /ShrimpHub/history.

Exclude all history records timestamped before /growth/start_date.

Display the chart only when enough valid post-start records are available.

If there is no valid history, show an empty-state message explaining that more sensor data is required.

Use actual timestamps and valid recorded values.

If the graph represents sensor conditions rather than measured biological growth, label it clearly as a sensor-condition trend.

Update the trend when valid new history records arrive.

Handle missing readings, duplicate timestamps, out-of-order records, and Firebase errors.

5. Add a Harvest Button and Harvest Notification

Add a new Harvest button to the Growth screen. It should become available when the model or validated harvesting logic determines that the culture has reached its configured harvesting criteria.

Harvest readiness

Determine readiness using the existing machine-learning model or documented, configurable harvest criteria.

Do not enable the button just because a predicted date has arrived if the actual readiness criteria indicate otherwise.

If there is no model output or configured readiness rule, keep the button unavailable and explain that harvesting criteria have not been configured.

When harvesting criteria are met, show a clear notification or banner, such as:

Harvesting time! Your prawns may be ready for harvest. Please review the pond conditions before proceeding.

The notification must be based on actual model output or validated criteria, not a hardcoded date or random condition.

Avoid repeatedly sending duplicate notifications for the same readiness event. Use the existing notification system if available and persist an event identifier or notification state if needed.

When the farmer taps Harvest

Show a confirmation dialog explaining that the farmer is marking the current culture as harvested.

Require confirmation before changing the culture status.

Save the harvest action and timestamp to Firebase using a compatible existing data structure. Inspect the schema before creating any new fields.

Record the current culture's status as harvested.

Stop displaying the active culture as an ongoing, unharvested culture.

Preserve the completed culture's start date, harvest date, and relevant history for future reports and analysis.

Prevent accidental duplicate harvest submissions.

Display a success message only after Firebase confirms that the action was saved.

Handle connection failures and allow the farmer to retry without creating duplicate harvest records.

After harvesting

Display Culture Harvested with the recorded harvest date.

Stop active-culture progress and readiness notifications for that completed culture.

Do not delete the culture's sensor history.

If the farmer starts a new culture, provide an explicit workflow to start a new culture with a new start date, rather than silently resetting the completed record.

Keep the Harvest button separate from the existing manual feeding controls. It must not activate or deactivate any pumps, relays, or feeder hardware unless the project already has an explicitly designed and safe harvesting-control feature.

6. Firebase and ESP32 Integration

Preserve the existing Firebase paths:

/growth/start_date

/ShrimpHub/temperature

/ShrimpHub/tds

/ShrimpHub/turbidity

/ShrimpHub/history

/settings

Inspect the existing schema before creating or changing any additional paths.

If additional fields are needed for sensor activation, harvest readiness, or completed-culture records:

Use consistent field names and data types.

Preserve compatibility with the existing Android app and ESP32 firmware.

Document every new field and its purpose.

Do not rename or delete existing Firebase nodes without a compatibility plan.

Ensure the model receives real, valid data and does not mistake inactive sensor defaults for genuine readings.

If the machine-learning model currently runs on a server or another component, inspect how it is called and integrate it through the existing supported architecture. Do not claim that Firebase itself performs machine learning unless that capability is actually implemented.