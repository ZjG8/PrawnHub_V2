PrawnHub History Screen Redesign and Firebase Sync Fix

Project Context

I am working on my existing PrawnHub Android application using Java and XML in Android Studio.

The relevant files are:

HistoryActivity.java

activity_history.xml

The history data is stored in Firebase Realtime Database at:

/ShrimpHub/history

The ESP32 currently saves grouped sensor history records containing:

timestamp

temperature

tds

turbidity

waterLevel

The app may also encounter older history records containing:

timestamp

param_type

rec_val

status

Main Objectives

1. Fix Firebase History Synchronization

The history list is currently not displaying the expected sensor records. Investigate and fix the actual cause instead of only changing the UI.

Requirements:

Inspect HistoryActivity.java, activity_history.xml, Firebase initialization, database references, and the existing ESP32 history-writing code if available.

Verify that the app reads from the correct Firebase path: /ShrimpHub/history.

Confirm that the Firebase listener is attached correctly and receives child records.

Check how Firebase records are parsed, especially grouped records containing multiple sensor values.

Handle Firebase numeric values safely, including Long, Double, and other numeric representations.

Ensure that the timestamp is parsed correctly. The ESP32 stores now.unixtime(), which is a Unix timestamp in seconds. Convert it correctly to milliseconds when using Java date/time APIs.

Check whether the selected date range, timestamp units, sensor filter, or record validation is accidentally excluding valid records.

Support both grouped records and legacy records using param_type, rec_val, and status.

Sort the history list from newest to oldest.

Make sure the RecyclerView adapter updates when new data arrives.

Show an appropriate loading indicator while fetching data, an empty-state message when no matching records exist, and a useful error message if Firebase access fails.

Remove Firebase listeners appropriately when the activity is destroyed to avoid duplicate listeners or memory leaks.

Check Firebase read permissions and database errors if the listener cannot retrieve records.

Do not fabricate history records or use hardcoded sample values to make the list appear populated.

2. Change the Date and Time Format to a 12-Hour Clock

Update the Start Date & Time and End Date & Time controls to display a 12-hour clock with AM/PM.

Use this display format:

MM/dd/yyyy hh:mm AM/PM

Examples:

10/12/2026 08:30 AM

10/12/2026 04:45 PM

Requirements:

Use a 12-hour clock instead of a 24-hour clock.

Include AM/PM so users can distinguish morning and afternoon.

Use Android date and time pickers that support 12-hour time selection.

Preserve the correct underlying date and time values for filtering.

Convert the selected date/time to the appropriate Unix timestamp unit when comparing against Firebase records.

Keep the default date range as the previous seven days through the current time.

Keep the Apply Filter button functional.

Validate that the start date/time is not later than the end date/time.

Make sure records at the beginning and end of the selected range are handled correctly.

3. Change the History Screen Layout Order

Rearrange the existing screen to follow this order:

A. Header

Back button

History title

B. Date & Time Range

Start Date & Time

End Date & Time

Apply Filter button

All Sensors dropdown

C. History List

Display the timestamp, sensor name, value, and status where available.

Show the newest records first.

Display a clear empty state when no records match the selected range or sensor filter.

D. Graph Analytics

Temperature Trend

Salinity/TDS Trend

Turbidity Trend

Water Level Trend

The History List must appear immediately below the date and time filter section. Graph Analytics must appear below the History List.

Keep the existing chart library and working chart functionality where possible.

4. Remove the Firebase Verification Section

Completely remove the Firebase Verification panel from the History screen.

Remove its related UI elements and unused Java references, such as:

Latest timestamp

Temperature verification value

TDS verification value

Turbidity verification value

Water level verification value

Do not remove Firebase itself or the Firebase listener used to retrieve history records. Firebase must continue powering the history list and graphs.

5. Preserve Existing Features

Keep the existing Java and XML architecture. Do not migrate to Flutter.

Preserve the current Firebase database schema and ESP32 integration.

Keep the existing sensor filter and date range filtering functional.

Keep chart data synchronized with the same filtered history records used by the list.

Ensure All Sensors displays all available sensor records and all four charts.

When an individual sensor is selected, show the corresponding history entries and chart while hiding or appropriately handling unrelated charts.

Use clear sensor labels and units. Display temperature in °C, salinity/TDS using the existing verified unit, turbidity in the appropriate calibrated unit, and water level in the unit actually produced by the ESP32.

Do not change the ESP32 firmware or Firebase data format unless a genuine compatibility issue requires it.

Implementation Instructions

Inspect the existing project files and identify the actual reason history records are not displaying.

Explain the cause briefly before implementing the fix.

Update HistoryActivity.java and activity_history.xml, along with any directly related adapter, drawable, or resource files that need changes.

Remove obsolete Firebase Verification code and references.

Implement the new layout order and 12-hour date/time format.

Build the project and fix any compilation errors introduced by the changes.

Review Firebase parsing, timestamp conversion, filtering, list rendering, and chart rendering for consistency.

Summarize the files changed and explain how to test the fix.

Important: Do not rewrite the entire application or make unrelated changes. Preserve working code wherever possible, and do not claim Firebase synchronization is fixed unless the code and available test results support that conclusion.