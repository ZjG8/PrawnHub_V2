Codex Prompt: Update PrawnHub Farmer Growth Dashboard

You are working on my existing Android Studio project, PrawnHub: Smart Aquaculture Monitoring and Control System for Indoor Prawn Farming.

Update the existing Farmer Growth screen (GrowthActivity and activity_growth.xml, or the equivalent files you find in the project). Follow the current project structure and modify existing files instead of creating duplicate screens.

1. Display the Culture Start Date

Add a clear Culture Start Date field in the Culture Information section of the Growth Dashboard.

Before a culture start date is saved

Display Culture Not Started.

Show a message prompting the farmer to set the actual date when prawn culture began.

Do not show fabricated culture days, growth percentages, growth stages, or harvest dates.

Keep the Set Start Date button visible and functional.

When the farmer sets the date

Open a date picker and require confirmation before saving.

Save the confirmed date to the existing Firebase path /growth/start_date.

Inspect the current Firebase schema and use a compatible date format.

Load the saved date whenever the Growth screen opens.

Display the date in a readable format, such as 12 October 2026.

Calculate culture days consistently using the saved date and the current date.

Validate the selected date and reject unsupported future dates.

If a start date already exists, ask for confirmation before changing it.

Recalculate the culture-day count and harvest estimate whenever the saved start date changes.

2. Add an Estimated Prawn Harvest Date

Add a prominent Estimated Harvest Date field to the Culture Information section.

The purpose is to help the farmer understand when the prawns may be ready for harvesting based on the culture start date, recorded pond conditions, and the project's existing growth logic.

Data to inspect and use

Read the actual data from the project's Firebase structure:

/growth/start_date — actual culture start date.

/ShrimpHub/temperature — current water temperature.

/ShrimpHub/tds — current TDS/salinity reading.

/ShrimpHub/turbidity — current turbidity reading.

/ShrimpHub/history — historical sensor readings recorded after the start date.

/settings — configured sensor thresholds and any existing culture-duration or harvest-target settings.

Inspect the existing Android and ESP32 code to determine the actual history record structure, timestamps, field names, and any existing growth or culture-duration calculations before implementing the estimate.

Harvest estimation rules

Use the saved culture start date as the baseline.

Analyze valid sensor readings recorded from the culture start date onward.

Consider both current sensor conditions and historical conditions instead of relying on a single reading.

Reuse an existing, documented growth or harvest estimation method if the project already has one.

If a configurable target culture duration or harvest-day setting exists, use it as the baseline and explain how the recorded pond conditions affect the estimate, if the existing logic supports that calculation.

Do not invent a biological growth formula, species-specific growth rate, biomass, or harvest-readiness threshold.

Do not assume sensor readings alone can accurately determine the exact harvest date.

If there is no validated estimation model or configured target culture duration, display:

Harvest estimate unavailable. Configure a target culture duration or a validated growth model.

If historical sensor records are missing, incomplete, invalid, or insufficient, explain that more valid data is required.

Never generate an arbitrary harvest date just to fill the UI.

Clearly label the result as an estimate, not a guaranteed harvest date or a measured biological result.

When supported by the existing logic, include a short explanation such as:

Estimated from the configured culture duration and recorded pond conditions. Actual harvest timing may vary.

Update the estimate when new valid sensor-history records arrive, settings change, or the culture start date changes.

3. Update the Growth Trend Section

The Growth Trend must not display a line chart before the farmer starts the culture.

If no start date is saved

Hide the line chart completely.

Do not render a chart containing zero values, placeholder values, or sample points.

Display an empty-state message:

Growth trend will appear after you set the culture start date and valid sensor history is recorded.

If a start date is saved

Read actual sensor history from /ShrimpHub/history.

Exclude all records timestamped before the saved culture start date.

Display the trend only when enough valid post-start records are available.

If no valid records exist, display:

No sensor history recorded since the culture start date. More data is required to display the trend.

If there are too few records for a meaningful chart, explain that additional readings are needed.

Use actual timestamps and recorded values only.

Handle missing fields, invalid readings, duplicate timestamps, out-of-order records, and Firebase errors.

If the chart represents a calculated sensor-condition score, label it Sensor Condition Trend, not actual prawn growth or biomass.

Do not imply that water sensor readings directly measure prawn size or weight.

4. Keep Growth Prediction and Culture Information Consistent

Update the existing Growth Prediction and Culture Information sections.

Before culture starts:

Growth prediction: Not Started.

Culture days: --.

Growth progress: --.

Growth stage: Not Started.

Estimated harvest date: Unavailable.

Growth analysis: prompt the farmer to save the actual culture start date.

Growth trend: empty state, with no chart.

After culture starts:

Display the saved culture start date.

Calculate culture days from that date.

Read current sensor readings from Firebase.

Use valid post-start history and configured thresholds for supported condition calculations.

Display a growth stage, progress indicator, or harvest estimate only when the existing logic or a documented, defensible calculation supports it.

Distinguish live sensor measurements from calculated indicators and estimated outcomes.

Do not show fake growth percentages or present a sensor-condition score as measured prawn growth.

5. Synchronize Sensor Conditions with the Farmer Dashboard

The Growth screen must use the same live sensor values and threshold logic as the Farmer Dashboard.

Show:

Temperature and its condition.

Salinity/TDS and its condition.

Turbidity and its condition.

Use the actual Firebase readings and configured settings. Do not hardcode values or create separate, conflicting sensor-status rules.

6. Firebase and Code Requirements

Preserve the existing /ShrimpHub, /ShrimpHub/history, /growth/start_date, and settings structures unless inspection identifies a necessary, compatibility-safe change.

Inspect the existing project before assuming data types, node names, or history formats.

Reuse existing models, repository classes, Firebase listeners, and growth logic where practical.

Avoid duplicate listeners and clean them up according to the Activity lifecycle.

Ensure date calculations and history filtering do not shift dates because of time-zone conversions.

Handle loading states, empty data, Firebase disconnections, permission errors, and malformed records.

Preserve farmer/admin role restrictions and existing navigation.

Keep the layout compatible with the existing app design and dark mode.

Do not break Dashboard, History, Growth, Settings, or feeding controls.