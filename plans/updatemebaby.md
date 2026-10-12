Codex Prompt: Add Reset Culture Button to PrawnHub Growth Screen

Modify the existing Growth screen in my PrawnHub Android application. Add a Reset Culture button that allows the farmer to reset the current culture and set a new culture start date.

Inspect the existing GrowthActivity, activity_growth.xml, Firebase structure, growth calculations, Growth Trend, harvest prediction, and Harvest workflow before implementing the changes.

Important: Modify the existing implementation. Do not create duplicate screens, Firebase paths, or navigation components. Preserve existing sensor monitoring, pond settings, feeding controls, role permissions, and dark mode.

1. Add a Reset Culture Button

Add a clearly visible Reset Culture button to the Growth screen.

Place it near the existing Culture Information or Set Start Date section.

Style it consistently with the current application UI.

When tapped, display a confirmation dialog before resetting anything.

Suggested confirmation message:

Are you sure you want to reset the current culture? The active growth data and harvest prediction will be cleared, and you will need to set a new culture start date.

Buttons:

Cancel

Reset Culture

Do not reset anything if the farmer selects Cancel.

2. Reset the Active Culture Information

After the farmer confirms the reset, clear or deactivate the current culture's active tracking state.

Reset the following UI fields to their default states:

Culture Start Date: Not Set

Culture Days: --

Growth Progress: Not Started

Growth Prediction: Not Started

Harvest Date: Unavailable

Growth Stage: Not Started

Harvest Readiness: Not Available

Growth Analysis: Prompt the farmer to set a new culture start date

Growth Trend: Empty state, with no line chart

Harvest Button: Disabled or hidden until a new culture has started and valid harvesting criteria are met

Do not display old culture predictions, percentages, chart points, or harvest readiness after the reset.

3. Firebase Reset Behavior

Inspect the existing Firebase schema before changing data.

Reset the active culture's /growth/start_date value using a data type and approach compatible with the existing implementation.

Clear or reset other active-culture calculation fields only if they belong to the current culture and are safe to reset.

Ensure the Growth screen reloads the reset state from Firebase, rather than only changing local text labels.

If the reset operation fails, show an error message and retain the last confirmed state. Do not falsely tell the user that the reset succeeded.

Show a success message only after Firebase confirms the reset operation.

Do not blindly delete the entire /growth node because it may contain settings, predictions, or historical information unrelated to the active culture.

4. Preserve Historical Data

Resetting a culture must not permanently delete historical sensor data or completed-culture records.

Do not delete /ShrimpHub/history.

Do not delete past harvest records or previous culture reports.

Do not delete pond settings, sensor calibration values, user accounts, or feeding configuration.

If the project stores culture-specific records, close or deactivate the current active culture and preserve its historical information where applicable.

If the current culture has already been marked as harvested, do not silently erase its completed record. Require an appropriate new-culture workflow instead.

If the project does not yet distinguish active cultures from completed cultures, inspect the schema and implement a compatible approach that preserves previous records.

5. Require a New Start Date

After the reset succeeds:

Return the Growth screen to the initial not-started state.

Keep the Set Start Date button available.

Require the farmer to select and confirm the actual start date of the new culture.

Do not automatically reuse the previous culture's start date.

Do not start culture-day counting until the new valid date has been saved.

Do not display a harvest date or growth prediction until the new culture has the required data.

Ensure the new culture's Growth Trend uses only valid sensor-history records recorded from the new start date onward.

If a new start date is saved, update the current culture tracking state and calculate its information from the new culture's start date and valid data.

6. Refresh Related Growth Features

After a successful reset, refresh all related Growth screen components:

Growth Prediction

Culture Information

Sensor Conditions

Growth Analysis

Growth Trend

Harvest Date

Harvest Readiness

Harvest Button

Continue showing live Sensor Conditions from Firebase. Do not reset real sensor readings, calibration settings, or the actual pond condition to zero or Normal just because the culture is reset.

The Dashboard's live sensor readings and pond status must remain unchanged by a culture reset.

Remove any old culture-specific listeners or cached calculated values that could repopulate the screen with the previous culture's data. Keep listeners required for live sensor monitoring.

7. Prevent Accidental or Duplicate Resets

Require explicit confirmation.

Prevent multiple reset requests while one is already processing.

Handle Firebase errors and retry safely.

Avoid duplicate Firebase listeners and conflicting updates.

Ensure the reset works after leaving and reopening the Growth screen.

Keep the operation compatible with the current Firebase security rules and Android architecture.