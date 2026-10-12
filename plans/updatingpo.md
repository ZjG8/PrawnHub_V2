# PrawnHub UI and Functionality Update Prompt

Update the existing PrawnHub Android Studio project. First inspect the current activities, XML layouts, navigation, Firebase data structure, and existing report/export code. Modify the existing implementation instead of creating duplicate screens or replacing working features unnecessarily.

Use the current system UI diagram and existing Arduino/Firebase structure as the reference. Keep the current PrawnHub design, naming conventions, role permissions, and light/dark theme support.

## 1. Farmer Growth Screen

Update the existing **Growth** screen with these rules:

- **Before farming starts:** Growth Prediction and Culture Information must show a clear default/not-started state. Do not show invented growth percentages, health scores, culture days, growth stage, harvest estimates, or readiness values.
- **Start Date:** Make the existing **Set Start Date** action fully functional. Let the farmer choose and confirm the actual farming/culture start date, save it persistently using the project's existing Firebase structure, and reload it correctly after navigating away or reopening the app.
- Do not start culture-day counting or growth prediction until a valid start date has been saved. Validate the selected date and handle missing or invalid dates safely.
- **Sensor Conditions:** Sync these values with the same live sensor data and status logic shown on the Farmer Dashboard. Avoid separate hardcoded values or inconsistent readings.
- **Growth Analysis:** Before the start date is set, show a neutral “Farming not started”/empty state. Afterward, generate the analysis from actual available sensor readings and history.
- **Growth Trend:** Before farming starts, show no chart statistics or fabricated points; display a clear empty state instead. After the start date is set, use actual history records from that date onward. Refresh when new valid records arrive and handle insufficient or missing data.
- Base all displayed estimates and analysis on real available Firebase data. Do not hardcode example values or claim that estimates are measured biomass. If the available data is insufficient, explain that more data is needed rather than inventing a prediction.
- Preserve the existing Growth screen navigation and styling.

## 2. Farmer Settings — Feeding Automation

Make **Feeding Automation / Auto Feed** functional.

- When Auto Feed is enabled, automatically trigger feeding according to the configured feed time and interval in hours.
- Read and persist the enabled state, feed time, and interval through the project's existing Firebase configuration so the schedule remains consistent after the app/ESP32 restarts.
- Coordinate the Android settings with the ESP32 feeder implementation. Inspect the existing `/ShrimpHub/feeder` paths and Arduino code before deciding where schedule fields should live; reuse existing paths where appropriate and document any new fields.
- Ensure the ESP32 executes scheduled feeds even when the app is closed, as long as the ESP32 is powered, connected, and able to access Firebase. The Android UI should configure the schedule, not be required to remain open to run it.
- Prevent repeated or duplicate feeding for the same scheduled occurrence. Track the last successful scheduled feed or use an equivalent reliable safeguard.
- Keep **Feed Now** working separately from Auto Feed.
- Update the UI to show whether automation is enabled, the configured time/interval, and the next scheduled feed when that can be calculated reliably.
- Validate the time and interval, handle missing configuration safely, and do not automatically feed when automation is disabled.
- Maintain the existing manual feeding status, feed amount, feed count, and last-feed-time behavior.

## 3. Admin Dashboard Navigation

Update the existing Admin Dashboard to include navigation/actions for:

- **History**
- **Growth**
- **Generate Farm Reports**
- **System Settings**
- **Manage User Accounts**, only if this feature already exists and is part of the current admin implementation
- **Logout**

Remove **Recent Alerts** from the Admin Dashboard and admin navigation. Remove its card/action and related navigation entry without breaking other screens.

History and Growth must open the appropriate existing screens and show real Firebase data. Apply appropriate admin navigation/back behavior and preserve role-based access. Avoid creating duplicate versions of screens if existing screens can be reused safely.

## 4. Generate Farm Reports — PDF and Excel

Make the existing **Generate Farm Reports** feature fully functional.

- Provide start date and end date selection.
- Validate that both dates are valid and that the start date is not after the end date.
- Query actual Firebase history records within the selected date range, using the record timestamps and the correct timezone handling.
- Show a report preview or useful summary of the records that will be exported.
- Generate a real, readable **PDF** report and a valid, Excel-compatible **`.xlsx`** file from the selected date range.
- Include the selected date range, generation date/time, and available sensor records such as temperature, TDS/salinity value, turbidity, water level, and timestamps. Include other fields only if they actually exist in the data.
- Handle empty ranges, missing fields, Firebase errors, and export errors with clear messages. Never generate a report that silently contains fabricated or out-of-range records.
- Let the admin save, open, or share the generated files using appropriate Android storage/file-sharing APIs for the project's target Android version.
- Keep PDF and Excel exports consistent with the report preview and selected date range.

## 5. Firebase, Compatibility, and Quality Checks

- Preserve compatibility with the current ESP32 data paths, especially `/ShrimpHub`, `/ShrimpHub/history`, `/ShrimpHub/control`, and `/ShrimpHub/feeder`.
- Inspect the actual Firebase schema and current project files before implementing paths or assumptions. Do not rename existing nodes without a clear compatibility reason.
- Keep farmer/admin permissions and existing Dashboard, History, Growth, Settings, controls, and feeder behavior working.
- Ensure all relevant screens support dark mode correctly.
- Use the project's existing architecture and dependencies where possible; add a dependency only if necessary.
- Do not hardcode sensor values, report records, growth values, or feeding schedules.
- Build/compile the Android project after changes and fix all compile errors. Run available tests or checks.
- In the final summary, list the files changed, explain how the start date and scheduled feeding work, identify any Firebase fields added, and report the build/test result. Mention any requirement that could not be verified.

## Acceptance Checklist

- [ ] Growth remains in a not-started/default state until the farmer saves a valid culture start date.
- [ ] Culture days begin from the saved start date, not before it.
- [ ] Growth sensor conditions match the live Dashboard readings.
- [ ] Growth analysis and trend use actual data and show appropriate empty states.
- [ ] Auto Feed runs from the saved schedule on the ESP32 without requiring the Android app to stay open.
- [ ] No duplicate scheduled feed occurs for the same scheduled occurrence.
- [ ] Admin can open History and Growth.
- [ ] Recent Alerts is removed from the Admin Dashboard/navigation.
- [ ] PDF and `.xlsx` reports contain only actual records within the selected date range.
- [ ] Existing features remain functional, and the project builds successfully.
